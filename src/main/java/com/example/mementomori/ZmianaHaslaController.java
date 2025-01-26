package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaRejestracja;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;

public class ZmianaHaslaController {
    public static final String PATH = "moje_konto/zmiana_hasla.fxml";

    @FXML private PasswordField currentPassword;
    @FXML private PasswordField newPassword;
    @FXML private PasswordField confirmPassword;
    @FXML private Text ErrorMessage;

    @FXML
    public void zmienHaslo(ActionEvent event) {
        String current = currentPassword.getText();
        String newPass = newPassword.getText();
        String confirmPass = confirmPassword.getText();

        if (!BazaRejestracja.isPasswordCorrect(MementoMori.currentUser, current)) {
            ErrorMessage.setText("Błędne aktualne hasło.");
            ErrorMessage.setVisible(true);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            ErrorMessage.setText("Nowe hasło i potwierdzenie nie są zgodne.");
            ErrorMessage.setVisible(true);
            return;
        }

        if (newPass.isEmpty()) {
            ErrorMessage.setText("Nowe hasło nie może być puste.");
            ErrorMessage.setVisible(true);
            return;
        }

        BazaRejestracja.updatePassword(MementoMori.currentUser, newPass);
        MementoMori.navigateTo("moje_konto/moje_konto.fxml");
    }

    @FXML
    public void anuluj(ActionEvent event) {
        MementoMori.navigateTo("moje_konto/moje_konto.fxml");
    }
}
