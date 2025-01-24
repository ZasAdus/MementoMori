package com.example.mementomori;
import com.example.mementomori.bazyDanych.BazaKroki;
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

public class KrokiController {
    public static final String PATH = "kroki/kroki.fxml";

    public Button KrokiZmienCel;
    public Button KrokiStatystyki;
    public Button KrokiDodajRecznie;
    public Integer cel;
    public Integer liczbaKrokow;
    public Text KrokiCel;
    public String currentUser;

    @FXML RoundProgressbar progress;


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
        // Wyświetlenie dialogu do wprowadzenia nowego celu
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Zmiana Celu");
        dialog.setHeaderText(null);
        dialog.setContentText("Wprowadź nowy cel:");

        Optional<String> wynik = dialog.showAndWait();
        wynik.ifPresent(newGoal -> {
            try {
                // Parsowanie nowego celu z tekstu na liczbę
                int nowyCel = Integer.parseInt(newGoal);

                // Aktualizacja zmiennej `cel`
                cel = nowyCel;

                // Zaktualizuj cel w bazie danych dla zalogowanego użytkownika i bieżącej daty
                BazaKroki.zaktualizujDaneKroki(currentUser, liczbaKrokow, cel);

                // Aktualizacja progresu na interfejsie
                progress.setProgress((double) liczbaKrokow / cel);
                progress.setMax(cel.toString() + " kroków");

            } catch (NumberFormatException e) {
                // Obsługa błędu w przypadku, gdy użytkownik wprowadzi niepoprawne dane
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

            // Zapisz zmienione dane do bazy
            BazaKroki.zaktualizujDaneKroki(MementoMori.currentUser, liczbaKrokow, cel);
        });
    }

    public void KrokiStatystyki(ActionEvent actionEvent) {
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

            Label stepsLabel = new Label(steps + "");
            stepsLabel.setStyle("-fx-text-fill: blue;");
            dayBox.getChildren().add(stepsLabel);

            Label goalLabel = new Label();
            if (steps >= goal) {
                goalLabel.setText("Cel osiągnięty! 🎉");
                goalLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                goalLabel.setText(goal + "");
                goalLabel.setStyle("-fx-text-fill: red;");
            }
            dayBox.getChildren().add(goalLabel);

            weekGrid.add(dayBox, i, 0);
            date = date.plusDays(1);
        }
    }

    private Button createStatusButton(LocalDate date) {
        String color;
        switch (date.getDayOfWeek()) {
            case MONDAY: color = "#00FF00"; break;
            case WEDNESDAY: color = "#FFFF00"; break;
            case FRIDAY: color = "#FF0000"; break;
            default: color = "#00FF00"; break;
        }

        Button button = new Button();
        button.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 50%; -fx-min-width: 20px; -fx-min-height: 20px; -fx-max-width: 20px; -fx-max-height: 20px;");
        return button;
    }

    @FXML
    public void ReturnHome() {
        MementoMori.returnHome();
    }

}



