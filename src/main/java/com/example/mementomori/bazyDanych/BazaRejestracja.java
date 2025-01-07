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
}