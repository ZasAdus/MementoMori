package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaLeki;
import com.example.mementomori.bazyDanych.BazaRejestracja;
import javafx.application.Application;
import javafx.css.Style;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MementoMori extends Application {

    public static Stage main_stage;
    public static int idDoctor;

    private static Map<String, Scene> loadedScenes;

    public static String currentUser;
    public static boolean isDoctor;


    private static final double VALID_RATIO = 0.7;
    private static final double WIDTH = 428;
    private static final double HEIGHT = 926;
    private static double scale = 0.75;

    @Override
    public void start(Stage stage) throws IOException {
        loadedScenes = new HashMap<>();
        main_stage = stage;

        stage.setTitle("Memento mori");

        double screenHeight = Screen.getPrimary().getBounds().getHeight();
        scale = Math.min(1, screenHeight / HEIGHT * VALID_RATIO);

        System.out.printf("scale: %.2f \n",scale);
        System.out.printf("actual dimensions: %.2f x %.2f \n", WIDTH * scale, HEIGHT * scale);

        stage.setWidth(WIDTH* scale);
        stage.setHeight(HEIGHT* scale);

        stage.setResizable(false);
        stage.setOnCloseRequest(e -> System.exit(0));

        // bez tego folderu się rzeczy wywalają
        new File("data").mkdirs();

        // vv to potrzebuje folder data vv
        BazaLeki.initTable();
        BazaRejestracja.initTable();

        navigateTo("Logowanie.fxml");
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

            // do skalowania bo inaczej wyjebka
            main_stage.sizeToScene();
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
        Region root = loader.load();
        root.setPrefSize(WIDTH, HEIGHT);

        //Wrap the resizable content in a non-resizable container (Group)
        Group group = new Group( root );
        //Place the Group in a StackPane, which will keep it centered
        StackPane rootPane = new StackPane();
        rootPane.getChildren().add( group );

        // Create the scene initally at the "100%" size
        Scene scene = new Scene( rootPane, WIDTH* scale, HEIGHT* scale);

        // Bind the scene's width and height to the scaling parameters on the group
        group.scaleXProperty().bind( scene.widthProperty().divide( WIDTH ) );
        group.scaleYProperty().bind( scene.heightProperty().divide( HEIGHT ) );

        loadedScenes.put(path, scene);
        return scene;
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