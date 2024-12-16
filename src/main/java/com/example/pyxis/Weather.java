package com.example.pyxis;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
public class Weather extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Weather.class.getResource("WeatherView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Weather App");
        stage.setScene(scene);
        stage.show();

        WeatherController controller = fxmlLoader.getController();
        Platform.runLater(() -> {
            try {
                controller.selectCity();
                controller.fillWeatherData();
                 // This should now work without errors
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
