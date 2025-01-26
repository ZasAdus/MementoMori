package com.example.mementomori;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import java.time.LocalTime;

public class TimePickerDialog extends Dialog<TimeRange> {
    private final Spinner<Integer> startHourSpinner;
    private final Spinner<Integer> startMinuteSpinner;
    private final Spinner<Integer> endHourSpinner;
    private final Spinner<Integer> endMinuteSpinner;
    private TimeRange result;

    public TimePickerDialog() {
        setTitle("Wybierz godziny pracy");
        DialogPane dialogPane = getDialogPane();

        dialogPane.setPrefWidth(MementoMori.WIDTH * MementoMori.scale * 0.95);
        dialogPane.setPrefHeight(MementoMori.HEIGHT * MementoMori.scale * 0.15);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setStyle("-fx-background-color: #d5f1de;");

        // Customize buttons
        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setText("Anuluj");
        okButton.setText("Potwierdź");

        okButton.setStyle(
                "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;" +
                        "-fx-transition: -fx-background-color 0.2s ease;"
        );

        cancelButton.setStyle(
                "-fx-background-color: #f44336;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;" +
                        "-fx-transition: -fx-background-color 0.2s ease;"
        );

        okButton.setOnMouseEntered(e -> okButton.setStyle(
                "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;" +
                        "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        ));

        okButton.setOnMouseExited(e -> okButton.setStyle(
                "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;" +
                        "-fx-effect: null;"
        ));

        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle(
                "-fx-background-color: #f44336;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;" +
                        "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
        ));

        cancelButton.setOnMouseExited(e -> cancelButton.setStyle(
                "-fx-background-color: #f44336;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;" +
                        "-fx-effect: null;"
        ));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setStyle("-fx-background-color: #d5f1de;");

        SpinnerValueFactory<Integer> startHourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 8);
        SpinnerValueFactory<Integer> startMinuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 0, 30);
        SpinnerValueFactory<Integer> endHourFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 16);
        SpinnerValueFactory<Integer> endMinuteFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30, 0, 30);

        startHourSpinner = new Spinner<>(startHourFactory);
        startMinuteSpinner = new Spinner<>(startMinuteFactory);
        endHourSpinner = new Spinner<>(endHourFactory);
        endMinuteSpinner = new Spinner<>(endMinuteFactory);

        configureSpinner(startHourSpinner);
        configureSpinner(startMinuteSpinner);
        configureSpinner(endHourSpinner);
        configureSpinner(endMinuteSpinner);

        grid.add(new Label("Godzina rozpoczęcia:"), 0, 0);
        grid.add(startHourSpinner, 1, 0);
        grid.add(new Label(":"), 2, 0);
        grid.add(startMinuteSpinner, 3, 0);

        grid.add(new Label("Godzina zakończenia:"), 0, 1);
        grid.add(endHourSpinner, 1, 1);
        grid.add(new Label(":"), 2, 1);
        grid.add(endMinuteSpinner, 3, 1);

        // Apply application font size scaling
        grid.setStyle(String.format(
                "-fx-font-size: %.0fpx;" +
                        "-fx-background-color: #d5f1de;",
                14 * MementoMori.scale
        ));
        dialogPane.setContent(grid);

        // Styling for spinners
        String spinnerStyle =
                "-fx-border-color: gray;" +
                        "-fx-background-color: #7ec8ff;";

        startHourSpinner.setStyle(spinnerStyle);
        startMinuteSpinner.setStyle(spinnerStyle);
        endHourSpinner.setStyle(spinnerStyle);
        endMinuteSpinner.setStyle(spinnerStyle);

        startHourSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateResult());
        startMinuteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateResult());
        endHourSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateResult());
        endMinuteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updateResult());

        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return result;
            }
            return null;
        });
    }

    private void configureSpinner(Spinner<Integer> spinner) {
        spinner.setEditable(true);
        // Scale the width based on the application's scale
        spinner.setPrefWidth(70 * MementoMori.scale);

        spinner.getValueFactory().setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer value) {
                return String.format("%02d", value);
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        });
    }

    private void updateResult() {
        LocalTime startTime = LocalTime.of(
                startHourSpinner.getValue(),
                startMinuteSpinner.getValue()
        );
        LocalTime endTime = LocalTime.of(
                endHourSpinner.getValue(),
                endMinuteSpinner.getValue()
        );
        result = new TimeRange(startTime, endTime);
    }

    public void setInitialTime(LocalTime startTime, LocalTime endTime) {
        if (startTime != null && endTime != null) {
            startHourSpinner.getValueFactory().setValue(startTime.getHour());
            startMinuteSpinner.getValueFactory().setValue(startTime.getMinute());
            endHourSpinner.getValueFactory().setValue(endTime.getHour());
            endMinuteSpinner.getValueFactory().setValue(endTime.getMinute());
            updateResult();
        }
    }
}