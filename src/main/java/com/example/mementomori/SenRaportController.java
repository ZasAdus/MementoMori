package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaSpanko;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Random;

public class SenRaportController {
    public static final String PATH = "sen/raport.fxml";

    @FXML private BarChart<String, Number> eep;
    @FXML private Label efektywnosc;
    @FXML private Label dlugosc;

    public void initialize() {
        try {
            Timestamp upperBound = Timestamp.valueOf(LocalDateTime.now().minusDays(1).withHour(23).withMinute(59));
            Timestamp lowerBound = Timestamp.valueOf(LocalDateTime.now().minusDays(1).withHour(0).withMinute(0));
            double yesterdaySleep = Math.min(BazaSpanko.getTotalSleepHours(lowerBound, upperBound), 24.0);

            int hours = (int) yesterdaySleep;
            int minutes = (int) ((yesterdaySleep - hours) * 60);

            efektywnosc.setText(Math.min(6, 100 - 3 * Math.abs(yesterdaySleep - 8)) + "%");
            dlugosc.setText(hours + " h " + minutes + " min");
            eep.getData().clear();

            Random random = new Random(hours + minutes);
            int sen_lekki = (int)((random.nextGaussian() * 0.1 + 0.5) * 100);
            int sen_gleboki = (int)((random.nextGaussian() * 0.2 + 0.2) * 100);
            int sen_REM = (int)((random.nextGaussian() * 0.2 + 0.2) * 100);
            int czujnosc = 100 - sen_lekki - sen_gleboki - sen_REM;

            XYChart.Series<String, Number> series = new XYChart.Series<>();

            series.getData().add(new XYChart.Data<>("Przebudzenie", czujnosc));
            series.getData().add(new XYChart.Data<>("REM", sen_REM));
            series.getData().add(new XYChart.Data<>("Sen lekki", sen_lekki));
            series.getData().add(new XYChart.Data<>("Sen głęboki", sen_gleboki));

            eep.getData().add(series);
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }


    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void goSpanko() {
        MementoMori.navigateTo(SenController.PATH);
    }
}
