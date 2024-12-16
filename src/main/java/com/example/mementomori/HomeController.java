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
    protected void goKroki() {
        System.out.println("kroki");
    }

    @FXML
    protected void goDieta() {
        System.out.println("dieta");
    }

    @FXML
    protected void goSpanko() {
        System.out.println("spanko");
    }

    @FXML
    protected void goLeki() {
        MementoMori.navigateTo(LekiController.MAIN_PATH);
    }

    @FXML
    protected void goWizyty() {
        System.out.println("wizyty");
    }
}