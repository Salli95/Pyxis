module com.example.pyxis {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.jsoup;
    requires com.fasterxml.jackson.databind;


    opens com.example.pyxis to javafx.fxml;
    exports com.example.pyxis;
}