package com.example.mementomori;
import com.example.mementomori.bazyDanych.BazaDieta;
import com.example.mementomori.custom_elements.RoundProgressbar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public class DietaController {
    public static final String PATH = "Dieta.fxml";

    public Button DietaZmienCel;
    public Button DietaStatystyki;
    public Button DietaDodajRecznie;
    public Integer cel;
    public Integer liczbaKalori;
    @FXML RoundProgressbar DietaProgress;
    public String currentUser;

    @FXML private GridPane weekGrid;
    @FXML private Text weekLabel;

    private LocalDate currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);

    public void DietaStatystyki(ActionEvent actionEvent) {
        showStats();
        updateWeekGrid(weekGrid, LocalDate.now().with(java.time.DayOfWeek.MONDAY));
        updateWeekLabel(weekLabel, LocalDate.now().with(java.time.DayOfWeek.MONDAY));
    }

    @FXML
    private StackPane statsOverlay;

    public void showStats() {
        statsOverlay.setVisible(true);
        for (javafx.scene.Node child : statsOverlay.getChildren()) {
            child.setStyle("-fx-background-color: #a5f0bd; -fx-border-color: gray; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-max-width: " + MementoMori.WIDTH *0.95 + "; -fx-max-height: 300px");
        }
    }

    public void hideStats() {
        statsOverlay.setVisible(false);
    }

    public void hideStats(ActionEvent actionEvent) {
        hideStats();
    }

    public void prevWeek() {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        updateWeekLabel(weekLabel, currentWeekStart);
        updateWeekGrid(weekGrid, currentWeekStart);
    }

    public void nextWeek(ActionEvent actionEvent) {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        updateWeekLabel(weekLabel, currentWeekStart);
        updateWeekGrid(weekGrid, currentWeekStart);
    }

    public void initialize() {
        currentUser = MementoMori.currentUser;

        BazaDieta.sprawdzLubUtworzDzien(currentUser);

        int[] dane = BazaDieta.pobierzDaneDieta(currentUser);
        liczbaKalori = dane[0];
        cel = dane[1];

        DietaProgress.setProgress((double) liczbaKalori / cel);
        DietaProgress.setProgressTitle("Kalorie");
        DietaProgress.setColor("orange");
        DietaProgress.setMin(liczbaKalori.toString());
        DietaProgress.setMax(cel.toString() + " kcal");
    }

    public void DietaZmienCel(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setGraphic(null);
        dialog.setHeaderText(null);
        dialog.setContentText("Wprowadź nowy cel:");

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.setOnShown(e -> {
            dialogStage.setX(MementoMori.main_stage.getX() + (MementoMori.main_stage.getWidth() - dialogStage.getWidth()) / 2);
            dialogStage.setY(MementoMori.main_stage.getY() + (MementoMori.main_stage.getHeight() - dialogStage.getHeight()) / 2);
        });

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #a5f0bd; " +
                "-fx-padding: 10; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: gray; " +
                "-fx-border-radius: 5; " +
                "-fx-border-width: 1;");

        dialogPane.lookupButton(ButtonType.OK)
                .setStyle("-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;");
        dialogPane.lookupButton(ButtonType.CANCEL)
                .setStyle("-fx-background-color: #f44336;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;");

        Optional<String> wynik = dialog.showAndWait();
        wynik.ifPresent(newGoal -> {
            try {
                int nowyCel = Integer.parseInt(newGoal);
                cel = nowyCel;
                BazaDieta.zaktualizujDaneDieta(currentUser, liczbaKalori, cel);
                DietaProgress.setProgress((double) liczbaKalori / cel);
                DietaProgress.setMax(cel.toString() + " kcal");
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText(null);
                alert.setContentText("Wprowadzono niepoprawną wartość. Podaj liczbę całkowitą.");
                alert.initStyle(StageStyle.UNDECORATED);
                alert.setGraphic(null);

                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.setOnShown(e2-> {
                    alertStage.setX(MementoMori.main_stage.getX() + (MementoMori.main_stage.getWidth() - alertStage.getWidth()) / 2);
                    alertStage.setY(MementoMori.main_stage.getY() + (MementoMori.main_stage.getHeight() - alertStage.getHeight()) / 2);
                });

                DialogPane alertPane = alert.getDialogPane();
                alertPane.setStyle("-fx-background-color: #a5f0bd; -fx-border-color: gray; " +
                        "-fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-max-width: 300");
                alertPane.lookupButton(ButtonType.OK)
                        .setStyle("-fx-background-color: #2f9e44; -fx-text-fill: white; " +
                                "-fx-background-radius: 5; -fx-border-radius: 5;");

                alert.showAndWait();
            }
        });
    }

    public void DietaDodajRecznie(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setGraphic(null);
        dialog.setHeaderText(null);
        dialog.setContentText("Ile kcal spożyłeś:");

        Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        dialogStage.setOnShown(e -> {
            dialogStage.setX(MementoMori.main_stage.getX() + (MementoMori.main_stage.getWidth() - dialogStage.getWidth()) / 2);
            dialogStage.setY(MementoMori.main_stage.getY() + (MementoMori.main_stage.getHeight() - dialogStage.getHeight()) / 2);
        });

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #a5f0bd; " +
                "-fx-padding: 10; " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: gray; " +
                "-fx-border-radius: 5; " +
                "-fx-border-width: 1;" );

        dialogPane.lookupButton(ButtonType.OK)
                .setStyle("-fx-background-color: #4CAF50;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;");
        dialogPane.lookupButton(ButtonType.CANCEL)
                .setStyle("-fx-background-color: #f44336;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: gray;");

        Optional<String> wynik = dialog.showAndWait();
        wynik.ifPresent(x -> {
            try {
                liczbaKalori += Integer.parseInt(x);
                DietaProgress.setProgress((double) liczbaKalori / cel);
                DietaProgress.setMin(liczbaKalori.toString());

                BazaDieta.zaktualizujDaneDieta(MementoMori.currentUser, liczbaKalori, cel);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText(null);
                alert.setContentText("Wprowadzono niepoprawną wartość. Podaj liczbę całkowitą.");
                alert.initStyle(StageStyle.UNDECORATED);
                alert.setGraphic(null);

                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.setOnShown(e2-> {
                    alertStage.setX(MementoMori.main_stage.getX() + (MementoMori.main_stage.getWidth() - alertStage.getWidth()) / 2);
                    alertStage.setY(MementoMori.main_stage.getY() + (MementoMori.main_stage.getHeight() - alertStage.getHeight()) / 2);
                });
                DialogPane alertPane = alert.getDialogPane();
                alertPane.setStyle("-fx-background-color: #a5f0bd; -fx-border-color: gray; " +
                        "-fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-max-width: 300");
                alertPane.lookupButton(ButtonType.OK)
                        .setStyle("-fx-background-color: #2f9e44; -fx-text-fill: white; " +
                                "-fx-background-radius: 5; -fx-border-radius: 5;");

                alert.showAndWait();
            }
        });
    }

    private void updateWeekLabel(Text weekLabel, LocalDate weekStart) {
        LocalDate weekEnd = weekStart.plusDays(6);
        String start = String.format("%02d/%02d", weekStart.getDayOfMonth(), weekStart.getMonthValue());
        String end = String.format("%02d/%02d", weekEnd.getDayOfMonth(), weekEnd.getMonthValue());
        int year = weekStart.getYear();
        weekLabel.setText("Tydzień: " + start + " - " + end + " (" + year + ")");
    }

    private void updateWeekGrid(GridPane weekGrid, LocalDate weekStart) {
        weekGrid.getChildren().clear();
        weekGrid.setStyle("-fx-background-color: #a5f0bd;");
        LocalDate date = weekStart;

        Map<LocalDate, int[]> weeklyData = BazaDieta.pobierzKalICeleWTygodniu(currentUser, weekStart);
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            String dayDetails = String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue());
            VBox dayBox = new VBox();
            dayBox.setStyle("-fx-border-color: gray; -fx-padding: 1.3px; -fx-alignment: center; -fx-background-color: #a5f0bd");

            Label dayLabel = new Label(dayDetails);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #a5f0bd;");
            dayBox.getChildren().add(dayLabel);

            int[] data = weeklyData.getOrDefault(date, new int[]{0, cel});
            int kal = data[0];
            int goal = data[1];

            Label circleLabel = new Label();
            circleLabel.setMinSize(40, 40);
            circleLabel.setMaxSize(40, 40);
            circleLabel.setStyle("-fx-border-radius: 20px; -fx-background-radius: 20px; -fx-alignment: center; -fx-text-fill: white; -fx-font-weight: bold;");

            if (!date.isAfter(today)) {
                if (kal >= goal) {
                    circleLabel.setText("✔");
                    circleLabel.setStyle(circleLabel.getStyle() + "-fx-background-color: green;");
                    Tooltip tooltip = new Tooltip("Cel osiągnięty\nKalorie: " + kal + "\nCel: " + goal);
                    Tooltip.install(circleLabel, tooltip);
                } else {
                    circleLabel.setText("❌");
                    circleLabel.setStyle(circleLabel.getStyle() + "-fx-background-color: red;");
                    Tooltip tooltip = new Tooltip("Cel nieosiągnięty\nKalorie: " + kal + "\nCel: " + goal);
                    Tooltip.install(circleLabel, tooltip);
                }
            } else {
                circleLabel.setText("");
                circleLabel.setStyle(circleLabel.getStyle() + "-fx-background-color: lightgray;");
            }

            dayBox.getChildren().add(circleLabel);

            weekGrid.add(dayBox, i, 0);
            date = date.plusDays(1);
        }
    }

    @FXML
    public void ReturnHome() {
        MementoMori.returnHome();
    }

}