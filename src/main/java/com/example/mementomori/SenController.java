package com.example.mementomori;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

class EepyTimeDialogController {
    record EepyTimeEntry (LocalDate date, Time start, Time end) {}

    private static final String Path = "sen/nowy_sen.fxml";

    @FXML private DatePicker date;
    @FXML private TextField start_hours;
    @FXML private TextField start_minutes;

    @FXML private TextField end_hours;
    @FXML private TextField end_minutes;

    public LocalDate getDate() {
        return date.getValue();
    }

    public Time getTimeStart() {
        return Time.valueOf(LocalTime.of(Integer.parseInt(start_hours.getText()), Integer.parseInt(start_minutes.getText()), 0));
    }

    public Time getTimeEnd() {
        return Time.valueOf(LocalTime.of(Integer.parseInt(end_hours.getText()), Integer.parseInt(end_minutes.getText()), 0));
    }

    public void setValues(EepyTimeEntry entry) {
        date.setValue(entry.date);

        String s_time = entry.start.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        start_hours.setText(s_time.substring(0, 2));
        start_minutes.setText(s_time.substring(3, 5));

        String e_time = entry.end.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        end_hours.setText(e_time.substring(0, 2));
        end_minutes.setText(e_time.substring(3, 5));
    }

    public static EepyTimeEntry show(String dialog_title, EepyTimeEntry default_value) {
        Dialog<EepyTimeEntry> dialog = new Dialog<>();
        dialog.setTitle(dialog_title);
        dialog.setHeaderText(null);

        ButtonType submitButtonType = new ButtonType("Zapisz", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

        EepyTimeDialogController controller = new EepyTimeDialogController();
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

                return new EepyTimeEntry(controller.getDate(), controller.getTimeStart(), controller.getTimeEnd());
            });

            Optional<EepyTimeEntry> result = dialog.showAndWait();
            return result.orElse(null);
        }
        catch (IOException err) {
            return null;
        }
    }
}

public class SenController {
    public static final String PATH = "sen/sen.fxml";

    @FXML private TextField start_hours;
    @FXML private TextField start_minutes;

    @FXML private TextField end_hours;
    @FXML private TextField end_minutes;

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void generateRaport() {
        MementoMori.navigateTo(SenRaportController.PATH);
    }

    @FXML
    public void addEepyTime() {
        Time start = Time.valueOf(LocalTime.of(Integer.parseInt(start_hours.getText()), Integer.parseInt(start_minutes.getText()), 0));
        Time end = Time.valueOf(LocalTime.of(Integer.parseInt(end_hours.getText()), Integer.parseInt(end_minutes.getText()), 0));
        EepyTimeDialogController.EepyTimeEntry default_values = new EepyTimeDialogController.EepyTimeEntry(
                LocalDate.now().minusDays(1),
                start,
                end
        );

        EepyTimeDialogController.EepyTimeEntry entry = EepyTimeDialogController.show("Dodaj nowy sen", default_values);
        if(entry != null) {
            System.out.println("Dodano sen: " + entry.date() + " " + entry.start() + " - " + entry.end());
        }
    }
}
