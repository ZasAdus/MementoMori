package com.example.mementomori;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerminWizytyController {
    public static final String PATH = "wizyty/termin_wizyty.fxml";

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void goWizyty() {
        MementoMori.navigateTo(UmowWizyteController.PATH);
    }

    private static String selectedDoctor;

    @FXML
    private Label doctorLabel;

    @FXML
    private ListView<String> appointmentListView;

    private static final Map<String, List<String>> doctorAppointments = new HashMap<>();

    static {
        doctorAppointments.put("Dr. Jan Kowalski", List.of("2024-12-17 08:00", "2024-12-17 10:00", "2024-12-18 14:00"));
        doctorAppointments.put("Dr. Anna Nowak", List.of("2024-12-17 09:00", "2024-12-19 11:00", "2024-12-20 13:00"));
        doctorAppointments.put("Dr. Piotr Wiśniewski", List.of("2025-01-10 08:30", "2024-12-18 12:30", "2024-12-19 15:00"));
    }

    public static void setSelectedDoctor(String doctor) {
        selectedDoctor = doctor;
    }

    @FXML
    private void initialize() {

        doctorLabel.setText("Wybrany lekarz: " + selectedDoctor);

        List<String> appointments = doctorAppointments.getOrDefault(selectedDoctor, List.of());
        ObservableList<String> appointmentList = FXCollections.observableArrayList(appointments);
        appointmentListView.setItems(appointmentList);

        appointmentListView.getItems().clear();

        appointmentListView.getItems().addAll(appointments);

        if (appointments.isEmpty()) {
            appointmentListView.getItems().add("Brak dostępnych terminów.");
        }

        appointmentListView.setOnMouseClicked(this::onAppointmentSelected);
    }

    @FXML
    private void onAppointmentSelected(MouseEvent event) {
        String selectedAppointment = appointmentListView.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            WizytyController.setSelectedAppointment(selectedAppointment);
            scheduleAppointment();
        }
    }

    @FXML
    public void scheduleAppointment() {
        MementoMori.navigateTo(WizytyController.PATH);
    }

}
