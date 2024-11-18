module com.example.pyxis {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pyxis to javafx.fxml;
    exports com.example.pyxis;
}