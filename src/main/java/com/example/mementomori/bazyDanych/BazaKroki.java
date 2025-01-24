package com.example.mementomori.bazyDanych;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class BazaKroki {

    private static final String SQLITE_PREFIX = "jdbc:sqlite:";
    private static String URL = SQLITE_PREFIX + "data/kroki.db";

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
        CREATE TABLE IF NOT EXISTS kroki (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_login TEXT NOT NULL,
            date TEXT NOT NULL,
            steps INTEGER DEFAULT 0,
            goal INTEGER DEFAULT 6000,
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
        String sqlCheck = "SELECT COUNT(*) FROM kroki WHERE user_login = ? AND date = ?";
        String sqlInsert = "INSERT INTO kroki (user_login, date, steps, goal) VALUES (?, ?, 0, ?)";
        String sqlPreviousGoal = "SELECT goal FROM kroki WHERE user_login = ? ORDER BY date DESC LIMIT 1";

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

    public static void zaktualizujDaneKroki(String login, int steps, int goal) {
        String sql = "UPDATE kroki SET steps = ?, goal = ? WHERE user_login = ? AND date = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, steps);
            pstmt.setInt(2, goal);
            pstmt.setString(3, login);
            pstmt.setString(4, LocalDate.now().toString());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Błąd podczas aktualizacji danych kroków: " + e.getMessage());
        }
    }


    public static int[] pobierzDaneKroki(String login) {
        String sql = "SELECT steps, goal FROM kroki WHERE user_login = ? AND date = ?";
        int[] dane = new int[]{0, 6000};

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            pstmt.setString(2, LocalDate.now().toString()); // Pobieramy dane dla bieżącego dnia
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                dane[0] = rs.getInt("steps");
                dane[1] = rs.getInt("goal");
            }

        } catch (SQLException e) {
            System.out.println("Błąd podczas pobierania danych kroków: " + e.getMessage());
        }

        return dane;
    }

    public static Map<LocalDate, int[]> pobierzKrokiICeleWTygodniu(String login, LocalDate weekStart) {
        Map<LocalDate, int[]> weeklyData = new HashMap<>();
        String sql = "SELECT date, steps, goal FROM kroki WHERE user_login = ? AND date BETWEEN ? AND ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            pstmt.setString(2, weekStart.toString());
            pstmt.setString(3, weekStart.plusDays(6).toString());

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                LocalDate date = LocalDate.parse(rs.getString("date"));
                int steps = rs.getInt("steps");
                int goal = rs.getInt("goal");
                weeklyData.put(date, new int[]{steps, goal});
            }

        } catch (SQLException e) {
            System.out.println("Błąd podczas pobierania danych tygodniowych: " + e.getMessage());
        }

        return weeklyData;
    }



}
