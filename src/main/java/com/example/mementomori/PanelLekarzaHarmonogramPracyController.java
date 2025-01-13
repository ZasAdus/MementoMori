package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaWizytyLekarze;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PanelLekarzaHarmonogramPracyController {
    @FXML private Button poniedzialekBtn;
    @FXML private Button wtorekBtn;
    @FXML private Button srodaBtn;
    @FXML private Button czwartekBtn;
    @FXML private Button piatekBtn;
    @FXML private Button sobotaBtn;
    @FXML private Button niedzielaBtn;
    @FXML private Button homeBtn;

    private Map<String, TimeRange> harmonogram;
    private BazaWizytyLekarze baza;
    private static final int ID_LEKARZA = MementoMori.idDoctor;

    private final Map<String, Button> dniTygodnia = new HashMap<>();

    @FXML
    public void initialize() {
        baza = new BazaWizytyLekarze();
        initButtonMap();
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

    private void loadScheduleFromDatabase() {
        System.out.println("Loading schedule for doctor ID: " + ID_LEKARZA);
        harmonogram = baza.pobierzHarmonogram();
        for (Map.Entry<String, Button> entry : dniTygodnia.entrySet()) {
            String dzien = entry.getKey();
            Button button = entry.getValue();

            TimeRange timeRange = harmonogram.get(dzien);

            if (timeRange != null) {
                button.setText(dzien + ": " + timeRange);
            } else {
                button.setText(dzien + ": Brak godzin");
            }
        }
    }

    @FXML
    private void handleDayClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String dzien = clickedButton.getText().split(":")[0];

        javafx.scene.control.Dialog<ButtonType> dialog = new javafx.scene.control.Dialog<>();

        ButtonType modifyButtonType = new ButtonType("Modyfikuj godziny", ButtonBar.ButtonData.OK_DONE);
        ButtonType removeButtonType = new ButtonType("Usuń godziny", ButtonBar.ButtonData.NO);
        ButtonType cancelButtonType = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().addAll(modifyButtonType, removeButtonType, cancelButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().add(new Label("Chcesz modyfikować istniejące godziny czy je usunąć?"));

        dialog.getDialogPane().setContent(content);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == modifyButtonType) {
                showTimePicker(dzien);
            } else if (result.get() == removeButtonType) {
                removeHours(dzien);
            }
        }
    }


    private void showTimePicker(String dzien) {
        TimePickerDialog dialog = new TimePickerDialog();

        TimeRange currentRange = harmonogram.get(dzien);
        if (currentRange != null) {
            dialog.setInitialTime(currentRange.getStartTime(), currentRange.getEndTime());
        } else {
            dialog.setInitialTime(LocalTime.of(8, 0), LocalTime.of(16, 0));
        }

        Optional<TimeRange> result = dialog.showAndWait();
        result.ifPresent(timeRange -> {
            if (timeRange.getEndTime().isAfter(timeRange.getStartTime())) {
                baza.zapiszHarmonogram(dzien, timeRange);
                loadScheduleFromDatabase();
            } else {
                showErrorAlert("Błąd", "Godzina końcowa musi być późniejsza niż początkowa!");
            }
        });
    }

    private void removeHours(String dzien) {
        baza.usunHarmonogramDnia(ID_LEKARZA, dzien);
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
    private void handleHome(){
        MementoMori.navigateTo("Lekarz/PanelLekarzaMain.fxml");
    }

}