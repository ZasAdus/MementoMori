package com.example.mementomori;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class LogowanieController {
    @FXML public TextField Login;
    @FXML public TextField haslo;
    @FXML  public Button guzikUtworzKonto;
    @FXML public Button goMenu;

    public void initialize(){
        Login.setText("exampleUserName");
        haslo.setText("***************");
    }

    @FXML
    public void clear(ActionEvent actionEvent) {
        Login.setText("");
        haslo.setText("");
    }

    @FXML
    public void stworzKonto(ActionEvent actionEvent) {
        //Trzeba dorobić rejestrację
    }

    @FXML
    public void zaloguj(ActionEvent actionEvent) {
        MementoMori.returnHome();
    }
}
