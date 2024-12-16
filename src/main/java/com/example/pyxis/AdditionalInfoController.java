package com.example.pyxis;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdditionalInfoController {
    @FXML private Label dateLabel;
    @FXML private Label temperatureLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label humidityLabel;
    @FXML private Label windDirectionLabel;
    @FXML private Label windSpeedLabel;
    @FXML private Label precipitationLabel;

    /**
     * Sets weather data into the labels for display.
     * The date parameter now includes the day of the week for better user clarity.
     */
    public void setData(String dayWithDate, double temperature, String description, int humidity,
                        double windDirection, double windSpeed, int precipitationProbability) {
        dateLabel.setText("Date: " + dayWithDate);  // Now includes day of week
        temperatureLabel.setText("Temperature: " + temperature + "°C");
        descriptionLabel.setText("Description: " + description);
        humidityLabel.setText("Humidity: " + humidity + "%");
        windDirectionLabel.setText("Wind Direction: " + windDirection + "°");
        windSpeedLabel.setText("Wind Speed: " + windSpeed + " m/s");
        precipitationLabel.setText("Precipitation Probability: " + precipitationProbability + "%");
    }
}
