package com.example.mementomori;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        stage.setMaxWidth(428);
        stage.setMaxHeight(926);
        stage.setResizable(false);

        stage.setScene(load(HOME_PATH));
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