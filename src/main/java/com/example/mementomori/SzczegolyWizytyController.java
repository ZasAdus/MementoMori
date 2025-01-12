package com.example.mementomori;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class SzczegolyWizytyController {

    @FXML
    private Label detailsLabel;

    public void setDetails(String details) {
        detailsLabel.setText(details);
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) detailsLabel.getScene().getWindow();
        stage.close();
    }

    private void showAppointmentDetails(String appointment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("szczegoly_wizyty.fxml"));
            Parent root = loader.load();

            SzczegolyWizytyController controller = loader.getController();
            controller.setDetails("Data wizyty: " + appointment);

            Stage stage = new Stage();
            stage.setTitle("Szczegóły wizyty");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
