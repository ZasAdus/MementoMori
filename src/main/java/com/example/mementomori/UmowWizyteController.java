package com.example.mementomori;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

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

    @FXML
    private Label selectedDoctorLabel;

    private List<String> doctorsInWarsawFamilyDoctor = List.of("Dr. Jan Kowalski", "Dr. Anna Nowak");
    private List<String> doctorsInKrakowFamilyDoctor = List.of("Dr. Piotr Wiśniewski");
    private List<String> doctorsInWroclawDermatologist = List.of("Dr. Monika Zielińska");

    @FXML
    public void initialize() {
        doctorListView.setOnMouseClicked(this::onDoctorSelected);
    }


    @FXML
    public void searchAppointments() {
        // Pobierz wybrane miasto i specjalistę
        String selectedCity = cityComboBox.getValue();
        String selectedSpecialist = specialistComboBox.getValue();

        if (selectedCity != null && selectedSpecialist != null) {
            List<String> doctors = new ArrayList<>();

            // Logika uzależniona od miasta i specjalisty
            if (selectedCity.equals("Warszawa") && selectedSpecialist.equals("Lekarz rodzinny")) {
                doctors.addAll(doctorsInWarsawFamilyDoctor);
            } else if (selectedCity.equals("Kraków") && selectedSpecialist.equals("Lekarz rodzinny")) {
                doctors.addAll(doctorsInKrakowFamilyDoctor);
            } else if (selectedCity.equals("Wrocław") && selectedSpecialist.equals("Dermatolog")) {
                doctors.addAll(doctorsInWroclawDermatologist);
            }

            // Wyczyść poprzednie wyniki
            doctorListView.getItems().clear();

            // Dodaj dostępnych lekarzy do ListView
            doctorListView.getItems().addAll(doctors);

            if (doctors.isEmpty()) {
                // Jeśli brak wyników, wyświetl informację
                doctorListView.getItems().add("Brak dostępnych lekarzy.");
            }
        } else {
            // Jeśli nie wybrano miasta lub specjalisty, poinformuj użytkownika
            doctorListView.getItems().clear();
            doctorListView.getItems().add("Wybierz miasto i specjalistę przed wyszukaniem.");
        }
    }

    // Obsługuje kliknięcie na lekarza w ListView
    private void onDoctorSelected(MouseEvent event) {
        String selectedDoctor = doctorListView.getSelectionModel().getSelectedItem();
        if (selectedDoctor != null) {
            selectedDoctorLabel.setText("Wybrany lekarz: " + selectedDoctor);
            // Możesz zapisać wybranego lekarza do dalszego procesu umawiania wizyty
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
