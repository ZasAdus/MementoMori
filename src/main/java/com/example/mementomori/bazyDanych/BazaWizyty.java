package com.example.mementomori.bazyDanych;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BazaWizyty {
    private static final String DB_NAME = "data\\harmonogram.db";
    private static final String URL = "jdbc:sqlite:" + DB_NAME;
    private static final int VISIT_DURATION_MINUTES = 30;
    private static final String USER_DB_URL = "jdbc:sqlite:data\\uzytkownicy.db";

    public BazaWizyty() {
        createTables();
    }

    private void createTables() {
        String sql = """
            CREATE TABLE IF NOT EXISTS wizyty (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_lekarza INTEGER NOT NULL,
                id_pacjenta INTEGER NOT NULL,
                data_wizyty TEXT NOT NULL,
                godzina_wizyty TEXT NOT NULL,
                status TEXT NOT NULL,
                UNIQUE(id_lekarza, data_wizyty, godzina_wizyty)
            );
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabela wizyt została utworzona lub już istnieje.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static class Wizyta {
        public int id;
        public int idLekarza;
        public int idPacjenta;
        public LocalDateTime dataczas;
        public String status;
        public String pacjentName;
    }

    public boolean dodajWizyte(int idLekarza, int idPacjenta, LocalDateTime dataczas) {
        // Sprawdź czy lekarz przyjmuje w tym czasie, do aplikacji ze strony użytkownika
        BazaWizytyLekarze bazaLekarze = new BazaWizytyLekarze();
        if (!bazaLekarze.sprawdzCzyPrzyjmuje(dataczas.getDayOfWeek().toString(), dataczas.toLocalTime())) {
            return false;
        }

        String sql = """
            INSERT INTO wizyty(id_lekarza, id_pacjenta, data_wizyty, godzina_wizyty, status)
            VALUES(?, ?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idLekarza);
            pstmt.setInt(2, idPacjenta);
            pstmt.setString(3, dataczas.toLocalDate().toString());
            pstmt.setString(4, dataczas.toLocalTime().toString());
            pstmt.setString(5, "OCZEKUJACA");
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private String getPacjentName(int idPacjenta) {
        String sql = """
        SELECT du.imie, du.nazwisko 
        FROM uzytkownicy u
        JOIN dane_uzytkownikow du ON u.login = du.login
        WHERE u.id = ?
    """;

        try (Connection conn = DriverManager.getConnection(USER_DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPacjenta);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String imie = rs.getString("imie");
                    String nazwisko = rs.getString("nazwisko");
                    return imie + " " + nazwisko;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting patient data: " + e.getMessage());
            e.printStackTrace();
        }
        return "Pacjent " + idPacjenta;
    }

    public List<Wizyta> pobierzWizyty(int idLekarza, String status) {
        String sql = """
        SELECT id, id_lekarza, id_pacjenta, data_wizyty, godzina_wizyty, status
        FROM wizyty
        WHERE id_lekarza = ? AND status = ?
        ORDER BY data_wizyty, godzina_wizyty
    """;

        List<Wizyta> wizyty = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idLekarza);
            pstmt.setString(2, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Wizyta wizyta = new Wizyta();
                    wizyta.id = rs.getInt("id");
                    wizyta.idLekarza = rs.getInt("id_lekarza");
                    wizyta.idPacjenta = rs.getInt("id_pacjenta");
                    String dateStr = rs.getString("data_wizyty");
                    String[] dateParts = dateStr.split("\\.");
                    LocalDate data = LocalDate.of(
                            Integer.parseInt(dateParts[2]), // rok
                            Integer.parseInt(dateParts[1]), // miesiąc
                            Integer.parseInt(dateParts[0])  // dzień
                    );
                    LocalTime czas = LocalTime.parse(rs.getString("godzina_wizyty"));
                    wizyta.dataczas = LocalDateTime.of(data, czas);
                    wizyta.status = rs.getString("status");
                    wizyta.pacjentName = getPacjentName(wizyta.idPacjenta);
                    wizyty.add(wizyta);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error querying appointments: " + e.getMessage());
            e.printStackTrace();
        }
        return wizyty;
    }

    public void zmienStatusWizyty(int idWizyty, String nowyStatus) {
        String sql = "UPDATE wizyty SET status = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nowyStatus);
            pstmt.setInt(2, idWizyty);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean czyTerminDostepny(int idLekarza, LocalDateTime dataczas) {
        String sql = """
            SELECT COUNT(*) as count FROM wizyty 
            WHERE id_lekarza = ? 
            AND data_wizyty = ? 
            AND time(godzina_wizyty) BETWEEN time(?) AND time(?)
        """;

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            LocalTime startTime = dataczas.toLocalTime();
            LocalTime endTime = startTime.plusMinutes(VISIT_DURATION_MINUTES);

            pstmt.setInt(1, idLekarza);
            pstmt.setString(2, dataczas.toLocalDate().toString());
            pstmt.setString(3, startTime.toString());
            pstmt.setString(4, endTime.toString());

            ResultSet rs = pstmt.executeQuery();
            return rs.getInt("count") == 0;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}

