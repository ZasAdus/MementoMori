package com.example.mementomori;

import javafx.fxml.FXML;

public class SenRaportController {
    public static final String PATH = "sen/raport.fxml";

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void goLeki() {
        MementoMori.navigateTo(LekiController.MAIN_PATH);
    }
}
