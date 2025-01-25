package com.example.mementomori;
import com.example.mementomori.bazyDanych.BazaDieta;
import com.example.mementomori.custom_elements.RoundProgressbar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
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

    public void DietaStatystyki(ActionEvent actionEvent) {
        Stage statsStage = new Stage();
        statsStage.initStyle(StageStyle.UNDECORATED);
        statsStage.setWidth(420);

        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-padding: 20px; -fx-font-size: 14px; -fx-border-color: black; -fx-border-width: 1px;");

        final LocalDate[] currentWeekStart = {LocalDate.now().with(java.time.DayOfWeek.MONDAY)};

        Text weekLabel = new Text();
        updateWeekLabel(weekLabel, currentWeekStart[0]);

        GridPane weekGrid = new GridPane();
        weekGrid.setHgap(10);
        weekGrid.setVgap(10);
        weekGrid.setAlignment(Pos.CENTER);
        updateWeekGrid(weekGrid, currentWeekStart[0]);

        Button prevWeekButton = new Button("⮪");
        prevWeekButton.setPrefSize(72, 20);
        prevWeekButton.setFont(new Font(15));
        Button nextWeekButton = new Button("⮩");
        nextWeekButton.setPrefSize(72, 20);
        nextWeekButton.setFont(new Font(15));

        prevWeekButton.setOnAction(e -> {
            LocalDate newWeekStart = currentWeekStart[0].minusWeeks(1);
            updateWeekLabel(weekLabel, newWeekStart);
            updateWeekGrid(weekGrid, newWeekStart);
            currentWeekStart[0] = newWeekStart;
        });

        nextWeekButton.setOnAction(e -> {
            LocalDate newWeekStart = currentWeekStart[0].plusWeeks(1);
            updateWeekLabel(weekLabel, newWeekStart);
            updateWeekGrid(weekGrid, newWeekStart);
            currentWeekStart[0] = newWeekStart;
        });

        HBox navigationButtons = new HBox(10, prevWeekButton, weekLabel, nextWeekButton);
        navigationButtons.setStyle("-fx-alignment: center;");
        HBox.setHgrow(weekLabel, Priority.ALWAYS);
        weekLabel.setStyle("-fx-alignment: center; -fx-font-weight: bold;");

        // Create the close button (X) in the top-right corner
        Button closeButton = new Button("X");
        closeButton.setFont(new Font(18));
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-border-width: 0;");
        closeButton.setOnAction(e -> statsStage.close());

        // Create a container for the X button and position it at the top-right corner
        StackPane topRightPane = new StackPane(closeButton);
        topRightPane.setStyle("-fx-alignment: top-right;");

        // Wrap the navigation buttons and close button in a VBox
        VBox topBox = new VBox(topRightPane, navigationButtons);
        topBox.setStyle("-fx-alignment: top-left;");

        VBox bottomBox = new VBox();
        bottomBox.setStyle("-fx-alignment: center; -fx-padding: 10px;");

        layout.setTop(topBox);
        layout.setCenter(weekGrid);
        layout.setBottom(bottomBox);

        Scene scene = new Scene(layout);
        statsStage.setScene(scene);
        statsStage.setResizable(false);
        statsStage.show();
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

            if (kal >= goal) { // Cel osiągnięty
                circleLabel.setText("✔");
                circleLabel.setStyle(circleLabel.getStyle() + "-fx-background-color: green;");
                Tooltip tooltip = new Tooltip( "Cel osiągnięty" + "\nKalorie: " + kal + "\nCel: " + goal);
                Tooltip.install(circleLabel, tooltip);
            } else { // Cel nieosiągnięty
                circleLabel.setText("❌");
                circleLabel.setStyle(circleLabel.getStyle() + "-fx-background-color: red;");
                Tooltip tooltip = new Tooltip( "Cel nieosiągnięty" + "\nKalorie: " + kal + "\nCel: " + goal);
                Tooltip.install(circleLabel, tooltip);
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