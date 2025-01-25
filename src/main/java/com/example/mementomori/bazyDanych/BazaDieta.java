package com.example.mementomori.bazyDanych;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BazaDieta {

    private static final String SQLITE_PREFIX = "jdbc:sqlite:";
    private static String URL = SQLITE_PREFIX + "data/dieta.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            //conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println("Błąd połączenia z bazą danych.");
            e.printStackTrace();
        }
        return conn;
    }

    public static void initTable() {
        String sql = """
        CREATE TABLE IF NOT EXISTS dieta (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_login TEXT NOT NULL,
            date TEXT NOT NULL,
            kal INTEGER DEFAULT 0,
            goal INTEGER DEFAULT 2000,
            UNIQUE(user_login, date)
        )
        """;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Błąd podczas tworzenia tabeli kroki: " + e.getMessage());
        }
    }

    public static void sprawdzLubUtworzDzien(String login) {
        String sqlCheck = "SELECT COUNT(*) FROM dieta WHERE user_login = ? AND date = ?";
        String sqlInsert = "INSERT INTO dieta (user_login, date, kal, goal) VALUES (?, ?, 0, ?)";
        String sqlPreviousGoal = "SELECT goal FROM dieta WHERE user_login = ? ORDER BY date DESC LIMIT 1";

        try (Connection conn = connect()) {
            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheck)) {
                pstmtCheck.setString(1, login);
                pstmtCheck.setString(2, LocalDate.now().toString());
                ResultSet rs = pstmtCheck.executeQuery();

                if (rs.next() && rs.getInt(1) == 0) {
                    int poprzedniCel = 6000;
                    try (PreparedStatement pstmtGoal = conn.prepareStatement(sqlPreviousGoal)) {
                        pstmtGoal.setString(1, login);
                        ResultSet rsGoal = pstmtGoal.executeQuery();
                        if (rsGoal.next()) {
                            poprzedniCel = rsGoal.getInt("goal");
                        }
                    }

                    try (PreparedStatement pstmtInsert = conn.prepareStatement(sqlInsert)) {
                        pstmtInsert.setString(1, login);
                        pstmtInsert.setString(2, LocalDate.now().toString());
                        pstmtInsert.setInt(3, poprzedniCel);
                        pstmtInsert.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas sprawdzania lub tworzenia dnia: " + e.getMessage());
        }
    }

    public static void zaktualizujDaneDieta(String login, int kal, int goal) {
        String sql = "UPDATE dieta SET kal = ?, goal = ? WHERE user_login = ? AND date = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, kal);
            pstmt.setInt(2, goal);
            pstmt.setString(3, login);
            pstmt.setString(4, LocalDate.now().toString());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Błąd podczas aktualizacji danych kroków: " + e.getMessage());
        }
    }


    public static int[] pobierzDaneDieta(String login) {
        String sql = "SELECT kal, goal FROM dieta WHERE user_login = ? AND date = ?";
        int[] dane = new int[]{0, 6000};

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            pstmt.setString(2, LocalDate.now().toString()); // Pobieramy dane dla bieżącego dnia
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                dane[0] = rs.getInt("kal");
                dane[1] = rs.getInt("goal");
            }

        } catch (SQLException e) {
            System.out.println("Błąd podczas pobierania danych kroków: " + e.getMessage());
        }

        return dane;
    }

    public static Map<LocalDate, int[]> pobierzKalICeleWTygodniu(String login, LocalDate weekStart) {
        Map<LocalDate, int[]> weeklyData = new HashMap<>();
        String sql = "SELECT date, kal, goal FROM dieta WHERE user_login = ? AND date BETWEEN ? AND ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            pstmt.setString(2, weekStart.toString());
            pstmt.setString(3, weekStart.plusDays(6).toString());

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("date"));
                int kal = rs.getInt("kal");
                int goal = rs.getInt("goal");
                weeklyData.put(date, new int[]{kal, goal});
            }

        } catch (SQLException e) {
            System.out.println("Błąd podczas pobierania danych tygodniowych: " + e.getMessage());
        }

        return weeklyData;
    }



}
