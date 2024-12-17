package com.example.mementomori;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.util.Random;

class AptekaTemplate extends HBox {
    private static final String Path = "leki/apteka_template.fxml";

    @FXML private Label name;
    @FXML private Label where;
    @FXML private Label price;

    private String map_link;

    public AptekaTemplate(String name, String where, String price, String map_link) {
        FXMLLoader loader = new FXMLLoader(MementoMori.class.getResource(Path));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        this.map_link = map_link;
        this.name.setText(name);
        this.where.setText(where);
        this.price.setText(price);
    }

    @FXML
    public void mapa() {
        try {
            Desktop.getDesktop().browse(new URI(this.map_link));
        }
        catch (Exception e) {
            System.err.println("to sie nie miało prawa wywalić");
            e.printStackTrace();
        }
    }
}

public class LekiDostepnoscController {

    @FXML private VBox aptekaContainer;
    @FXML private TextField SzukanyLek;
    @FXML private TextField SzukanaMiejsc;
    @FXML private Text remindToFill;

    public static final String MAIN_PATH = "leki/dostepnosc.fxml";

    public void initialize() {
        remindToFill.setVisible(false);
    }

    private static String[] AptekiLol = {
        "Apteka Zdrowie",
        "Apteka Natura",
        "Apteka Słoneczna",
        "Apteka Uzdrowisko",
        "Apteka Pod Aniołem",
        "Apteka Wróżba Zdrowia",
        "Apteka Serce Natury",
        "Apteka Przyjazna",
        "Apteka z Pasją",
        "Apteka Białe Leki",
        "Apteka Witaminka",
        "Apteka Ziołowy Zakątek",
        "Apteka Zdrowy Krok",
        "Apteka Królewska",
        "Apteka Morska",
        "Apteka Pod Laskiem",
        "Apteka Wiatr Zdrowia",
        "Apteka na Rogu",
        "Apteka Stare Miasto",
        "Apteka Nocna"
    };

    private static String[] Ulice = {
        "Ulica Różanej Polany",
        "Osiedle Leśny Zakątek",
        "Ulica Słonecznego Wzgórza",
        "Osiedle Nad Strumykiem",
        "Ulica Szafirowa",
        "Osiedle Złotej Jesieni",
        "Ulica Kwiatowej Łąki",
        "Osiedle Dębowych Alejek",
        "Ulica Morskiego Brzegu",
        "Osiedle Cichych Traw"
    };

    private static String Miejsce = "https://www.google.com/maps/@48.8599549,2.3439078,3a,64y,169.11h,74.92t/data=!3m7!1e1!3m5!1sdTPZ3qeaCdIgAkGq1j_Q6Q!2e0!6shttps:%2F%2Fstreetviewpixels-pa.googleapis.com%2Fv1%2Fthumbnail%3Fcb_client%3Dmaps_sv.tactile%26w%3D900%26h%3D600%26pitch%3D15.079999999999998%26panoid%3DdTPZ3qeaCdIgAkGq1j_Q6Q%26yaw%3D169.11!7i16384!8i8192?entry=ttu&g_ep=EgoyMDI0MTIxMS4wIKXMDSoASAFQAw%3D%3D";

    @FXML
    public void search() {
        if(SzukanaMiejsc.getText().length() == 0 || SzukanyLek.getText().length() == 0) {
            remindToFill.setVisible(true);
            return;
        }
        remindToFill.setVisible(false);

        long seed = SzukanyLek.getText().hashCode() + SzukanaMiejsc.getText().hashCode();
        Random rng = new Random(seed);

        double base_price = rng.nextDouble(20, 200);
        float n_apteki = rng.nextInt(1,5);

        var container = aptekaContainer.getChildren();
        container.clear();
        for(int i = 0; i < n_apteki; i++) {
            double price = rng.nextGaussian(base_price, 10);
            String apteka = AptekiLol[rng.nextInt(AptekiLol.length)];
            String ulica = Ulice[rng.nextInt(Ulice.length)];
            int numer_domu = rng.nextInt(1, 30);

            container.add(new AptekaTemplate(apteka, ulica + numer_domu, String.format("%.2f", price), Miejsce));
        }
    }

    @FXML
    public void goHome() {
        MementoMori.returnHome();
    }

    @FXML
    public void goLeki() {
        MementoMori.navigateTo(LekiController.MAIN_PATH);
    }
}
