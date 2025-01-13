package com.example.mementomori.bazyDanych;

import com.example.mementomori.MementoMori;
import com.example.mementomori.TimeRange;

import java.sql.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class BazaWizytyLekarze {
    private static final String DB_NAME = "data\\harmonogram.db";
    private static final String URL = "jdbc:sqlite:" + DB_NAME;

    public BazaWizytyLekarze() {
        createNewDatabase();
        createTables();
    }

    private void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("Nazwa sterownika: " + meta.getDriverName());
                System.out.println("Baza danych została utworzona.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createTables() {
        String sql = """
            CREATE TABLE IF NOT EXISTS harmonogram_lekarzy (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_lekarza INTEGER NOT NULL,
                dzien_tygodnia TEXT NOT NULL,
                godzina_start TEXT NOT NULL,
                godzina_koniec TEXT NOT NULL,
                UNIQUE(id_lekarza, dzien_tygodnia)
            );
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela została utworzona lub już istnieje.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void zapiszHarmonogram(String dzienTygodnia, TimeRange czasPracy) {
        String sql = """
            INSERT OR REPLACE INTO harmonogram_lekarzy(id_lekarza, dzien_tygodnia, godzina_start, godzina_koniec) 
            VALUES(?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, MementoMori.idDoctor);
            pstmt.setString(2, dzienTygodnia);
            pstmt.setString(3, czasPracy.getStartTime().toString());
            pstmt.setString(4, czasPracy.getEndTime().toString());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Map<String, TimeRange> pobierzHarmonogram() {
        String sql = "SELECT dzien_tygodnia, godzina_start, godzina_koniec FROM harmonogram_lekarzy WHERE id_lekarza = ?";
        Map<String, TimeRange> harmonogram = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, MementoMori.idDoctor);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String dzien = rs.getString("dzien_tygodnia");
                    LocalTime start = LocalTime.parse(rs.getString("godzina_start"));
                    LocalTime koniec = LocalTime.parse(rs.getString("godzina_koniec"));
                    harmonogram.put(dzien, new TimeRange(start, koniec));
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return harmonogram;
    }

    public boolean sprawdzCzyPrzyjmuje(String dzienTygodnia, LocalTime godzina) {
        String sql = """
            SELECT COUNT(*) as count FROM harmonogram_lekarzy 
            WHERE id_lekarza = ? 
            AND dzien_tygodnia = ? 
            AND time(?) >= time(godzina_start) 
            AND time(?) <= time(godzina_koniec)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, MementoMori.idDoctor);
            pstmt.setString(2, dzienTygodnia);
            pstmt.setString(3, godzina.toString());
            pstmt.setString(4, godzina.toString());

            ResultSet rs = pstmt.executeQuery();
            return rs.getInt("count") > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void usunHarmonogramDnia(int idLekarza, String dzienTygodnia) {
        String sql = "DELETE FROM harmonogram_lekarzy WHERE id_lekarza = ? AND dzien_tygodnia = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idLekarza);
            pstmt.setString(2, dzienTygodnia);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
