package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaMojeKonto;
import com.example.mementomori.bazyDanych.BazaWizyty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WizytyController {
    public static final String PATH = "wizyty/wizyty.fxml";

    @FXML
    private GridPane calendarGrid;

    @FXML
    private VBox headerBox;

    @FXML
    private ScrollPane calendarScrollPane;

    private LocalDate currentMonday;

//    private static String selectedAppointment;
//    private static int doctorId;

    private static WizytyController instance;

    private static int userId;

    @FXML
    public void initialize() {
        String currentUser = MementoMori.currentUser;
        User user = BazaMojeKonto.getUserData(currentUser);
        userId = user.getId();

        currentMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        instance = this;
        updateCalendar(userId);
    }

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void listWizyty() throws IOException {
        SzczegolyWizytyController.setUserId(userId);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SzczegolyWizytyController.PATH));
        Parent root = loader.load();

        SzczegolyWizytyController controller = loader.getController();
        controller.odswiez();
        MementoMori.navigateTo(SzczegolyWizytyController.PATH);
    }

    @FXML
    public void scheduleAppointment() {
        MementoMori.navigateTo(UmowWizyteController.PATH);
    }

    @FXML
    public void nextWeek() {
        currentMonday = currentMonday.plusWeeks(1);
        updateCalendar(userId);
    }

    @FXML
    public void prevWeek() {
        currentMonday = currentMonday.minusWeeks(1);
        updateCalendar(userId);
    }

    public void updateCalendar(int patientId) {
        calendarGrid.getChildren().clear();
        headerBox.getChildren().clear();

        LocalDate startOfWeek = currentMonday;
        LocalDate endOfWeek = currentMonday.plusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        String weekRange = "Tydzień " + startOfWeek.format(formatter) + " - " + endOfWeek.format(formatter) + " (" + endOfWeek.getYear() + ")";

        Text weekText = new Text(weekRange);
        weekText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        headerBox.getChildren().add(weekText);

        String[] daysOfWeek = {"Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Nd"};
        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = startOfWeek.plusDays(i);
            String dayHeader = daysOfWeek[i] + " " + currentDay.getDayOfMonth();
            Text dayText = new Text(dayHeader);
            calendarGrid.add(dayText, i + 1, 0);
        }

        for (int hour = 6; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                int rowIndex = (hour - 6) * 2 + (minute == 30 ? 1 : 0) + 1;
                String time = String.format("%02d:%02d", hour, minute);
                calendarGrid.add(new Text(time), 0, rowIndex);
            }
        }

        List<String> appointments = BazaWizyty.fetchAppointmentsFromDatabase(patientId);

        for (String appointment : appointments) {
            String[] parts = appointment.split("\\|");
            String dateTime = parts[0];
            int doctorId = Integer.parseInt(parts[1]);

            LocalDateTime appointmentDateTime = parseAppointment(dateTime);
            if (!appointmentDateTime.toLocalDate().isBefore(startOfWeek) && !appointmentDateTime.toLocalDate().isAfter(endOfWeek)) {
                int dayColumn = appointmentDateTime.getDayOfWeek().getValue(); // dzień tygodnia (1-7)
                int hour = appointmentDateTime.getHour();
                int minutes = appointmentDateTime.getMinute();

                int rowIndex = (hour - 6) * 2 + (minutes >= 30 ? 1 : 0) + 1;

                Button button = new Button();
                button.setStyle(
                        "-fx-background-color: #27ae60; " +
                                "-fx-background-radius: 50%; " +
                                "-fx-min-width: 25px; " +
                                "-fx-min-height: 25px; " +
                                "-fx-border-width: 0 "
                );

                String doctorDetails = BazaWizyty.getDoctorDetails(doctorId);

                String tooltipText = String.format(
                        "Data wizyty: %s\nGodzina wizyty: %s\n%s",
                        dateTime.substring(0, 10), dateTime.substring(11), doctorDetails
                );
                Tooltip tooltip = new Tooltip(tooltipText);
                Tooltip.install(button, tooltip);

                calendarGrid.add(button, dayColumn, rowIndex);
            }
        }

        calendarScrollPane.setVvalue(0.0); // Przewiń na samą górę
        calendarScrollPane.setFitToWidth(true);
        calendarScrollPane.setStyle("-fx-border-color: gray; -fx-border-style: solid; -fx-border-radius: 10; -fx-background-radius: 10;-fx-background-color: #a5f0bd");
        calendarScrollPane.getStyleClass().add("scroll-bar");

    }

    public static void setSelectedAppointment(String appointment) {
        //selectedAppointment = appointment;
        instance.updateCalendar(userId);
    }

    private LocalDateTime parseAppointment(String appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(appointment, formatter);
    }
}