package com.example.mementomori;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LogowanieController {
    public TextField Login;
    public TextField haslo;
    public Button guzikUtworzKonto;
    public Button goMenu;

    public void init(){
        Login.setText("exampleUserName");
        haslo.setText("***************");
    }

    public void clear(ActionEvent actionEvent) {
        Login.setText("");
        haslo.setText("");
    }

    public void stworzKonto(ActionEvent actionEvent) {
        //Trzeba dorobić rejestrację
    }

    public void zaloguj(ActionEvent actionEvent) {

    }
}
