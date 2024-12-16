package com.example.mementomori;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

public class DietaScreen extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DietaScreen.class.getResource("Dieta.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Memento Mori");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        stage.setOnCloseRequest(e -> System.exit(0));
        DietaController controller = fxmlLoader.getController();
        controller.init();
    }

    public static void main(String[] args) {
        launch();
    }
}