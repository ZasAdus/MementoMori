package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaRejestracja;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

public class RejestracjaController implements Initializable {
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
    public Label komunikaty;

    public static String login;
    public Button cofnijDoTypKontaButton;
    public Button przejdzDoOsobowychButton;

    @FXML
    public ChoiceBox<String> specjalizcjaChoice;

    @FXML
    public TextField adresField;

    @FXML
    public Button mapButton;

    private final String[] specjalizacje = {
            "Alergolog",
            "Dermatolog",
            "Kardiolog",
            "Okulista",
            "Psychiatra",
            "Reumatolog",
            "Stomatolog"
    };

    public static boolean isDoctor = false;
    public static String tempSpecjalizacja;
    public static String tempAdres;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (specjalizcjaChoice != null) {
            specjalizcjaChoice.getItems().addAll(specjalizacje);
            specjalizcjaChoice.setValue(specjalizacje[0]); // Set default value
        }

    }

    public void RejestracjaLekarz(ActionEvent actionEvent) {
        isDoctor = true;
        MementoMori.navigateTo("Rejestracja/RejestracjaDaneZawodowe.fxml");
    }

    public void RejestracjaDaneOsobowe(ActionEvent actionEvent) {
        if (!isDoctor) {
            MementoMori.navigateTo("Rejestracja/RejestracjaDaneKonta.fxml");
            return;
        }
        String specjalizacja = specjalizcjaChoice.getValue();
        String adres = adresField.getText();

        System.out.println(specjalizacja);
        System.out.println(adres);

        if (specjalizacja == null || adres.isEmpty()) {
            System.out.println("Proszę wypełnić wszystkie pola");
            return;
        }
        tempSpecjalizacja = specjalizacja;
        tempAdres = adres;

        MementoMori.navigateTo("Rejestracja/RejestracjaDaneKonta.fxml");
    }


    public void PowrotDoEkranuLogowania(ActionEvent actionEvent) {
        MementoMori.navigateTo("Logowanie.fxml");
    }

    public void PowrotDoTypuKonta(ActionEvent actionEvent) {
        isDoctor = false;
        MementoMori.navigateTo("Rejestracja/RejestracjaTypKonta.fxml");
    }

    public void KontynujRejestrację(ActionEvent actionEvent) {
        if(NazwaUzytkownika.getText().isEmpty() || Haslo.getText().isEmpty() || PowtorzoneHaslo.getText().isEmpty()){
            komunikaty.setText("Uzupełnij wszystkie pola");
            return;
        }else if(!Haslo.getText().equals(PowtorzoneHaslo.getText())){
            komunikaty.setText("Hasła nie są takie same");
            return;
        }
        login = NazwaUzytkownika.getText();
        if (BazaRejestracja.userExists(login)) {
            komunikaty.setText("Użytkownik o podanym loginie już istnieje");
            return;
        }
        if(isDoctor){
            System.out.println(tempSpecjalizacja);
            System.out.println(tempAdres);
            BazaRejestracja.insertLoginHaslo(login, Haslo.getText());
            BazaRejestracja.insertDaneZawodowe(login, tempSpecjalizacja, tempAdres);
            isDoctor = false;
            tempAdres = "";
            tempSpecjalizacja = "";
        }else {
            BazaRejestracja.insertLoginHaslo(login, Haslo.getText());
        }
        MementoMori.navigateTo("Rejestracja/RejestracjaDaneOsobowe.fxml");
    }

    public void Zarejestruj(ActionEvent actionEvent) {
        if(Imie.getText().isEmpty() || Nazwisko.getText().isEmpty() || NrTelefonu.getText().isEmpty() || Email.getText().isEmpty()){
            System.out.println("uzupełnij wszystkie pola");
            return;
        }
        BazaRejestracja.insertDaneOsobowe(login, Imie.getText(), Nazwisko.getText(), Email.getText(), NrTelefonu.getText());
        System.out.println("dodano dane osobowe " + login + Imie.getText() + " " + Nazwisko.getText() + " " + NrTelefonu.getText() + " " + Email.getText());
        MementoMori.navigateTo("Logowanie.fxml");
    }

    public void CofanieDoDanychKonta(ActionEvent actionEvent) {
        MementoMori.navigateTo("Rejestracja/RejestracjaDaneKonta.fxml");
    }

    public void MapaZWyborem(ActionEvent actionEvent) {
        try {
            Desktop.getDesktop().browse(new URI("https://www.google.com/maps"));
            //można skopiować i dodać do adresField
        } catch (Exception e) {
            System.err.println("Nie udało się otworzyć Map Google: " + e.getMessage());
        }
    }
}