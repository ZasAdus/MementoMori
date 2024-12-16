package com.example.mementomori;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;

import java.time.LocalDate;
import java.time.DayOfWeek;
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

    @FXML
    public void initialize() {
        currentMonday = LocalDate.now().with(DayOfWeek.MONDAY);
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
            calendarGrid.add(new Text(daysOfWeek[i]), i + 1, 0);
        }

        for (int i = 0; i < 24; i++) {
            String time = String.format("%02d:00", i);
            calendarGrid.add(new Text(time), 0, i + 1);
        }

        calendarScrollPane.setFitToWidth(true);
        calendarScrollPane.setFitToHeight(true);
    }
}
