package bazyDanych;

import java.sql.*;

public class BazaLogowania {
    private static final String DB_PATH = "C:\\Users\\dawid\\IdeaProjects\\MementoMori\\src\\main\\java\\bazyDanych\\uzytkownicy.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("Połączono z bazą danych SQLite.");
        } catch (SQLException e) {
            System.out.println("Błąd połączenia z bazą danych.");
            e.printStackTrace();
        }
        return conn;
    }

    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS uzytkownicy (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "login TEXT UNIQUE NOT NULL, " +
                "haslo TEXT NOT NULL);";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela 'uzytkownicy' została utworzona lub już istnieje.");
        } catch (SQLException e) {
            System.out.println("Błąd podczas tworzenia tabeli.");
            e.printStackTrace();
        }
    }

    public static boolean userExists(String login) {
        String sql = "SELECT login FROM uzytkownicy WHERE login = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas sprawdzania, czy użytkownik istnieje.");
            e.printStackTrace();
        }
        return false;
    }

    public static void insertUser(String login, String haslo) {
        if (userExists(login)) {
            System.out.println("Użytkownik o loginie '" + login + "' już istnieje w bazie danych.");
            return;
        }

        String sql = "INSERT INTO uzytkownicy (login, haslo) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, haslo);
            pstmt.executeUpdate();
            System.out.println("Użytkownik '" + login + "' został dodany do bazy danych.");
        } catch (SQLException e) {
            System.out.println("Błąd podczas dodawania użytkownika.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        createTable();
        insertUser("admin", "admin123");


    }
}