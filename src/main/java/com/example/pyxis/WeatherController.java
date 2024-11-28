package com.example.pyxis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class WeatherController {

    @FXML
    private Button Update;

    @FXML
    private HBox Weather_output;

    @FXML
    private Label output;

    @FXML
    protected void onHelloButtonClick() {
        output.setText("Hello World!");
    }

}
