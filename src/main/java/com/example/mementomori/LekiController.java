package com.example.mementomori;

import javafx.fxml.FXML;
import java.io.IOException;

public class LekiController {

    public static final String MAIN_PATH = "leki/leki.fxml";

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void checkAvailability() {
        MementoMori.navigateTo(LekiDostepnoscController.MAIN_PATH);
    }
}
