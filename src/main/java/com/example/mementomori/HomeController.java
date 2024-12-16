package com.example.mementomori;

import javafx.fxml.FXML;
import java.io.IOException;

public class HomeController {
    //public void initialize()

    @FXML
    protected void goHome() {
        System.out.println("domeeek");
        try {
            MementoMori.returnHome();
        }
        catch (IOException e) {
            System.out.println(e.toString());
        }
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
        System.out.println("leki");
    }

    @FXML
    protected void goWizyty() {
        System.out.println("wizyty");
    }
}