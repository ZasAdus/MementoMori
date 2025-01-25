package com.example.mementomori.bazyDanych;

import com.example.mementomori.MementoMori;
import com.example.mementomori.TimeRange;

import java.sql.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class BazaHarmonogram {
    private static final String DB_NAME = "data\\wizyty.db";
    private static final String URL = "jdbc:sqlite:" + DB_NAME;

    public BazaHarmonogram() {
        createNewDatabase();
        createTables();
    }

    private void createNewDatabase() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
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
                dzien TEXT NOT NULL,
                miesiac TEXT NOT NULL,
                rok TEXT NOT NULL,
                godzina_start TEXT NOT NULL,
                godzina_koniec TEXT NOT NULL,
                UNIQUE(id_lekarza, dzien_tygodnia, dzien, miesiac, rok)
            );
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void zapiszHarmonogram(String dzienTygodnia, String dzien, String miesiac, String rok, TimeRange czasPracy) {
        String sql = """
            INSERT OR REPLACE INTO harmonogram_lekarzy(id_lekarza, dzien_tygodnia, dzien, miesiac, rok, godzina_start, godzina_koniec) 
            VALUES(?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, MementoMori.idDoctor);
            pstmt.setString(2, dzienTygodnia);
            pstmt.setString(3, dzien);
            pstmt.setString(4, miesiac);
            pstmt.setString(5, rok);
            pstmt.setString(6, czasPracy.getStartTime().toString());
            pstmt.setString(7, czasPracy.getEndTime().toString());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Map<String, TimeRange> pobierzHarmonogram() {
        String sql = "SELECT dzien_tygodnia, dzien, miesiac, rok, godzina_start, godzina_koniec FROM harmonogram_lekarzy WHERE id_lekarza = ?";
        Map<String, TimeRange> harmonogram = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, MementoMori.idDoctor);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String dzienTygodnia = rs.getString("dzien_tygodnia");
                    String dzien = rs.getString("dzien");
                    String miesiac = rs.getString("miesiac");
                    String rok = rs.getString("rok");
                    String start = rs.getString("godzina_start");
                    String koniec = rs.getString("godzina_koniec");

                    String mapKey = dzienTygodnia + ", " + dzien + "." + miesiac + "." + rok;
                    LocalTime startTime = LocalTime.parse(start);
                    LocalTime koniecTime = LocalTime.parse(koniec);
                    harmonogram.put(mapKey, new TimeRange(startTime, koniecTime));
                }
            }

        } catch (SQLException e) {
            System.out.println("Błąd przy pobieraniu harmonogramu: " + e.getMessage());
            e.printStackTrace();
        }

        return harmonogram;
    }

    public boolean sprawdzCzyPrzyjmuje(String dzienTygodnia, String dzien, String miesiac, String rok, LocalTime godzina) {
        String sql = """
            SELECT COUNT(*) as count FROM harmonogram_lekarzy 
            WHERE id_lekarza = ? 
            AND dzien_tygodnia = ? 
            AND dzien = ? 
            AND miesiac = ? 
            AND rok = ? 
            AND time(?) >= time(godzina_start) 
            AND time(?) <= time(godzina_koniec)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, MementoMori.idDoctor);
            pstmt.setString(2, dzienTygodnia);
            pstmt.setString(3, dzien);
            pstmt.setString(4, miesiac);
            pstmt.setString(5, rok);
            pstmt.setString(6, godzina.toString());
            pstmt.setString(7, godzina.toString());

            ResultSet rs = pstmt.executeQuery();
            return rs.getInt("count") > 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void usunHarmonogramDnia(int idLekarza, String dzienTygodnia, String dzien, String miesiac, String rok) {
        String sql = "DELETE FROM harmonogram_lekarzy WHERE id_lekarza = ? AND dzien_tygodnia = ? AND dzien = ? AND miesiac = ? AND rok = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idLekarza);
            pstmt.setString(2, dzienTygodnia);
            pstmt.setString(3, dzien);
            pstmt.setString(4, miesiac);
            pstmt.setString(5, rok);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
