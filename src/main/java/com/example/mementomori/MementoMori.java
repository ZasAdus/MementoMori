package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaLeki;
import com.example.mementomori.bazyDanych.BazaRejestracja;
import javafx.application.Application;
import javafx.css.Style;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MementoMori extends Application {

    public static Stage main_stage;

    private static Map<String, Scene> loadedScenes;

    @Override
    public void start(Stage stage) throws IOException {
        loadedScenes = new HashMap<>();
        main_stage = stage;

        stage.setTitle("Memento mori");
        stage.setMinWidth(428);
        stage.setMinHeight(926);
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> System.exit(0));

        // bez tego folderu się rzeczy wywalają
        // z mojej winy więc później może to naprawię XD
        new File("data").mkdirs();

        // vv to potrzebuje folder data vv
        BazaLeki.initTable();
        BazaRejestracja.initTable();


        stage.setScene(load("Logowanie.fxml"));
        stage.show();
    }

    private static final String HOME_PATH = "home.fxml";

    /**
     * Przełącza na scenę strony głównej
     */
    public static void returnHome() {
        navigateTo(HOME_PATH);
    }

    /**
     * Zmienia scenę
     * @param path nazwa pliku fxml
     */
    public static void navigateTo(String path) {
        try {
            main_stage.setScene(load(path));
        }
        catch(IOException e) {
            System.err.println("nie udało się załadować " + e + ": " + e.toString());
        }
    }


    /**
     * Aktualizuje zapamiętaną scenę i zwraca nową wersję
     * @param path nazwa pliku fxml
     */
    public static Scene forceReload(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(MementoMori.class.getResource(path));
        Scene result =  new Scene(loader.load());
        result.getStylesheets().add(MementoMori.class.getResource("styles/style.css").toExternalForm()); // ładowanie globalnych styli

        loadedScenes.put(path, result);
        return result;
    }

    /**
     * Ładuje scenę. Zwraca zapamiętaną, jeśli już raz była załadowana
     * @param path nazwa pliku fxml
     */
    public static Scene load(String path) throws IOException {
        Scene target = loadedScenes.get(path);
        if(target == null) {
            target = forceReload(path);
        }

        return target;
    }

    public static void main(String[] args) {
        launch();
    }
}