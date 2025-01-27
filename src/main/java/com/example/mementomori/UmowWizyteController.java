package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaWizyty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import java.util.HashMap;
import java.util.Map;


public class UmowWizyteController {

    public static final String PATH = "wizyty/umow_wizyte.fxml";

    @FXML
    private ComboBox<String> cityComboBox;

    @FXML
    private ComboBox<String> specialistComboBox;

    @FXML
    private ListView<String> doctorListView;

    private Map<String, Integer> doctorIdMap = new HashMap<>();

    @FXML
    public void initialize() {
        doctorListView.setStyle("-fx-background-color: #a5f0bd; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: gray;");
        cityComboBox.setStyle("-fx-background-color: #a5d8ff;");
        specialistComboBox.setStyle("-fx-background-color: #a5d8ff;");
        doctorListView.setOnMouseClicked(this::onDoctorSelected);
    }

    @FXML
    public void scheduleAppointment() {
        MementoMori.navigateTo(TerminWizytyController.PATH);
    }

    @FXML
    public void searchAppointments() {
        String selectedCity = cityComboBox.getValue();
        String selectedSpecialist = specialistComboBox.getValue();

        if (selectedCity != null && selectedSpecialist != null) {
            Map<String, Integer> fetchedDoctors = BazaWizyty.fetchDoctorsFromDatabase(selectedSpecialist, selectedCity);

            updateDoctorListView(fetchedDoctors);
        } else {
            doctorListView.getItems().clear();
            doctorListView.getItems().add("Wybierz miasto i specjalistę przed wyszukaniem.");
        }
    }

    private void updateDoctorListView(Map<String, Integer> doctors) {
        doctorListView.getItems().clear();
        doctorIdMap.clear();

        if (doctors.isEmpty()) {
            doctorListView.getItems().add("Brak dostępnych lekarzy.");
        } else {
            doctorListView.getItems().addAll(doctors.keySet());
            doctorIdMap.putAll(doctors);
        }
    }

    private void onDoctorSelected(MouseEvent event) {
        String selectedDoctor = doctorListView.getSelectionModel().getSelectedItem();
        if (selectedDoctor != null && !selectedDoctor.equals("Brak dostępnych lekarzy.") && !selectedDoctor.equals("Wybierz miasto i specjalistę przed wyszukaniem.")) {
            Integer doctorId = doctorIdMap.get(selectedDoctor);
            TerminWizytyController.setSelectedDoctor(selectedDoctor);
            TerminWizytyController.setSelectedDoctorId(doctorId);
            scheduleAppointment();
        }
    }

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void goWizyty() {
        MementoMori.navigateTo(WizytyController.PATH);
    }

}
