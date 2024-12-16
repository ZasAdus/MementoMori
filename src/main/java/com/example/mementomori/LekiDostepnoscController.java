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
        try {
            MementoMori.returnHome();
        }
        catch (IOException e ) {
            System.out.println(e.toString());
        }
    }

    @FXML
    public void goLeki() {
        try {
            MementoMori.navigateTo(LekiController.MAIN_PATH);
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
