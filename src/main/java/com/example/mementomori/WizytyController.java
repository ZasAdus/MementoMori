package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaMojeKonto;
import com.example.mementomori.bazyDanych.BazaWizyty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
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

    private static String selectedAppointment;

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
            LocalDateTime appointmentDateTime = parseAppointment(appointment);
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
                                "-fx-min-height: 25px; "+
                                "-fx-border-width: 0 "
                );

                String tooltipText = getAppointmentDetails(appointment);
                Tooltip tooltip = new Tooltip(tooltipText);
                Tooltip.install(button, tooltip);

                calendarGrid.add(button, dayColumn, rowIndex);
            }
        }

        calendarScrollPane.setVvalue(0.0);
        calendarScrollPane.setFitToWidth(true);
    }


    public static void setSelectedAppointment(String appointment) {
        selectedAppointment = appointment;
        instance.updateCalendar(userId);
    }

    private LocalDateTime parseAppointment(String appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(appointment, formatter);
    }


    private String getAppointmentDetails(String appointment) {
        String[] details = {
                "Data wizyty: " + appointment.substring(0, 10),
                "Godzina wizyty: " + appointment.substring(11),
                "Lekarz: ",
                "Specjalizacja: ",
                "Adres: "
        };

        return String.join("\n", details);
    }
}