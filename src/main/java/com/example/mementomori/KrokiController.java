package com.example.mementomori;
import com.example.mementomori.bazyDanych.BazaKroki;
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

public class KrokiController {

    public static final String PATH = "kroki/kroki.fxml";
    private ConfettiPane confettiPane;
    public Button KrokiZmienCel;
    public Button KrokiStatystyki;
    public Button KrokiDodajRecznie;
    public Integer cel;
    public Integer liczbaKrokow;
    public String currentUser;

    @FXML RoundProgressbar progress;

    @FXML private GridPane weekGrid;
    @FXML private Text weekLabel;

    private LocalDate currentWeekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);

    public void KrokiStatystyki(ActionEvent actionEvent) {
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

        BazaKroki.sprawdzLubUtworzDzien(currentUser);

        int[] dane = BazaKroki.pobierzDaneKroki(currentUser);
        liczbaKrokow = dane[0];
        cel = dane[1];

        progress.setProgress((double) liczbaKrokow / cel);
        progress.setProgressTitle("Cel");
        progress.setColor("#2f9e44");
        progress.setMin(liczbaKrokow.toString());
        progress.setMax(cel.toString() + " kroków");

        confettiPane = new ConfettiPane();
        confettiPane.setMouseTransparent(true);
        confettiPane.prefWidthProperty().bind(progress.widthProperty());
        confettiPane.prefHeightProperty().bind(progress.heightProperty());
        confettiPane.setVisible(false);

        ((Pane) progress.getParent()).getChildren().add(confettiPane);
    }

    public void KrokiZmienCel(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setGraphic(null);
        dialog.setHeaderText(null);
        dialog.setContentText("Wprowadź nowy cel:");

        // Centrowanie dialogu po pokazaniu
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

        dialog.getDialogPane().lookupButton(ButtonType.OK).setOnMouseEntered(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #4CAF50;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
                )
        );
        dialog.getDialogPane().lookupButton(ButtonType.OK).setOnMouseExited(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #4CAF50;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: null;"
                )
        );

        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setOnMouseEntered(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #f44336;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
                )
        );

        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setOnMouseExited(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #f44336;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: null;"
                )
        );

        Optional<String> wynik = dialog.showAndWait();
        wynik.ifPresent(newGoal -> {
            try {
                int nowyCel = Integer.parseInt(newGoal);
                cel = nowyCel;
                BazaKroki.zaktualizujDaneKroki(currentUser, liczbaKrokow, cel);
                progress.setProgress((double) liczbaKrokow / cel);
                progress.setMax(cel.toString() + " kroków");
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText(null);
                alert.setContentText("Wprowadzono niepoprawną wartość. Podaj liczbę całkowitą.");
                alert.initStyle(StageStyle.UNDECORATED);
                alert.setGraphic(null);

                // Centrowanie alertu po pokazaniu
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

    public void KrokiDodajRecznie(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setGraphic(null);
        dialog.setHeaderText(null);
        dialog.setContentText("Ile wykonałeś kroków:");

        // Centrowanie dialogu po pokazaniu
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

        dialog.getDialogPane().lookupButton(ButtonType.OK).setOnMouseEntered(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #4CAF50;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
                )
        );
        dialog.getDialogPane().lookupButton(ButtonType.OK).setOnMouseExited(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #4CAF50;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: null;"
                )
        );

        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setOnMouseEntered(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #f44336;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: innershadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 0);"
                )
        );

        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setOnMouseExited(e ->
                ((Button)e.getSource()).setStyle(
                        "-fx-background-color: #f44336;" +
                                "-fx-text-fill: black;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-color: gray;" +
                                "-fx-effect: null;"
                )
        );

        Optional<String> wynik = dialog.showAndWait();
        wynik.ifPresent(kroki -> {
            try {
                int poprzedniaLiczba = liczbaKrokow;
                liczbaKrokow += Integer.parseInt(kroki);
                progress.setProgress((double) liczbaKrokow / cel);
                progress.setMin(liczbaKrokow.toString());

                BazaKroki.zaktualizujDaneKroki(MementoMori.currentUser, liczbaKrokow, cel);

                if (poprzedniaLiczba < cel && liczbaKrokow >= cel) {
                    confettiPane.setVisible(true);
                    confettiPane.startAnimation();
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Błąd");
                alert.setHeaderText(null);
                alert.setContentText("Wprowadzono niepoprawną wartość. Podaj liczbę całkowitą.");
                alert.initStyle(StageStyle.UNDECORATED);
                alert.setGraphic(null);

                // Centrowanie alertu po pokazaniu
                Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                alertStage.setOnShown(e2 -> {
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
        LocalDate today = LocalDate.now();
        Map<LocalDate, int[]> weeklyData = BazaKroki.pobierzKrokiICeleWTygodniu(currentUser, weekStart);

        for (int i = 0; i < 7; i++) {
            String dayDetails = String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue());
            VBox dayBox = new VBox();
            dayBox.setStyle("-fx-border-color: gray; -fx-padding: 1.3px; -fx-alignment: center; -fx-background-color: #a5f0bd");

            Label dayLabel = new Label(dayDetails);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-background-color: #a5f0bd;");
            dayBox.getChildren().add(dayLabel);

            int[] data = weeklyData.getOrDefault(date, new int[]{0, cel});
            int steps = data[0];
            int goal = data[1];

            Label circleLabel = new Label();
            circleLabel.setMinSize(40, 40);
            circleLabel.setMaxSize(40, 40);
            circleLabel.setStyle("-fx-border-radius: 20px; -fx-background-radius: 20px; -fx-alignment: center; -fx-text-fill: white; -fx-font-weight: bold;");

            if (!date.isAfter(today)) {
                if (steps >= goal * 2) { // Cel przekroczony dwukrotnie
                    circleLabel.setText("🏆");
                    circleLabel.setStyle(circleLabel.getStyle() + "-fx-background-color: navy;");
                    Tooltip tooltip = new Tooltip( "Cel przekroczony dwukrotnie" + "\nKroki: " + steps + "\nCel: " + goal);
                    Tooltip.install(circleLabel, tooltip);
                } else if (steps >= goal) { // Cel osiągnięty
                    circleLabel.setText("✔");
                    circleLabel.setStyle(circleLabel.getStyle() + "-fx-background-color: green;");
                    Tooltip tooltip = new Tooltip( "Cel osiągnięty" + "\nKroki: " + steps + "\nCel: " + goal);
                    Tooltip.install(circleLabel, tooltip);
                } else { // Cel nieosiągnięty
                    circleLabel.setText("❌");
                    circleLabel.setStyle(circleLabel.getStyle() + "-fx-background-color: red;");
                    Tooltip tooltip = new Tooltip( "Cel nieosiągnięty" + "\nKroki: " + steps + "\nCel: " + goal);
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



