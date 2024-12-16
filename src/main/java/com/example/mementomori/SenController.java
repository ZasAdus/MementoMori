package com.example.mementomori;

import javafx.fxml.FXML;

public class SenController {
    public static final String PATH = "sen/sen.fxml";

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void generateRaport() {
        MementoMori.navigateTo(SenRaportController.PATH);
    }
}
