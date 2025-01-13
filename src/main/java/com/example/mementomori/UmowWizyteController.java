package com.example.mementomori;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class UmowWizyteController {

    public static final String PATH = "wizyty/umow_wizyte.fxml";

    @FXML
    private ComboBox<String> cityComboBox;

    @FXML
    private ComboBox<String> specialistComboBox;

    @FXML
    private ListView<String> doctorListView;

    private List<String> doctorsInWarsawFamilyDoctor = List.of("Dr. Jan Kowalski", "Dr. Anna Nowak");
    private List<String> doctorsInKrakowFamilyDoctor = List.of("Dr. Piotr Wiśniewski");
    private List<String> doctorsInWroclawDermatologist = List.of("Dr. Monika Zielińska");

    @FXML
    public void initialize() {
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
            List<String> doctors = new ArrayList<>();

            if (selectedCity.equals("Warszawa") && selectedSpecialist.equals("Lekarz rodzinny")) {
                doctors.addAll(doctorsInWarsawFamilyDoctor);
            } else if (selectedCity.equals("Kraków") && selectedSpecialist.equals("Lekarz rodzinny")) {
                doctors.addAll(doctorsInKrakowFamilyDoctor);
            } else if (selectedCity.equals("Wrocław") && selectedSpecialist.equals("Dermatolog")) {
                doctors.addAll(doctorsInWroclawDermatologist);
            }

            doctorListView.getItems().clear();

            doctorListView.getItems().addAll(doctors);

            if (doctors.isEmpty()) {
                doctorListView.getItems().add("Brak dostępnych lekarzy.");
            }
        } else {
            doctorListView.getItems().clear();
            doctorListView.getItems().add("Wybierz miasto i specjalistę przed wyszukaniem.");
        }
    }

    private void onDoctorSelected(MouseEvent event) {
        String selectedDoctor = doctorListView.getSelectionModel().getSelectedItem();
        if (selectedDoctor != null) {
            TerminWizytyController.setSelectedDoctor(selectedDoctor);
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
