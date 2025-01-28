package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaSpanko;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import javax.swing.*;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

class EepyTimeDialogController {
    record EepyTimeEntry (Timestamp start, Timestamp end) {}

    private static final String Path = "sen/nowy_sen.fxml";

    @FXML private DatePicker start_date;
    @FXML private TextField start_hours;
    @FXML private TextField start_minutes;

    @FXML private DatePicker end_date;
    @FXML private TextField end_hours;
    @FXML private TextField end_minutes;


    public Timestamp getTimeStart() {
        return Timestamp.valueOf(
                start_date.getValue()
                    .atTime(Integer.parseInt(start_hours.getText()), Integer.parseInt(start_minutes.getText()), 0)
        );
    }

    public Timestamp getTimeEnd() {
        return Timestamp.valueOf(
                end_date.getValue()
                        .atTime(Integer.parseInt(end_hours.getText()), Integer.parseInt(end_minutes.getText()), 0)
        );
    }

    public void setValues(EepyTimeEntry entry) {
        start_date.setValue(entry.start.toLocalDateTime().toLocalDate());
        end_date.setValue(entry.end.toLocalDateTime().toLocalDate());

        String s_time = entry.start.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        start_hours.setText(s_time.substring(0, 2));
        start_minutes.setText(s_time.substring(3, 5));

        String e_time = entry.end.toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        end_hours.setText(e_time.substring(0, 2));
        end_minutes.setText(e_time.substring(3, 5));
    }

    public static EepyTimeEntry show(String dialog_title, EepyTimeEntry default_value) {
        Dialog<EepyTimeEntry> dialog = new Dialog<>();
        dialog.setTitle(dialog_title);
        dialog.setHeaderText(null);

        ButtonType submitButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        dialogPane.setStyle("-fx-background-color: #a5f0bd; " +
                "-fx-padding: 10; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: gray; " +
                "-fx-border-radius: 5; " +
                "-fx-border-width: 1;");

        dialogPane.lookupButton(submitButtonType)
                .setStyle("-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;");
        dialogPane.lookupButton(ButtonType.CANCEL)
                .setStyle("-fx-background-color: #f44336;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;");

        EepyTimeDialogController controller = new EepyTimeDialogController();
        FXMLLoader loader = new FXMLLoader(MementoMori.class.getResource(Path));
        loader.setController(controller);

        try {
            dialog.getDialogPane().setContent(loader.load());
            if(default_value != null) {
                controller.setValues(default_value);
            }
            dialog.setResultConverter(dialogButton -> {
                if(dialogButton.getButtonData().isCancelButton()) {
                    return null;
                }

                try {
                    return new EepyTimeEntry(controller.getTimeStart(), controller.getTimeEnd());
                }
                catch (Exception err) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Błąd");
                    alert.setHeaderText(null);
                    alert.setContentText("Wprowadzono niepoprawną godzinę.");

                    // Style the error alert
                    DialogPane alertPane = alert.getDialogPane();
                    alertPane.setStyle("-fx-background-color: #a5f0bd; -fx-border-color: gray; " +
                            "-fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
                    alertPane.lookupButton(ButtonType.OK)
                            .setStyle("-fx-background-color: #2f9e44; -fx-text-fill: white; " +
                                    "-fx-background-radius: 5; -fx-border-radius: 5;");

                    alert.showAndWait();
                }
                return null;
            });

            Optional<EepyTimeEntry> result = dialog.showAndWait();
            return result.orElse(null);
        }
        catch (IOException err) {
            return null;
        }
    }
}

public class SenController {
    public static final String PATH = "sen/sen.fxml";

    @FXML private TextField start_hours;
    @FXML private TextField start_minutes;

    @FXML private TextField end_hours;
    @FXML private TextField end_minutes;

    @FXML private BarChart<String, Number> spankoWykres;

    @FXML private Button btnChartDay;
    @FXML private Button btnChartWeek;
    @FXML private Button btnChartMonth;
    @FXML private Button btnChart6Months;

    public void initialize() {
        btnChartDay.setOnAction(e -> updateChart(chartType.DAY));
        btnChartWeek.setOnAction(e -> updateChart(chartType.WEEK));
        btnChartMonth.setOnAction(e -> updateChart(chartType.MONTH));
        btnChart6Months.setOnAction(e -> updateChart(chartType.SIX_MONTHS));

        spankoWykres.setLegendVisible(false);
        updateChart();
    }

    enum chartType {
        DAY,
        WEEK,
        MONTH,
        SIX_MONTHS
    }

