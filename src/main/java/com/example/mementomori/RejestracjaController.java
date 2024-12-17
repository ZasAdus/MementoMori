package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaRejestracja;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RejestracjaController {
    public TextField Imie;
    public Button GuzikLekarz;
    public Button GuzikPacjent;
    public Button GuzikLogowanie;
    public TextField Nazwisko;
    public TextField Email;
    public TextField NrTelefonu;
    public Button TypKonta;
    public Button GuzikKontynuuj;
    public TextField NazwaUzytkownika;
    public TextField Haslo;
    public TextField PowtorzoneHaslo;
    public Button Cofnij;
    public Button GuzikUtworzKonto;

    public static String login;

    public void RejestracjaLekarz(ActionEvent actionEvent) {
        //Do zrobienia
    }

    public void RejestracjaPacjent(ActionEvent actionEvent) {
        MementoMori.navigateTo("Rejestracja/RejestracjaDaneKonta.fxml");
    }

    public void PowrotDoEkranuLogowania(ActionEvent actionEvent) {
        MementoMori.navigateTo("Logowanie.fxml");
    }

    public void PowrotDoTypuKonta(ActionEvent actionEvent) {
        MementoMori.navigateTo("Rejestracja/RejestracjaTypKonta.fxml");
    }

    public void KontynujRejestrację(ActionEvent actionEvent) {
        if(NazwaUzytkownika.getText().isEmpty() || Haslo.getText().isEmpty() || PowtorzoneHaslo.getText().isEmpty()){
            //dodać jakiś błąd
            System.out.println("uzupełnij wszystkie pola");
            return;
        }else if(!Haslo.getText().equals(PowtorzoneHaslo.getText())){
            //dodać jakiś błąd
            System.out.println("hasła nie są takie same");
            return;
        }
        login = NazwaUzytkownika.getText();
        BazaRejestracja.insertLoginHaslo(NazwaUzytkownika.getText(), Haslo.getText());
        MementoMori.navigateTo("Rejestracja/RejestracjaDaneOsobowe.fxml");
    }

    public void Zarejestruj(ActionEvent actionEvent) {
        if(Imie.getText().isEmpty() || Nazwisko.getText().isEmpty() || NrTelefonu.getText().isEmpty() || Email.getText().isEmpty()){
            //dodać jakiś błąd
            System.out.println("uzupełnij wszystkie pola");
            return;
        }
        BazaRejestracja.insertDaneOsobowe(login, Imie.getText(), Nazwisko.getText(), NrTelefonu.getText(), Email.getText());
        System.out.println("dodano dane osobowe " + login + Imie.getText() + " " + Nazwisko.getText() + " " + NrTelefonu.getText() + " " + Email.getText());
        MementoMori.navigateTo("Logowanie.fxml");
    }

    public void CofanieDoDanychKonta(ActionEvent actionEvent) {
        MementoMori.navigateTo("Rejestracja/RejestracjaDaneKonta.fxml");
    }
}
