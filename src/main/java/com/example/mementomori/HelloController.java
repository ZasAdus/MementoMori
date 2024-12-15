package com.example.mementomori;

import com.dlsc.formsfx.model.structure.IntegerField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.File;
import java.util.Optional;

public class HelloController {
    public Button DietaZmienCel;
    public Button DietaStatystyki;
    public Button DietaDodajRecznie;
    public Button DietaHome;
    public ProgressBar DietaProgres;
    public Integer cel = 2000;
    public Integer liczbaKalori = 300;
    public Text DietaKalorieCel;

    File cssFile = new File("C:\\Users\\dawid\\IdeaProjects\\MementoMori\\src\\main\\css\\progressBar.css");

    void init() {
        DietaProgres.getStyleClass().add("circular-progress-bar");
        DietaProgres.getStylesheets().add(cssFile.toURI().toString());
        DietaProgres.setProgress((double) liczbaKalori / cel);
        DietaKalorieCel.setText(liczbaKalori + "/" + cel + " kcal");

    }

    public void DietaZmienCel(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Zmiana Celu");
        dialog.setHeaderText(null);
        dialog.setContentText("Wprowadź nowy cel:");
        Optional<String> wynik = dialog.showAndWait();
        wynik.ifPresent(iloscKalori -> {
            cel = Integer.parseInt(iloscKalori);
            DietaProgres.setProgress((double) liczbaKalori / cel);
            DietaKalorieCel.setText(liczbaKalori + "/" + cel + " kcal");
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
            DietaProgres.setProgress((double) liczbaKalori / cel);
            DietaKalorieCel.setText(liczbaKalori + "/" + cel + " kcal");
        });
    }

    public void DietaStatystki(ActionEvent actionEvent) {
    }

    public void ReturnHome(ActionEvent actionEvent) {
    }

    public void DietaStatystyki(ActionEvent actionEvent) {
    }
}



