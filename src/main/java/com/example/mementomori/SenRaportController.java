package com.example.mementomori;

import javafx.fxml.FXML;

public class SenRaportController {
    public static final String PATH = "sen/raport.fxml";

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void goSpanko() {
        MementoMori.navigateTo(SenController.PATH);
    }
}
