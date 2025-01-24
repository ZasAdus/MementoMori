package com.example.mementomori.bazyDanych;

import com.example.mementomori.User;

import java.sql.*;

public class BazaMojeKonto {
    private static final String SQLITE_PREFIX = "jdbc:sqlite:";
    private static String URL = SQLITE_PREFIX + "data/uzytkownicy.db";

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

    public static User getUserData(String login) {
        String sql = "SELECT id, login, imie, nazwisko, email, nrTelefonu FROM dane_uzytkownikow WHERE login = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("email"),
                        rs.getString("nrTelefonu")
                );
            } else {
                System.out.println("Użytkownik o loginie '" + login + "' nie został znaleziony.");
            }
        } catch (SQLException e) {
            System.out.println("Błąd podczas pobierania danych użytkownika.");
            e.printStackTrace();
        }
        return null;
    }

    public static void updateUserData(String login, String email, String nrTelefonu) {
        String sql = "UPDATE dane_uzytkownikow SET email = ?, nrTelefonu = ? WHERE login = ?";
        try (Connection conn = BazaRejestracja.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, nrTelefonu);
            pstmt.setString(3, login);
            pstmt.executeUpdate();
            System.out.println("Dane użytkownika '" + login + "' zostały zaktualizowane.");
        } catch (SQLException e) {
            System.out.println("Błąd podczas aktualizacji danych użytkownika.");
            e.printStackTrace();
        }
    }

}

