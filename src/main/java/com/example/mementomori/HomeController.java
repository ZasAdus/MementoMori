package com.example.mementomori;

import javafx.fxml.FXML;
import java.io.IOException;

public class HomeController {
    @FXML
    protected void goHome() {
        System.out.println("ten guzik jest useless XD");
        MementoMori.returnHome();
    }

    @FXML
    protected void goKroki() { MementoMori.navigateTo(KrokiController.PATH); }

    @FXML
    protected void goDieta() {
        MementoMori.navigateTo(DietaController.PATH);
    }

    @FXML
    protected void goSpanko() {
        MementoMori.navigateTo(SenController.PATH);
    }

    @FXML
    protected void goLeki() {
        MementoMori.navigateTo(LekiController.MAIN_PATH);
    }

    @FXML
    protected void goWizyty() { MementoMori.navigateTo(WizytyController.PATH); }
}