    private chartType currentChart = chartType.DAY;
    private Button currentBtn = null;
    private void updateChart(chartType type) {
        if(currentBtn != null) {
            currentBtn.getStyleClass().remove("active");
        }
        currentBtn = switch (type) {
            case DAY -> btnChartDay;
            case WEEK -> btnChartWeek;
            case MONTH -> btnChartMonth;
            case SIX_MONTHS -> btnChart6Months;
        };
        currentBtn.getStyleClass().add("active");

        spankoWykres.getData().clear();
        spankoWykres.setBarGap(0);
        spankoWykres.getYAxis().setLabel("Liczba godzin snu");


        switch (type) {
            case DAY:
                fillChartDay();
                break;
            case WEEK:
                fillChartWeek();
                break;
            case MONTH:
                fillChartMonth();
                break;
            case SIX_MONTHS:
                fillChartSixMonths();
                break;
        }
    }
    private void updateChart() {
        updateChart(currentChart);
    }

    private void fillChartDay() {
        Timestamp upperBound = Timestamp.valueOf(LocalDateTime.now().minusDays(1).withHour(23).withMinute(59));
        Timestamp lowerBound = Timestamp.valueOf(LocalDateTime.now().minusDays(1).withHour(0).withMinute(0));

        double yesterdaySleep = BazaSpanko.getTotalSleepHours(lowerBound, upperBound);

        spankoWykres.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("", 0)); // margines lewy
        series.getData().add(new XYChart.Data<>("Zeszły dzień", yesterdaySleep));
        series.getData().add(new XYChart.Data<>(" ", 0)); // margines prawy

        spankoWykres.getData().add(series);
    }

    private void fillChartWeek() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        LocalDateTime start = LocalDateTime.now()
            .minusDays(LocalDateTime.now().getDayOfWeek().getValue()-1)
            .withHour(0)
            .withMinute(0);

        LocalDateTime end = start.withHour(23).withMinute(59);

        for(int i = 0; i < 7; i++) {
            Timestamp lowerBound = Timestamp.valueOf(start.plusDays(i));
            Timestamp upperBound = Timestamp.valueOf(end.plusDays(i));

            double sleep = BazaSpanko.getTotalSleepHours(lowerBound, upperBound);
            String weekDay = lowerBound.toLocalDateTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
            series.getData().add(new XYChart.Data<>(weekDay, sleep));
        }
        spankoWykres.getData().add(series);
    }

    private void fillChartMonth() {
        spankoWykres.setBarGap(-1);

        int monthDays = LocalDateTime.now().getMonth().maxLength();
        LocalDateTime start = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime end = start.withHour(23).withMinute(59);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for(int i = 0; i < monthDays; i++) {
            Timestamp lowerBound = Timestamp.valueOf(start.plusDays(i));
            Timestamp upperBound = Timestamp.valueOf(end.plusDays(i));

            double sleep = BazaSpanko.getTotalSleepHours(lowerBound, upperBound);
            series.getData().add(new XYChart.Data<>(Integer.toString(i), sleep));
        }
        spankoWykres.getData().add(series);
    }

    private void fillChartSixMonths() {
        spankoWykres.setBarGap(-1);
        spankoWykres.getYAxis().setLabel("Średnia liczba godzin snu");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        LocalDateTime start = LocalDateTime.now().minusMonths(5).withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime end = start.withDayOfMonth(start.getMonth().maxLength()).withHour(23).withMinute(59);

        for(int i = 1; i <= 6; i++) {
            Timestamp lowerBound = Timestamp.valueOf(start.plusMonths(i-1).withDayOfMonth(1));
            Timestamp upperBound = Timestamp.valueOf(end.plusMonths(i-1).withDayOfMonth(lowerBound.toLocalDateTime().getMonth().maxLength()));

            double avg = BazaSpanko.getAverageSleepHours(lowerBound, upperBound);
            series.getData().add(new XYChart.Data<>(lowerBound.toLocalDateTime().getMonth().getDisplayName(TextStyle.SHORT, Locale.getDefault()), avg));
        }
        spankoWykres.getData().add(series);
    }

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void generateRaport() {
        MementoMori.navigateTo(SenRaportController.PATH);
    }

    @FXML
    public void addEepyTime() {
        boolean substract_day = Integer.parseInt(start_hours.getText()) > Integer.parseInt(end_hours.getText());
        Timestamp start = Timestamp.valueOf(
            LocalDate.now()
                     .minusDays(substract_day ? 1 : 0)
                     .atTime(Integer.parseInt(start_hours.getText()), Integer.parseInt(start_minutes.getText()), 0)
        );
        Timestamp end = Timestamp.valueOf(
            LocalDateTime.now()
                         .withHour(Integer.parseInt(end_hours.getText())).withMinute(Integer.parseInt(end_minutes.getText()))
        );
        EepyTimeDialogController.EepyTimeEntry default_values = new EepyTimeDialogController.EepyTimeEntry(
            start,
            end
        );

        EepyTimeDialogController.EepyTimeEntry entry = EepyTimeDialogController.show("Dodaj nowy sen", default_values);
        if(entry != null) {
            BazaSpanko.addSpanko(entry.start(), entry.end());
        }
        updateChart();
    }
}
