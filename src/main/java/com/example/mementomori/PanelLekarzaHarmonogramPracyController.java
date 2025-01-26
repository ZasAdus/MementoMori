package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaHarmonogram;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PanelLekarzaHarmonogramPracyController {
    @FXML private Button poniedzialekBtn;
    @FXML private Button wtorekBtn;
    @FXML private Button srodaBtn;
    @FXML private Button czwartekBtn;
    @FXML private Button piatekBtn;
    @FXML private Button sobotaBtn;
    @FXML private Button niedzielaBtn;
    @FXML private Button homeBtn;
    @FXML private Button lewoDataGuzik;
    @FXML private Button prawoDataGuzik;
    @FXML private Text tygodniowkaText;

    private Map<String, TimeRange> harmonogram;
    private BazaHarmonogram baza;
    private static final int ID_LEKARZA = MementoMori.idDoctor;
    private LocalDate currentMonday;

    private final Map<String, Button> dniTygodnia = new HashMap<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        currentMonday = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        baza = new BazaHarmonogram();
        initButtonMap();
        updateTygodniowka();
        loadScheduleFromDatabase();
    }

    private void initButtonMap() {
        dniTygodnia.put("Poniedziałek", poniedzialekBtn);
        dniTygodnia.put("Wtorek", wtorekBtn);
        dniTygodnia.put("Środa", srodaBtn);
        dniTygodnia.put("Czwartek", czwartekBtn);
        dniTygodnia.put("Piątek", piatekBtn);
        dniTygodnia.put("Sobota", sobotaBtn);
        dniTygodnia.put("Niedziela", niedzielaBtn);
    }

    private void updateTygodniowka() {
        LocalDate endOfWeek = currentMonday.plusDays(6);
        LocalDate startOfWeek = currentMonday;
        String weekRange = startOfWeek.format(formatter) + " - " + endOfWeek.format(formatter);
        tygodniowkaText.setText(weekRange);
        tygodniowkaText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    }

    private void loadScheduleFromDatabase() {
        harmonogram = baza.pobierzHarmonogram();

        List<String> sortedDaysOfWeek = Arrays.asList("Poniedziałek", "Wtorek", "Środa", "Czwartek", "Piątek", "Sobota", "Niedziela");
        Map<String, Button> sortedDniTygodnia = new LinkedHashMap<>();
        for (String dzien : sortedDaysOfWeek) {
            if (dniTygodnia.containsKey(dzien)) {
                sortedDniTygodnia.put(dzien, dniTygodnia.get(dzien));
            }
        }

        for (Map.Entry<String, Button> entry : sortedDniTygodnia.entrySet()) {
            String dzien = entry.getKey();
            Button button = entry.getValue();

            LocalDate currentDate = currentMonday.plusDays(sortedDaysOfWeek.indexOf(dzien));
            String formattedDate = currentDate.format(formatter);

            // Modify this part to match the exact format in the database
            String databaseKey = dzien + ", " +
                    currentDate.getDayOfMonth() + "." +
                    String.format("%02d", currentDate.getMonthValue()) + "." +
                    currentDate.getYear();

            TimeRange timeRange = harmonogram.get(databaseKey);

            if (timeRange != null) {
                button.setText(dzien + " (" + formattedDate + "): " + timeRange);
            } else {
                button.setText(dzien + " (" + formattedDate + "): Brak godzin");
            }

            button.setVisible(false);
            button.setVisible(true);
        }
    }

    @FXML
    private void prevWeek() {
        currentMonday = currentMonday.minusWeeks(1);
        updateTygodniowka();
        loadScheduleFromDatabase();
    }

    @FXML
    private void nextWeek() {
        currentMonday = currentMonday.plusWeeks(1);
        updateTygodniowka();
        loadScheduleFromDatabase();
    }


    @FXML
    private void handleDayClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String data = (clickedButton.getText().split(" ")[1]).replace(":","").replace("(","").replace(")","");
        String dzien = clickedButton.getText().split(" ")[0];
        String formattedDate = clickedButton.getText().split(" ")[1].replaceAll("[()]:", "");
        formattedDate = formattedDate.substring(1);

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Opcje harmonogramu");
        dialog.initStyle(StageStyle.UNDECORATED);

        ButtonType modifyButtonType = new ButtonType("Zmień", ButtonBar.ButtonData.OK_DONE);
        ButtonType removeButtonType = new ButtonType("Usuń", ButtonBar.ButtonData.NO);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);


        dialog.getDialogPane().getButtonTypes().addAll(modifyButtonType, removeButtonType, cancelButtonType);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setPrefWidth(MementoMori.WIDTH * MementoMori.scale * 0.85);
        dialogPane.setPrefHeight(MementoMori.HEIGHT * MementoMori.scale * 0.2);

        Stage mainStage = (Stage) clickedButton.getScene().getWindow();
        double centerX = mainStage.getX() + mainStage.getWidth() * 0.93 / 2;
        double centerY = mainStage.getY() + mainStage.getHeight() / 2;

        dialog.setX(centerX - dialog.getDialogPane().getPrefWidth() / 2);
        dialog.setY(centerY - dialog.getDialogPane().getPrefHeight() / 2);

        dialog.getDialogPane().setStyle(
                "-fx-background-color: #a5f0bd;" +
                        "-fx-padding: 20; " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-color: gray; " +
                        "-fx-border-radius: 5; " +
                        "-fx-border-width: 1;"
        );

        // Customize buttons
        dialog.getDialogPane().lookupButton(modifyButtonType).setStyle(
                "-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;"
        );

        dialog.getDialogPane().lookupButton(removeButtonType).setStyle(
                "-fx-background-color: #f44336;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;"
        );

        dialog.getDialogPane().lookupButton(cancelButtonType).setStyle(
                "-fx-background-color: #CCCCCC;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;"
        );

        dialog.getDialogPane().lookupButton(modifyButtonType).setOnMouseEntered(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #4CAF50;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
                )
        );

        dialog.getDialogPane().lookupButton(modifyButtonType).setOnMouseExited(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #4CAF50;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: null;"
                )
        );

        dialog.getDialogPane().lookupButton(removeButtonType).setOnMouseEntered(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #f44336;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
                )
        );

        dialog.getDialogPane().lookupButton(removeButtonType).setOnMouseExited(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #f44336;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: null;"
                )
        );

        dialog.getDialogPane().lookupButton(cancelButtonType).setOnMouseEntered(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #CCCCCC;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
                )
        );

        dialog.getDialogPane().lookupButton(cancelButtonType).setOnMouseExited(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #CCCCCC;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: null;"
                )
        );

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        Label messageLabel = new Label("Co chcesz zrobić z godzinami w dniu:");
        messageLabel.setStyle("-fx-font-weight: bold;");
        Label date = new Label(data + "?");
        date.setStyle("-fx-font-weight: bold;");
        content.getChildren().addAll(messageLabel,date);
        content.setAlignment(Pos.CENTER);

        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == modifyButtonType) {
                showTimePicker(dzien, formattedDate);
            } else if (result.get() == removeButtonType) {
                removeHours(dzien, formattedDate);
            }
        }
    }

    private void showTimePicker(String dzien, String formattedDate) {
        TimePickerDialog dialog = new TimePickerDialog();

        TimeRange currentRange = harmonogram.get(dzien + ", " + formattedDate);
        if (currentRange != null) {
            dialog.setInitialTime(currentRange.getStartTime(), currentRange.getEndTime());
        } else {
            dialog.setInitialTime(LocalTime.of(8, 0), LocalTime.of(16, 0));
        }

        Optional<TimeRange> result = dialog.showAndWait();
        result.ifPresent(timeRange -> {
            try {
                // Split by dot or slash and ensure we have at least 3 parts
                String[] dateParts = formattedDate.split("[./]");
                if (dateParts.length >= 3) {
                    if (timeRange.getEndTime().isAfter(timeRange.getStartTime())) {
                        baza.zapiszHarmonogram(dzien, dateParts[0], dateParts[1], dateParts[2], timeRange);
                        loadScheduleFromDatabase();
                    } else {
                        showErrorAlert("Błąd", "Godzina końcowa musi być późniejsza niż początkowa!");
                    }
                } else {
                    showErrorAlert("Błąd", "Nieprawidłowy format daty: " + formattedDate);
                }
            } catch (Exception e) {
                showErrorAlert("Błąd", "Nie udało się przetworzyć daty: " + e.getMessage());
            }
        });
    }

    private void removeHours(String dzien, String formattedDate) {
        baza.usunHarmonogramDnia(ID_LEKARZA, dzien, formattedDate.split("\\.")[0], formattedDate.split("\\.")[1], formattedDate.split("\\.")[2]);
        loadScheduleFromDatabase();
    }

    private void showErrorAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleHome() {
        MementoMori.navigateTo("Lekarz/PanelLekarzaMain.fxml");
    }
}
