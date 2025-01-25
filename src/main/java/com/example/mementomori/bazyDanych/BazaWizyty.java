package com.example.mementomori.bazyDanych;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BazaWizyty {
    private static final String DB_NAME = "data\\wizyty.db";
    private static final String URL = "jdbc:sqlite:" + DB_NAME;
    private static final int VISIT_DURATION_MINUTES = 30;
    private static final String USER_DB_URL = "jdbc:sqlite:data\\uzytkownicy.db";

    public BazaWizyty() {
        createTables();
    }

    private void createTables() {
        String createWizytyTableSQL = """
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

        String createPacjenciTableSQL = """
        CREATE TABLE IF NOT EXISTS pacjenci (
            id_pacjenta INTEGER PRIMARY KEY,
            imie TEXT NOT NULL,
            nazwisko TEXT NOT NULL
        );
    """;

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createWizytyTableSQL);
            stmt.execute(createPacjenciTableSQL);
            System.out.println("Tabele wizyty i pacjenci zostały utworzone lub już istnieją.");
        } catch (SQLException e) {
            System.out.println("Błąd podczas tworzenia tabel: " + e.getMessage());
            e.printStackTrace();  // Wydrukuj pełny ślad stosu, aby uzyskać więcej informacji
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
        // Sprawdź czy lekarz przyjmuje w tym czasie
        BazaHarmonogram bazaLekarze = new BazaHarmonogram();

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
            System.out.println("Błąd dodawania wizyty: " + e.getMessage());
            e.printStackTrace();
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
        String i = "", n = "";

        try (Connection conn = DriverManager.getConnection(USER_DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPacjenta);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String imie = rs.getString("imie");
                    i = imie;
                    String nazwisko = rs.getString("nazwisko");
                    n = nazwisko;
                    return imie + " " + nazwisko;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting patient data: " + e.getMessage());
            e.printStackTrace();
        }
        sql = "INSERT INTO pacjenci VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPacjenta);
            pstmt.setString(2, i);
            pstmt.setString(3, n);

            // Execute the prepared statement
            pstmt.executeUpdate();
        } catch (SQLException e) {
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

                    // Bezpieczniejsze parsowanie daty
                    String dateStr = rs.getString("data_wizyty");
                    LocalDate data;
                    try {
                        // Próbujemy najpierw format YYYY-MM-DD
                        data = LocalDate.parse(dateStr);
                    } catch (Exception e) {
                        try {
                            // Jeśli nie wyjdzie, próbujemy format DD.MM.YYYY
                            String[] dateParts = dateStr.split("\\.");
                            data = LocalDate.of(
                                    Integer.parseInt(dateParts[2]), // rok
                                    Integer.parseInt(dateParts[1]), // miesiąc
                                    Integer.parseInt(dateParts[0])  // dzień
                            );
                        } catch (Exception ex) {
                            System.out.println("Błąd parsowania daty: " + dateStr);
                            continue; // Pomijamy nieprawidłowy wpis
                        }
                    }

                    // Bezpieczniejsze parsowanie czasu
                    try {
                        LocalTime czas = LocalTime.parse(rs.getString("godzina_wizyty"));
                        wizyta.dataczas = LocalDateTime.of(data, czas);
                        wizyta.status = rs.getString("status");
                        wizyta.pacjentName = getPacjentName(wizyta.idPacjenta);
                        wizyty.add(wizyta);
                    } catch (Exception e) {
                        System.out.println("Błąd parsowania czasu: " + rs.getString("godzina_wizyty"));
                    }
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

    public static Map<String, Integer> fetchDoctorsFromDatabase(String specialist, String city) {
        Map<String, Integer> doctorMap = new HashMap<>();
        String sql = """
            SELECT d.id, u.imie, u.nazwisko
            FROM lekarze d
            JOIN dane_uzytkownikow u ON d.login = u.login
            WHERE d.specjalizacja = ? AND d.miasto = ?
            """;

        try (Connection conn = DriverManager.getConnection(USER_DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, specialist);
            pstmt.setString(2, city);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int doctorId = rs.getInt("id");
                String doctorName = rs.getString("imie") + " " + rs.getString("nazwisko");
                doctorMap.put(doctorName, doctorId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doctorMap;
    }

    public static List<String> fetchAppointmentsFromDatabase(int patientId) {
        List<String> appointments = new ArrayList<>();
        String sql = """
            SELECT data_wizyty, godzina_wizyty, id_lekarza
            FROM wizyty
            WHERE id_pacjenta = ?
            """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:data\\wizyty.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String date = rs.getString("data_wizyty");
                String time = rs.getString("godzina_wizyty");
                appointments.add(date + " " + time); // Można dodać dodatkowe informacje, np. lekarza
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }
}

