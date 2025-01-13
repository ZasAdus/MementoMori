package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaMojeKonto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ZmianaDanychController {

    public static final String PATH = "moje_konto/zmiana_danych.fxml";

    @FXML private TextField newEmail;
    @FXML private TextField newNrTelefonu;

    @FXML
    public void zapiszZmiany(ActionEvent event) {
        String email = newEmail.getText();
        String nrTelefonu = newNrTelefonu.getText();

        if (!email.isEmpty() && !nrTelefonu.isEmpty()) {
            BazaMojeKonto.updateUserData(MementoMori.currentUser, null, null, email, nrTelefonu);
            System.out.println("Dane użytkownika zostały zaktualizowane.");
            MementoMori.navigateTo("moje_konto/moje_konto.fxml");
        } else {
            System.out.println("Wszystkie pola muszą być wypełnione.");
        }
    }

    @FXML
    public void anuluj(ActionEvent event) {
        MementoMori.navigateTo("moje_konto/moje_konto.fxml");
    }

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }
}
