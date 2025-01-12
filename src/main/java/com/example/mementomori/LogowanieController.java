package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaRejestracja;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LogowanieController {
    @FXML public TextField Login;
    @FXML public PasswordField haslo;
    @FXML  public Button guzikUtworzKonto;
    @FXML public Button goMenu;

    public void initialize(){
        Login.setPromptText("Wprowadź nazwę użytkownika");
        haslo.setPromptText("Wprowadź hasło");
    }

    @FXML
    public void clearHaslo(ActionEvent actionEvent) {
        haslo.setText("");
    }
    public void clearLogin(ActionEvent actionEvent) {
        Login.setText("");
    }

    @FXML
    public void stworzKonto(ActionEvent actionEvent) {
        MementoMori.navigateTo("Rejestracja/RejestracjaTypKonta.fxml");
    }

    public void zaloguj(ActionEvent actionEvent) {
        String loginText = Login.getText();
        if (BazaRejestracja.userExists(loginText) && BazaRejestracja.isPasswordCorrect(loginText, haslo.getText())) {
            MementoMori.currentUser = loginText;
            MementoMori.isDoctor = BazaRejestracja.isDoctor(loginText);
            if (MementoMori.isDoctor) {
                MementoMori.navigateTo("Lekarz/PanelLekarzaMain.fxml");
            } else {
                MementoMori.returnHome();
            }
        } else {
            System.out.println("Nieprawidłowy login lub hasło");
        }
    }
}
