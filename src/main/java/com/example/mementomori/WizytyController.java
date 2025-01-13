package com.example.mementomori;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @FXML
    public void initialize() {
        currentMonday = LocalDate.now().with(DayOfWeek.MONDAY);
        instance = this;
        updateCalendar();
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
        updateCalendar();
    }

    @FXML
    public void prevWeek() {
        currentMonday = currentMonday.minusWeeks(1);
        updateCalendar();
    }

    private void updateCalendar() {
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

        for (int i = 6; i <= 20; i += 2) {
            String time = String.format("%02d:00", i);
            int row = (i - 6) / 2 + 1;
            calendarGrid.add(new Text(time), 0, row);
        }

        if (selectedAppointment != null) {
            LocalDateTime appointmentDateTime = parseAppointment(selectedAppointment);
            if (!appointmentDateTime.toLocalDate().isBefore(startOfWeek) && !appointmentDateTime.toLocalDate().isAfter(endOfWeek)) {

                int dayColumn = appointmentDateTime.getDayOfWeek().getValue() % 7;

                int startHour = 6;
                int step = 2;
                int hour = appointmentDateTime.getHour();
                int minutes = appointmentDateTime.getMinute();

                double row = (hour - startHour) / (double) step + 1;
                row += (minutes / 60.0) / step;

                Button button = new Button();
                button.setStyle(
                        "-fx-background-color: #27ae60; " +
                                "-fx-background-radius: 50%; " +
                                "-fx-min-width: 25px; " +
                                "-fx-min-height: 25px; "
                );
                button.setOnAction(event -> showAppointmentDetails(selectedAppointment));
                calendarGrid.add(button, dayColumn + 1, (int) row);
            }
        }

        calendarScrollPane.setFitToWidth(true);
        calendarScrollPane.setFitToHeight(true);
    }

    public static void setSelectedAppointment(String appointment) {
        selectedAppointment = appointment;
        instance.updateCalendar();
    }

    private LocalDateTime parseAppointment(String appointment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(appointment, formatter);
    }


    private void showAppointmentDetails(String appointment) {
        String[] details = {
                "Data wizyty: " + appointment.substring(0, 10),
                "Godzina wizyty: " + appointment.substring(11),
                "Lekarz: ",
                "Specjalizacja: ",
                "Adres: "
        };

        String info = String.join("\n", details);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Szczegóły wizyty");
        alert.setHeaderText("Informacje o wizycie");
        alert.setContentText(info);

        alert.showAndWait();
    }

}