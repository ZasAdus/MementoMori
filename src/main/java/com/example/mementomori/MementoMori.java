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

        stage.setScene(preload(HOME_PATH));
        stage.show();
    }

    private static final String HOME_PATH = "home.fxml";

    /**
     * Przełącza na scenę strony głównej
     */
    public static void returnHome() throws IOException {
        navigateTo(HOME_PATH);
    }

    /**
     * Zmienia scenę
     * @param path nazwa pliku fxml
     */
    public static void navigateTo(String path) throws IOException {
        Scene target = loadedScenes.get(path);
        if(target == null) {
            target = preload(path);
        }

        main_stage.setScene(target);
    }

    /**
     * Ładuje wstępnie scenę, zanim będzie jeszcze potrzebna
     * @param path nazwa pliku fxml
     */

    public static Scene preload(String path) throws IOException {
        FXMLLoader loader = new FXMLLoader(MementoMori.class.getResource(path));
        Scene result =  new Scene(loader.load());

        loadedScenes.put(path, result);
        return result;
    }

    public static void main(String[] args) {
        launch();
    }
}