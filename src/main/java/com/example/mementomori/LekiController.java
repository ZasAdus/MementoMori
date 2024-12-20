package com.example.mementomori;

import com.example.mementomori.bazyDanych.BazaLeki;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Optional;

class LekElement extends HBox {
    public static String PATH = "leki/lek_template.fxml";

    private BazaLeki.LekiEntry lek;

    @FXML
    private Label Name;

    @FXML
    private Label Time;

    private void setLek(BazaLeki.LekiEntry value) {
        if(lek == null) {
            lek = value;
        } else {
            lek = new BazaLeki.LekiEntry(this.lek.id(), value.name(), value.time());
        }

        Name.setText(value.name());
        Time.setText(value.getTimeString());
    }

    public LekElement(BazaLeki.LekiEntry target) {
        FXMLLoader loader = new FXMLLoader(MementoMori.class.getResource(PATH));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        }
        catch (IOException e) {
            System.err.println("nie udało się załadować leku: " + e);
        }

        setLek(target);
    }

    @FXML
    public void edit() {
        BazaLeki.LekiEntry result = LekiDialogController.show("Edytuj lek", lek);
        if(result != null) {
            setLek(result);
            BazaLeki.update(this.lek);
        }
    }

    @FXML
    public void delete() {
        Alert ask = new Alert(Alert.AlertType.CONFIRMATION);
        ask.setTitle("Usuwanie leku");
        ask.setHeaderText(null);
        ask.setContentText("czy na pewno chcesz usunąć ten lek?");

        Optional<ButtonType> result = ask.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            BazaLeki.delete(this.lek);
            if(getParent() instanceof VBox parent) {
                parent.getChildren().remove(this);
            }
        }
    }
}

class LekiDialogController {
    private static final String Path = "leki/dialog.fxml";

    @FXML private TextField name;
    @FXML private TextField hours;
    @FXML private TextField minutes;

    public String getName() {
        return name.getText();
    }

    public Time getTime() {
        return Time.valueOf(LocalTime.of(Integer.parseInt(hours.getText()), Integer.parseInt(minutes.getText()), 0));
    }

    public void setValues(BazaLeki.LekiEntry value) {
        name.setText(value.name());
        String time = value.getTimeString();
        hours.setText(time.substring(0, 2));
        minutes.setText(time.substring(3, 5));
    }

    public static BazaLeki.LekiEntry show(String dialog_title, BazaLeki.LekiEntry default_value) {
        Dialog<BazaLeki.LekiEntry> dialog = new Dialog<>();
        dialog.setTitle(dialog_title);
        dialog.setHeaderText(null);

        ButtonType submitButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        LekiDialogController controller = new LekiDialogController();
        FXMLLoader loader = new FXMLLoader(MementoMori.class.getResource(Path));
        loader.setController(controller);

        try {
            dialog.getDialogPane().setContent(loader.load());
            if(default_value != null) {
                controller.setValues(default_value);
            }
            dialog.setResultConverter(dialogButton -> {
                if(dialogButton.getButtonData().isCancelButton()) {
                    return null;
                }

                return new BazaLeki.LekiEntry(-1, controller.getName(), controller.getTime());
            });

            Optional<BazaLeki.LekiEntry> result = dialog.showAndWait();
            return result.orElse(null);
        }
        catch (IOException err) {
            return null;
        }
    }
}

public class LekiController {

    public static final String MAIN_PATH = "leki/leki.fxml";

    @FXML private VBox LekiContainer;

    public void initialize() {
        var leki = BazaLeki.getAll();
        var leki_children = LekiContainer.getChildren();
        for(BazaLeki.LekiEntry lek : leki) {
            leki_children.add(new LekElement(lek));
        }
    }

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void checkAvailability() {
        MementoMori.navigateTo(LekiDostepnoscController.MAIN_PATH);
    }

    @FXML
    public void addSchedule() {
        BazaLeki.LekiEntry result = LekiDialogController.show("Dodaj lek", null);
        if(result != null) {
            BazaLeki.LekiEntry added = BazaLeki.add(result.name(), result.time());
            LekiContainer.getChildren().add(new LekElement(added));
        }
    }
}
