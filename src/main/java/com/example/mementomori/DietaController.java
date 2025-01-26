package com.example.mementomori;
import com.example.mementomori.bazyDanych.BazaDieta;
import com.example.mementomori.custom_elements.RoundProgressbar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
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
        dialog.setTitle("Zmiana Celu");
        dialog.setHeaderText(null);
        dialog.setContentText("Wprowadź nowy cel:");

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
                alert.showAndWait();
            }
        });
    }

    public void DietaDodajRecznie(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dodaj kalorie");
        dialog.setHeaderText(null);
        dialog.setContentText("Wprowadź ile kcal spożyłeś:");

        Optional<String> wynik = dialog.showAndWait();
        wynik.ifPresent(x -> {
            liczbaKalori += Integer.parseInt(x);
            DietaProgress.setProgress((double) liczbaKalori / cel);
            DietaProgress.setMin(liczbaKalori.toString());

            // Zapisz zmienione dane do bazy
            BazaDieta.zaktualizujDaneDieta(MementoMori.currentUser, liczbaKalori, cel);
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
        LocalDate date = weekStart;

        Map<LocalDate, int[]> weeklyData = BazaDieta.pobierzKalICeleWTygodniu(currentUser, weekStart);
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            String dayDetails = String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue());
            VBox dayBox = new VBox();
            dayBox.setStyle("-fx-border-color: gray; -fx-padding: 1.3px; -fx-alignment: center;");

            Label dayLabel = new Label(dayDetails);
            dayLabel.setStyle("-fx-font-weight: bold;");
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