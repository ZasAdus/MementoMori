package com.example.mementomori;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class LekiDostepnoscController {

    @FXML
    HBox aptekaTemplate;

    public static final String MAIN_PATH = "leki/dostepnosc.fxml";

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void goLeki() {
        MementoMori.navigateTo(LekiController.MAIN_PATH);
    }
}
