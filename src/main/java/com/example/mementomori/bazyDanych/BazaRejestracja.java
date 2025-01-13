package com.example.mementomori.bazyDanych;

import java.sql.*;

public class BazaRejestracja {
    private static final String DB_PATH = "data\\uzytkownicy.db";
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

    public static void initTable() {
        String[] createTableStatements = {
                "CREATE TABLE IF NOT EXISTS uzytkownicy (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT UNIQUE NOT NULL, haslo TEXT NOT NULL)",

                "CREATE TABLE IF NOT EXISTS dane_uzytkownikow (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "login TEXT UNIQUE NOT NULL, " +
                        "imie TEXT, " +
                        "nazwisko TEXT, " +
                        "email TEXT, " +
                        "nrTelefonu TEXT, " +
                        "FOREIGN KEY (login) REFERENCES uzytkownicy(login))",

                "CREATE TABLE IF NOT EXISTS doctors (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "login TEXT UNIQUE NOT NULL, " +
                        "specjalizacja TEXT NOT NULL, " +
                        "adresPrzychodni TEXT NOT NULL, " +
                        "FOREIGN KEY (login) REFERENCES uzytkownicy(login))"
        };

        try (Connection conn = connect()) {
            Statement stmt = conn.createStatement();
            for (String createStatement : createTableStatements) {
                stmt.execute(createStatement);
            }
            System.out.println("Tabele zostały utworzone pomyślnie.");
        } catch (SQLException e) {
            System.err.println("Błąd podczas tworzenia tabel: ");
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

    public static boolean isPasswordCorrect(String login, String haslo) {
        String sql = "SELECT haslo FROM uzytkownicy WHERE login = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("haslo");
                return storedPassword.equals(haslo);
            } else {
                System.out.println("Użytkownik o loginie '" + login + "' nie istnieje.");
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas sprawdzania hasła.");
            e.printStackTrace();
        }
        return false;
    }


    public static void insertLoginHaslo(String login, String haslo) {
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

    public static void insertDaneOsobowe(String login, String imie, String nazwisko, String email, String telefon) {
        String sql = "INSERT INTO dane_uzytkownikow (login, imie, nazwisko, email, nrTelefonu) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, imie);
            pstmt.setString(3, nazwisko);
            pstmt.setString(4, email);
            pstmt.setString(5, telefon);
            pstmt.executeUpdate();
            System.out.println("Użytkownikowi '" + login + "' zostały dodany dane osobowe do bazy danych.");
        } catch (SQLException e) {
            System.out.println("Błąd podczas dodawania danych osobowych.");
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        BazaRejestracja.connect();
        BazaRejestracja.insertDaneOsobowe("admin", "Jan", "Kowalski", "jan.kowalski@example.com", "123456789");
    }

    public static void insertDaneZawodowe(String login, String specjalizacja, String adresPrzychodni) {
        String sql = "INSERT INTO doctors (login, specjalizacja, adresPrzychodni) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.setString(2, specjalizacja);
            pstmt.setString(3, adresPrzychodni);
            pstmt.executeUpdate();
            System.out.println("Dane zawodowe zostały dodane dla użytkownika '" + login + "'");
        } catch (SQLException e) {
            System.out.println("Błąd podczas dodawania danych zawodowych.");
            e.printStackTrace();
        }
    }

    public static boolean isDoctor(String login) {
        String sql = "SELECT login FROM doctors WHERE login = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // jeśli znaleziono wpis w tabeli doctors, to jest to lekarz
        } catch (SQLException e) {
            System.out.println("Błąd podczas sprawdzania typu użytkownika.");
            e.printStackTrace();
        }
        return false;
    }

    public static int idDoctor(String login) {
        String sql = "SELECT id FROM doctors WHERE login = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) { // Sprawdzenie, czy jest wynik
                return rs.getInt("id"); // Pobranie wartości "id"
            } else {
                System.out.println("Nie znaleziono lekarza o podanym loginie.");
                return -1; // Zwraca -1, jeśli użytkownik nie istnieje
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas sprawdzania typu użytkownika.");
            e.printStackTrace();
        }
        return -1; // Zwraca -1 w przypadku błędu
    }

}