package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaWizyty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelLekarzaZarzadzanieWizytamiController {
    @FXML
    private TabPane appointmentTabPane;

    @FXML
    private Tab confirmedTab;

    @FXML
    private Tab pendingTab;

    @FXML
    private Button homeButton;

    @FXML
    private AnchorPane confirmedAnchorPane;

    @FXML
    private AnchorPane pendingAnchorPane;

    private ScrollPane confirmedScrollPane;
    private ScrollPane pendingScrollPane;
    private VBox confirmedAppointmentsBox;
    private VBox pendingAppointmentsBox;
    private BazaWizyty bazaWizyty;

    @FXML
    public void initialize() {
        try {
            bazaWizyty = new BazaWizyty();

            // Initialize VBoxes
            confirmedAppointmentsBox = new VBox(10);
            pendingAppointmentsBox = new VBox(10);
            confirmedAppointmentsBox.setPadding(new Insets(10));
            pendingAppointmentsBox.setPadding(new Insets(10));

            // Initialize ScrollPanes
            confirmedScrollPane = new ScrollPane(confirmedAppointmentsBox);
            pendingScrollPane = new ScrollPane(pendingAppointmentsBox);

            // Configure ScrollPanes
            configureScrollPane(confirmedScrollPane);
            configureScrollPane(pendingScrollPane);

            if (confirmedAnchorPane != null && pendingAnchorPane != null) {
                // Add ScrollPanes to AnchorPanes with proper anchoring
                setAnchorConstraints(confirmedScrollPane);
                setAnchorConstraints(pendingScrollPane);

                confirmedAnchorPane.getChildren().add(confirmedScrollPane);
                pendingAnchorPane.getChildren().add(pendingScrollPane);

                loadAppointments();

                appointmentTabPane.getSelectionModel().selectedItemProperty().addListener(
                        (observable, oldTab, newTab) -> {
                            System.out.println("Tab changed to: " + newTab.getText());
                            loadAppointments();
                        });
            }
        } catch (Exception e) {
            System.out.println("Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configureScrollPane(ScrollPane scrollPane) {
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    private void setAnchorConstraints(ScrollPane scrollPane) {
        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
    }

    private void loadAppointments() {

        try {
            if (confirmedAppointmentsBox != null && pendingAppointmentsBox != null) {
                confirmedAppointmentsBox.getChildren().clear();
                pendingAppointmentsBox.getChildren().clear();

                List<BazaWizyty.Wizyta> confirmedAppointments = bazaWizyty.pobierzWizyty(MementoMori.idDoctor, "POTWIERDZONA");
                for (BazaWizyty.Wizyta wizyta : confirmedAppointments) {
                    HBox appointmentEntry = createAppointmentEntry(wizyta);
                    confirmedAppointmentsBox.getChildren().add(appointmentEntry);
                }

                List<BazaWizyty.Wizyta> pendingAppointments = bazaWizyty.pobierzWizyty(MementoMori.idDoctor, "OCZEKUJACA");
                for (BazaWizyty.Wizyta wizyta : pendingAppointments) {
                    HBox appointmentEntry = createAppointmentEntry(wizyta);
                    pendingAppointmentsBox.getChildren().add(appointmentEntry);
                }
            } else {
                System.out.println("ERROR: VBoxes are null!");
            }
        } catch (Exception e) {
            System.out.println("Error loading appointments: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private HBox createAppointmentEntry(BazaWizyty.Wizyta wizyta) {
        HBox entryContainer = new HBox(10);
        entryContainer.setStyle("-fx-background-color: #E6F3FF; -fx-padding: 10; -fx-background-radius: 5;");
        entryContainer.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(entryContainer, Priority.ALWAYS);

        VBox patientInfo = new VBox(5);
        Label nameLabel = new Label(wizyta.pacjentName);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-wrap-text: true;");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        patientInfo.getChildren().add(nameLabel);
        HBox.setHgrow(patientInfo, Priority.ALWAYS);
        patientInfo.setMinWidth(100);

        VBox dateTimeInfo = new VBox(5);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        Label dateLabel = new Label(wizyta.dataczas.format(dateFormatter));
        Label timeLabel = new Label(wizyta.dataczas.format(timeFormatter) + " - " +
                wizyta.dataczas.plusMinutes(30).format(timeFormatter));
        dateLabel.setStyle("-fx-font-weight: bold;");
        Label dayLabel = new Label(wizyta.dataczas.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.forLanguageTag("pl")));
        dateTimeInfo.getChildren().addAll(dateLabel, dayLabel, timeLabel);
        dateTimeInfo.setMinWidth(100);

        VBox buttonBox = new VBox(5);
        buttonBox.setMinWidth(120);
        Button cancelButton = new Button("Odwołaj wizytę");
        Button detailsButton = new Button("Szczegóły wizyty");

        cancelButton.setPrefWidth(120);
        detailsButton.setPrefWidth(120);

        if (wizyta.status.equals("OCZEKUJACA")) {
            Button confirmButton = new Button("Potwierdź wizytę");
            confirmButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            confirmButton.setPrefWidth(120);
            confirmButton.setOnAction(e -> {
                bazaWizyty.zmienStatusWizyty(wizyta.id, "POTWIERDZONA");
                loadAppointments();
            });
            buttonBox.getChildren().add(confirmButton);
        }

        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        detailsButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        cancelButton.setOnAction(e -> {
            bazaWizyty.zmienStatusWizyty(wizyta.id, "ANULOWANA");
            loadAppointments();
        });

        buttonBox.getChildren().addAll(cancelButton, detailsButton);
        entryContainer.getChildren().addAll(patientInfo, dateTimeInfo, buttonBox);

        return entryContainer;
    }

    @FXML
    private void handleHomeButton() {
        MementoMori.navigateTo("Lekarz/PanelLekarzaMain.fxml");
    }
}