package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaMojeKonto;
import com.example.mementomori.bazyDanych.BazaRejestracja;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class MojeKontoController {

    public static final String PATH = "moje_konto/moje_konto.fxml";
    @FXML private Label labelLogin;
    @FXML private Label labelImie;
    @FXML private Label labelNazwisko;
    @FXML private Label labelEmail;
    @FXML private Label labelNrTelefonu;

    public void initialize() {
        String currentUser = MementoMori.currentUser;

        User user = BazaMojeKonto.getUserData(currentUser);

        if (user != null) {
            labelLogin.setText(user.getLogin());
            labelImie.setText(user.getImie());
            labelNazwisko.setText(user.getNazwisko());
            labelEmail.setText(user.getEmail());
            labelNrTelefonu.setText(user.getNrTelefonu());
        } else {
            System.out.println("Nie znaleziono danych użytkownika w bazie.");
        }
    }

    @FXML
    public void goHome() {
        if (MementoMori.isDoctor){
            MementoMori.navigateTo("Lekarz/PanelLekarzaMain.fxml");
        }else{
            MementoMori.returnHome();
        }
    }

    @FXML
    public void zmienDane(ActionEvent actionEvent) {
        MementoMori.navigateTo("moje_konto/zmiana_danych.fxml");
    }

    @FXML
    public void zmienHaslo(ActionEvent actionEvent) {
        MementoMori.navigateTo("moje_konto/zmiana_hasla.fxml");
    }

    @FXML
    public void wyloguj(ActionEvent actionEvent) {
        MementoMori.currentUser = null;
        MementoMori.navigateTo("Logowanie.fxml");
    }


}


