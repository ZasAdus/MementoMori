module com.example.mementomori {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.example.mementomori to javafx.fxml;
    opens com.example.mementomori.custom_elements to javafx.fxml;
    exports com.example.mementomori;
}