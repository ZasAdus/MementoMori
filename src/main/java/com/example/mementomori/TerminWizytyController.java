package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaMojeKonto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TerminWizytyController {
    public static final String PATH = "wizyty/termin_wizyty.fxml";

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void goWizyty() {
        MementoMori.navigateTo(UmowWizyteController.PATH);
    }

    private static String selectedDoctor;
    private static int selectedDoctorId;

    @FXML
    private Label doctorLabel;

    @FXML
    private Label dateLabel;
    private LocalDate selectedDate = LocalDate.now();

    @FXML
    private ListView<String> appointmentListView;

    public static void setSelectedDoctor(String doctor) {
        selectedDoctor = doctor;
    }
    public static void setSelectedDoctorId(int doctorId) { selectedDoctorId = doctorId; }

    private int userId;

    @FXML
    private void initialize() {
        String currentUser = MementoMori.currentUser;

        User user = BazaMojeKonto.getUserData(currentUser);

        userId = user.getId();

        doctorLabel.setText("Wybrany lekarz: " + selectedDoctor);

        updateAppointmentList();

        appointmentListView.setOnMouseClicked(this::onAppointmentSelected);
    }

    private void updateAppointmentList() {
        dateLabel.setText("Data: " + selectedDate.toString());

        List<String> appointments = fetchAppointmentsFromDatabase(selectedDoctorId, selectedDate);
        ObservableList<String> appointmentList = FXCollections.observableArrayList(appointments);

        appointmentListView.setItems(appointmentList);

        if (appointments.isEmpty()) {
            appointmentListView.getItems().add("Brak dostępnych terminów.");
        }
    }

    private List<String> fetchAppointmentsFromDatabase(int doctorId, LocalDate date) {
        List<String> appointments = new ArrayList<>();
        String sql = """
                SELECT dzien, miesiac, rok, godzina_start, godzina_koniec
                FROM harmonogram_lekarzy
                WHERE id_lekarza = ? AND dzien = ? AND miesiac = ? AND rok = ?
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:data\\wizyty.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String[] dateParts = date.toString().split("-");

            pstmt.setInt(1, doctorId);
            pstmt.setString(2, dateParts[2]);
            pstmt.setString(3, dateParts[1]);
            pstmt.setString(4, dateParts[0]);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String dateStr = String.format("%s-%s-%s", rs.getString("rok"), rs.getString("miesiac"), rs.getString("dzien"));
                String startTime = rs.getString("godzina_start");
                String endTime = rs.getString("godzina_koniec");

                List<String> availableAppointments = generateAvailableAppointments(dateStr, startTime, endTime);

                for (String appointment : availableAppointments) {
                    if (isAppointmentAvailable(appointment)) {
                        appointments.add(appointment);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    private List<String> generateAvailableAppointments(String date, String startTime, String endTime) {
        List<String> availableAppointments = new ArrayList<>();

        LocalDate appointmentDate = LocalDate.parse(date);
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);

        LocalTime currentTime = start;
        while (currentTime.isBefore(end)) {
            availableAppointments.add(appointmentDate + " " + currentTime.toString());
            currentTime = currentTime.plusMinutes(30);
        }

        return availableAppointments;
    }


    private boolean isAppointmentAvailable(String appointment) {
        String[] parts = appointment.split(" ");
        String date = parts[0];
        String time = parts[1];

        String sql = """
                SELECT COUNT(*) FROM wizyty
                WHERE id_lekarza = ? AND data_wizyty = ? AND godzina_wizyty = ?
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:data\\wizyty.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, selectedDoctorId);
            pstmt.setString(2, date);
            pstmt.setString(3, time);

            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    private void onAppointmentSelected(MouseEvent event) {
        String selectedAppointment = appointmentListView.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null && !selectedAppointment.equals("Brak dostępnych terminów.")) {
            saveAppointmentToDatabase(selectedAppointment);
            WizytyController.setSelectedAppointment(selectedAppointment);
            SzczegolyWizytyController.odswiez();
            scheduleAppointment();
        }
    }

    private void saveAppointmentToDatabase(String appointment) {
        String[] parts = appointment.split(" ");
        String date = parts[0];
        String time = parts[1];

        int patientId = userId;

        String sql = """
                INSERT INTO wizyty (id_lekarza, id_pacjenta, data_wizyty, godzina_wizyty, status)
                VALUES (?, ?, ?, ?, 'OCZEKUJACA')
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:data\\wizyty.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, selectedDoctorId);
            pstmt.setInt(2, patientId);
            pstmt.setString(3, date);
            pstmt.setString(4, time);

            pstmt.executeUpdate();
            System.out.println("Wizyta zapisana pomyślnie!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Błąd podczas zapisywania wizyty.");
        }
    }

    @FXML
    public void scheduleAppointment() {
        MementoMori.navigateTo(WizytyController.PATH);
    }

    @FXML
    public void previousDay() {
        selectedDate = selectedDate.minusDays(1);
        updateAppointmentList();
    }

    @FXML
    public void nextDay() {
        selectedDate = selectedDate.plusDays(1);
        updateAppointmentList();
    }

}
