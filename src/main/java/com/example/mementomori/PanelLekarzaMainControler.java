package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaWizyty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.StageStyle;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelLekarzaMainControler {
    public Button lewoDataGuzik;
    public Button prawoDataGuzik;
    public Button HarmonogramPracyGuzik;
    public Button ZarzadzanieWizytamiGuzik;
    public Button homeGuzik;
    public Button MojeKontoGuzik;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private VBox headerBox;

    @FXML
    private ScrollPane calendarScrollPane;

    private LocalDate currentMonday;
    private static String selectedAppointment;
    private static PanelLekarzaMainControler instance;
    private BazaWizyty bazaWizyty;

    @FXML
    public void initialize() {
        try {
            // Inicjalizacja podstawowych zmiennych
            currentMonday = LocalDate.now().with(DayOfWeek.MONDAY);
            instance = this;

            // Upewnij się, że ScrollPane jest poprawnie skonfigurowany
            if (calendarScrollPane != null) {
                calendarScrollPane.setFitToWidth(true);
                calendarScrollPane.setFitToHeight(false);
                calendarScrollPane.setPannable(true);
            }

            // Inicjalizacja bazy danych
            bazaWizyty = new BazaWizyty();

            // Aktualizacja kalendarza
            if (calendarGrid != null && headerBox != null) {
                updateCalendar();
            } else {
                System.out.println("Błąd: Nie znaleziono wymaganych komponentów GUI");
            }
        } catch (Exception e) {
            System.out.println("Błąd podczas inicjalizacji: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateCalendar() {
        calendarGrid.getChildren().clear();
        headerBox.getChildren().clear();

        LocalDate startOfWeek = currentMonday;
        LocalDate endOfWeek = currentMonday.plusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String weekRange = startOfWeek.format(formatter) + " - " + endOfWeek.format(formatter);
        Text weekText = new Text(weekRange);
        weekText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        headerBox.getChildren().add(weekText);

        String[] daysOfWeek = {"Pon", "Wt", "Śr", "Czw", "Pt", "Sob", "Nd"};
        for (int i = 0; i < 7; i++) {
            LocalDate currentDay = startOfWeek.plusDays(i);
            String dayHeader = daysOfWeek[i] + " " + currentDay.getDayOfMonth();
            Text dayText = new Text(dayHeader);
            calendarGrid.add(dayText, i + 1, 0);
        }

        for (int hour = 6; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 30) {
                int rowIndex = (hour - 6) * 2 + (minute == 30 ? 1 : 0) + 1;
                String time = String.format("%02d:%02d", hour, minute);
                calendarGrid.add(new Text(time), 0, rowIndex);
            }
        }

        List<BazaWizyty.Wizyta> confirmedAppointments = bazaWizyty.pobierzWizyty(MementoMori.idDoctor, "POTWIERDZONA");
        List<BazaWizyty.Wizyta> pendingAppointments = bazaWizyty.pobierzWizyty(MementoMori.idDoctor, "OCZEKUJACA");

        for (BazaWizyty.Wizyta wizyta : confirmedAppointments) {
            addAppointmentDot(wizyta, "#27ae60");
        }

        for (BazaWizyty.Wizyta wizyta : pendingAppointments) {
            addAppointmentDot(wizyta, "#f39c12");
        }

        calendarScrollPane.setFitToWidth(true);
        calendarScrollPane.setFitToHeight(true);
        calendarScrollPane.setStyle("-fx-border-color: gray; -fx-border-style: solid; -fx-border-radius: 5; -fx-background-radius: 5;-fx-background-color: #a5f0bd");
    }

    public static void refreshCalendar() {
        if (instance != null) {
            instance.updateCalendar();
        }
    }

   private void addAppointmentDot(BazaWizyty.Wizyta wizyta, String color) {
        LocalDateTime appointmentDateTime = wizyta.dataczas;

        if (!appointmentDateTime.toLocalDate().isBefore(currentMonday) &&
                !appointmentDateTime.toLocalDate().isAfter(currentMonday.plusDays(6))) {

            int dayColumn = appointmentDateTime.getDayOfWeek().getValue();

            int hourRow = (appointmentDateTime.getHour() - 6) * 2 +
                    (appointmentDateTime.getMinute() >= 30 ? 1 : 0) + 1;

            Button button = new Button();
            button.setStyle(
                    "-fx-background-color: " + color + ";"+
                            "-fx-background-radius: 50%; " +
                            "-fx-min-width: 25px; " +
                            "-fx-min-height: 25px; " +
                            "-fx-border-width: 0 "
            );
            button.setFocusTraversable(false);
            button.setOnAction(event -> showAppointmentDetails(wizyta));
            calendarGrid.add(button, dayColumn, hourRow);
        }
    }

    private void showAppointmentDetails(BazaWizyty.Wizyta wizyta) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String[] details = {
                "Data wizyty: " + wizyta.dataczas.format(dateFormatter),
                "Godzina wizyty: " + wizyta.dataczas.format(timeFormatter),
                "Pacjent: " + wizyta.pacjentName,
                "Status: " + wizyta.status.toLowerCase()
        };

        String info = String.join("\n", details);

        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText(null);
        alert.setTitle("Informacje o wizycie");
        alert.initStyle(StageStyle.UNDECORATED);

        ButtonType closeButton = new ButtonType("Zamknij", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().add(closeButton);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
                "-fx-background-color: #d5f1de;" +
                        "-fx-font-size: 14px;" +
                        "-fx-border-color: black;"

        );

        Node closeButtonNode = dialogPane.lookupButton(closeButton);
        closeButtonNode.setStyle(
                "-fx-background-color: #a5d8ff;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-radius: 5px;" +
                        "-fx-background-color-hover: #7ec8ff;" +
                        "-fx-background-color-pressed: #5eb8ff;"+
                        "-fx-border-color: black;"
        );

        alert.setContentText(info);

        dialogPane.setMaxWidth(MementoMori.WIDTH * MementoMori.scale * 0.8);
        dialogPane.setMaxHeight(MementoMori.HEIGHT * MementoMori.scale * 0.5);

        javafx.stage.Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnShown(event -> {
            window.setX(MementoMori.main_stage.getX() + (MementoMori.main_stage.getWidth() - window.getWidth()) / 2);
            window.setY(MementoMori.main_stage.getY() + (MementoMori.main_stage.getHeight() - window.getHeight()) / 2);
        });

        alert.showAndWait();
    }

    public void prevWeek(ActionEvent actionEvent) {
        currentMonday = currentMonday.minusWeeks(1);
        updateCalendar();
    }

    public void nextWeek(ActionEvent actionEvent) {
        currentMonday = currentMonday.plusWeeks(1);
        updateCalendar();
    }

    public void doHarmonogramuPracy(ActionEvent actionEvent) {
        MementoMori.navigateTo("Lekarz/HarmonogramPracy.fxml");
    }

    public void doZarzadzaniaWizytami(ActionEvent actionEvent) {
        MementoMori.navigateTo("Lekarz/ZarzadzajWizytami.fxml");
    }

    public void doMain(ActionEvent actionEvent) {
        MementoMori.navigateTo("Lekarz/PanelLekarzaMain.fxml");
    }

    public void doMojeKonto(ActionEvent actionEvent) {
        MementoMori.navigateTo("moje_konto/moje_konto.fxml");
    }
}

