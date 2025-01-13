package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaRejestracja;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

public class ZmianaHaslaController {
    public static final String PATH = "moje_konto/zmiana_hasla.fxml";

    @FXML private PasswordField currentPassword;
    @FXML private PasswordField newPassword;
    @FXML private PasswordField confirmPassword;

    @FXML
    public void zmienHaslo(ActionEvent event) {
        String current = currentPassword.getText();
        String newPass = newPassword.getText();
        String confirmPass = confirmPassword.getText();

        if (!BazaRejestracja.isPasswordCorrect(MementoMori.currentUser, current)) {
            System.out.println("Błędne aktualne hasło.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            System.out.println("Nowe hasło i potwierdzenie nie są zgodne.");
            return;
        }

        if (newPass.isEmpty()) {
            System.out.println("Nowe hasło nie może być puste.");
            return;
        }

        BazaRejestracja.updatePassword(MementoMori.currentUser, newPass);
        System.out.println("Hasło zostało zmienione.");
        MementoMori.navigateTo("moje_konto/moje_konto.fxml");
    }

    @FXML
    public void anuluj(ActionEvent event) {
        MementoMori.navigateTo("moje_konto/moje_konto.fxml");
    }
}
