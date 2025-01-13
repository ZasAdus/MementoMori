package com.example.mementomori.custom_elements;

import com.example.mementomori.MementoMori;
import com.example.mementomori.bazyDanych.BazaLeki;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;

import java.io.IOException;

public class RoundProgressbar extends VBox {
    private static final String Path = "custom_elements/RoundProgressbar.fxml";

    @FXML Arc ProgressBar;
    @FXML Label ProgressTitle;
    @FXML Label Min;
    @FXML Label Max;

    public RoundProgressbar() {
        FXMLLoader loader = new FXMLLoader(MementoMori.class.getResource(Path));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setColor(String color) {
        ProgressBar.setStyle("-fx-fill: " + color);
    }

    public void setProgress(double progress) {
        ProgressBar.setLength(Math.min(progress * 270, 270));
    }

    public void setProgressTitle(String title) {
        ProgressTitle.setText(title);
    }

    public void setMin(String min) {
        Min.setText(min);
    }

    public void setMax(String max) {
        Max.setText(max);
    }
}