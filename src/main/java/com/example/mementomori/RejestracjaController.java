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
    public TextField ulica;
    public TextField numer;
    public TextField miasto;

    public Button cofnijDoTypKontaButton;
    public Button przejdzDoOsobowychButton;

    @FXML
    public ChoiceBox<String> specjalizcjaChoice;

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
    public static String login;
    public static String tempHaslo;
    public static String tempSpecjalizacja;
    public static String tempMiasto;
    public static String tempUlica;
    public static String tempNumer;

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
        String m = miasto.getText();
        String u = ulica.getText();
        String n = numer.getText();

        if (specjalizacja == null || m.isEmpty() || u.isEmpty() || n.isEmpty()) {
            System.out.println("Proszę wypełnić wszystkie pola");
            return;
        }
        tempSpecjalizacja = specjalizacja;
        tempMiasto = m;
        tempUlica = u;
        tempNumer = n;
        System.out.println(tempSpecjalizacja);
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
        tempHaslo = Haslo.getText();
        if (BazaRejestracja.userExists(login)) {
            komunikaty.setText("Użytkownik o podanym loginie już istnieje");
            return;
        }
        System.out.println(tempSpecjalizacja);
        MementoMori.navigateTo("Rejestracja/RejestracjaDaneOsobowe.fxml");
    }

    public void Zarejestruj(ActionEvent actionEvent) {
        if(Imie.getText().isEmpty() || Nazwisko.getText().isEmpty() || NrTelefonu.getText().isEmpty() || Email.getText().isEmpty()){
            System.out.println("uzupełnij wszystkie pola");
            return;
        }
        if(isDoctor){
            System.out.println(tempSpecjalizacja);
            BazaRejestracja.insertDaneZawodowe(login, tempSpecjalizacja, tempMiasto, tempUlica, tempNumer);
        }
        BazaRejestracja.insertDaneOsobowe(login, Imie.getText(), Nazwisko.getText(), Email.getText(), NrTelefonu.getText());
        BazaRejestracja.insertLoginHaslo(login, tempHaslo);
        MementoMori.navigateTo("Logowanie.fxml");
        isDoctor = false;
    }

    public void CofanieDoDanychKonta(ActionEvent actionEvent) {
        MementoMori.navigateTo("Rejestracja/RejestracjaDaneKonta.fxml");
    }

}