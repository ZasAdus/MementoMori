package com.example.mementomori.bazyDanych;

import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BazaLeki {
    public record LekiEntry(int id, String name, Time time) {
        public String getTimeString() {
            LocalTime lc = time.toLocalTime();
            return lc.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }

    private static final String SQLITE_PREFIX = "jdbc:sqlite:";
    private static String URL = SQLITE_PREFIX + "data/leki.db";

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
        try(Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("""
                CREATE TABLE IF NOT EXISTS leki(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nazwa TEXT NOT NULL,
                    godzina TIME NOT NULL
                );
            """);
            stmt.execute();
        }
        catch (SQLException e) {
            System.err.println("błąd przygotowania tabeli leków: ");
            e.printStackTrace();
        }
    }

    public static List<LekiEntry> getAll() {
        List<LekiEntry> results = new ArrayList<>();
        try(Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nazwa, godzina FROM leki;");

            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                results.add(new LekiEntry(rs.getInt(1), rs.getString(2), rs.getTime(3)));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static LekiEntry add(String name, Time time) {
        try(Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO leki(nazwa, godzina) VALUES (?, ?)");
            stmt.setString(1, name);
            stmt.setTime(2, time);
            stmt.execute();

            stmt = conn.prepareStatement("SELECT last_insert_rowid();");
            ResultSet rs = stmt.executeQuery();

            return new LekiEntry(rs.getInt(1), name, time);
        }
        catch (SQLException err) {
            err.printStackTrace();
        }
        return null;
    }

    public static void update(LekiEntry entry) {
        try(Connection conn = connect()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE leki SET nazwa = ?, godzina = ? WHERE id = ?;");
            stmt.setString(1, entry.name);
            stmt.setTime(2, entry.time);
            stmt.setInt(3, entry.id);
            stmt.executeUpdate();
        }
        catch (SQLException err) {
            err.printStackTrace();
        }
    }

    public static void delete(LekiEntry entry) {
        try(Connection conn = connect()) {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM leki WHERE id = ?;");
            statement.setInt(1, entry.id);
            statement.executeUpdate();
        }
        catch (SQLException err) {
            err.printStackTrace();
        }
    }
}
