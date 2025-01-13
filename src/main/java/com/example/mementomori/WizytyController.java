package com.example.mementomori;

import javafx.event.ActionEvent;
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

    public void updateCalendar() {
        calendarGrid.getChildren().clear();
        headerBox.getChildren().clear();

        LocalDate startOfWeek = currentMonday;
        LocalDate endOfWeek = currentMonday.plusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        String weekRange = "Tydzień " + startOfWeek.format(formatter) + " - " + endOfWeek.format(formatter) + " (" + endOfWeek.getYear() + ")";

        Text weekText = new Text(weekRange);
        weekText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        headerBox.getChildren().add(weekText);

        // Dodaj nagłówki dni tygodnia
        String[] daysOfWeek = {"Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Nd"};
        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = startOfWeek.plusDays(i);
            String dayHeader = daysOfWeek[i] + " " + currentDay.getDayOfMonth();
            Text dayText = new Text(dayHeader);
            calendarGrid.add(dayText, i + 1, 0);
        }

        // Dodaj godziny
        for (int hour = 6; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                int rowIndex = (hour - 6) * 2 + (minute == 30 ? 1 : 0) + 1;
                String time = String.format("%02d:%02d", hour, minute);
                calendarGrid.add(new Text(time), 0, rowIndex);
            }
        }

        // Dodaj zaplanowane wizyty
        if (selectedAppointment != null) {
            LocalDateTime appointmentDateTime = parseAppointment(selectedAppointment);
            if (!appointmentDateTime.toLocalDate().isBefore(startOfWeek) && !appointmentDateTime.toLocalDate().isAfter(endOfWeek)) {
                int dayColumn = appointmentDateTime.getDayOfWeek().getValue();
                int hour = appointmentDateTime.getHour();
                int minutes = appointmentDateTime.getMinute();

                int rowIndex = (hour - 6) * 2 + (minutes >= 30 ? 1 : 0) + 1;

                Button button = new Button();
                button.setStyle(
                        "-fx-background-color: #27ae60; " +
                                "-fx-background-radius: 50%; " +
                                "-fx-min-width: 25px; " +
                                "-fx-min-height: 25px; "
                );
                button.setOnAction(event -> showAppointmentDetails(selectedAppointment));
                calendarGrid.add(button, dayColumn, rowIndex);
            }
        }

        // Ustaw ScrollPane
        calendarScrollPane.setVvalue(0.0); // Przewiń na samą górę
        calendarScrollPane.setFitToWidth(true);
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

    public void doHarmonogramuPracy(ActionEvent actionEvent) {
    }

    public void doZarzadzaniaWizytami(ActionEvent actionEvent) {
    }

    public void doMain(ActionEvent actionEvent) {
    }

    public void doMojeKonto(ActionEvent actionEvent) {
    }
}