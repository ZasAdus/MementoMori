package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaWizyty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import java.util.ArrayList;
import java.util.List;

public class SzczegolyWizytyController {

    public static final String PATH = "wizyty/szczegoly_wizyty.fxml";

    @FXML
    public void goBack() {
        MementoMori.navigateTo(WizytyController.PATH);
        WizytyController.setSelectedAppointment(null);
    }

    @FXML
    private ListView<HBox> appointmentsList;

    private static int userId;

    private static SzczegolyWizytyController instance;

    public static void setUserId(int id) {
        userId = id;
    }

    @FXML
    public void initialize() {
        instance = this;

        List<String> rawAppointments = BazaWizyty.fetchAppointmentsFromDatabase(userId);
        List<HBox> formattedAppointments = new ArrayList<>();

        if (rawAppointments.isEmpty()) {
            Label noAppointmentsLabel = new Label("Brak zaplanowanych wizyt.");
            noAppointmentsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            HBox noAppointmentsBox = new HBox(noAppointmentsLabel);
            noAppointmentsBox.setStyle("-fx-alignment: center;");

            formattedAppointments.add(noAppointmentsBox);
        } else {
            for (String appointment : rawAppointments) {
                String[] parts = appointment.split("\\|");
                String dateTime = parts[0];
                int doctorId = Integer.parseInt(parts[1]);
                String status = parts[2];

                String doctorDetails = BazaWizyty.getDoctorDetails(doctorId);

                HBox appointmentBox = new HBox(10);
                appointmentBox.setStyle(
                        "-fx-alignment: center-left; " +
                                "-fx-background-color: #D0E8FF; " +
                                "-fx-border-radius: 10px; " +
                                "-fx-padding: 10px; " +
                                "-fx-effect: null; " +
                                "-fx-border-color: #172023; " +
                                "-fx-border-width: 2px;" +
                                "-fx-text-fill: black; "
                );

                appointmentBox.setOnMouseClicked(Event::consume);

                Label appointmentLabel = new Label(dateTime + "\n" + doctorDetails + "\nStatus: " + status);
                appointmentLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

                Button cancelButton = new Button("Odwołaj wizytę");
                cancelButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");

                cancelButton.setOnAction(event -> BazaWizyty.cancelAppointment(dateTime, doctorId));

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                appointmentBox.setSpacing(10);
                appointmentBox.getChildren().addAll(appointmentLabel, spacer, cancelButton);
                formattedAppointments.add(appointmentBox);
            }
        }

        appointmentsList.setStyle("-fx-selection-bar: transparent;");
        appointmentsList.setItems(FXCollections.observableArrayList(formattedAppointments));
    }


    public static void odswiez() {
        //instance = new SzczegolyWizytyController();
        instance.initialize();
    }

}
