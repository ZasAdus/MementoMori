package com.example.mementomori;

import javafx.fxml.FXML;
import java.io.IOException;

public class LekiController {

    public static final String MAIN_PATH = "leki/leki.fxml";

    @FXML
    public void goHome() {
        try {
            MementoMori.returnHome();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    @FXML
    public void checkAvailability() {
        try {
            MementoMori.navigateTo(LekiDostepnoscController.MAIN_PATH);
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
