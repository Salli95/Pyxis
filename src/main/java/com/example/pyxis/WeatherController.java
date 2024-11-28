package com.example.pyxis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class WeatherController {

    @FXML
    private Button Update;

    @FXML
    private HBox Weather_output;  //вывод погоды в это поле

    @FXML
    private Label output;     // поле для вывода текстового сообщения

    @FXML
    protected void onHelloButtonClick() {
        output.setText("Hello World!");
    }

}
