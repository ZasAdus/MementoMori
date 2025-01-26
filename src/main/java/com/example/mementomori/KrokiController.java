package com.example.mementomori;
import com.example.mementomori.bazyDanych.BazaKroki;
import com.example.mementomori.custom_elements.RoundProgressbar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

public class KrokiController {
    public static final String PATH = "kroki/kroki.fxml";

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
    }

    public void KrokiZmienCel(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Zmiana Celu");
        dialog.setHeaderText(null);
        dialog.setContentText("Wprowadź nowy cel:");

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
                alert.showAndWait();
            }
        });
    }

    public void KrokiDodajRecznie(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Dodaj kroki");
        dialog.setHeaderText(null);
        dialog.setContentText("Wprowadź ile kroków przeszedłeś:");

        Optional<String> wynik = dialog.showAndWait();
        wynik.ifPresent(kroki -> {
            liczbaKrokow += Integer.parseInt(kroki);
            progress.setProgress((double) liczbaKrokow / cel);
            progress.setMin(liczbaKrokow.toString());

            BazaKroki.zaktualizujDaneKroki(MementoMori.currentUser, liczbaKrokow, cel);
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

        LocalDate today = LocalDate.now();
        Map<LocalDate, int[]> weeklyData = BazaKroki.pobierzKrokiICeleWTygodniu(currentUser, weekStart);

        for (int i = 0; i < 7; i++) {
            String dayDetails = String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue());
            VBox dayBox = new VBox();
            dayBox.setStyle("-fx-border-color: gray; -fx-padding: 1.3px; -fx-alignment: center;");

            Label dayLabel = new Label(dayDetails);
            dayLabel.setStyle("-fx-font-weight: bold;");
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



