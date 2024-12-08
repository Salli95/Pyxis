package com.example.pyxis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;

public class WeatherController {

    @FXML
    private Button Update;

    @FXML
    private ImageView weatherIcon;

    @FXML
    private HBox Weather_output;  //вывод погоды в это поле

    @FXML
    private Label output;     // поле для вывода текстового сообщения

    @FXML
    private Label city;

    @FXML
    private Label time_of_update;

    @FXML
    private void updateWeather() throws IOException, SQLException, ClassNotFoundException {
        ParsingWebSite parsingWebSite = new ParsingWebSite();
        parsingWebSite.parseData();
        selectCity();

        output.setText("Погода успешно обновлена на ближайшие 5 дней.");
    }

@FXML
public void selectCity() throws SQLException, ClassNotFoundException {
    Connection connection = new Conection().getDbConnection();

    String sql = "SELECT city, forecast_time, weather_icon FROM " + DatabaseFields.Table_weather + " LIMIT 1";

    try (PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        if (resultSet.next()) {
            // Получаем город
            city.setText("City: " + resultSet.getString("city"));

            // Получаем время последнего обновления
            Timestamp lastUpdated = resultSet.getTimestamp("forecast_time");
            if (lastUpdated != null) {
                // Преобразуем время в строку для отображения
                String formattedTime = new SimpleDateFormat("dd.MM.yyyy \n HH:mm:ss").format(lastUpdated);
                time_of_update.setText("Last update: \n" + formattedTime);
            } else {
                output.setText("Время обновления не доступно.");
            }
            String iconCode = resultSet.getString("weather_icon");
            if (iconCode != null) {
                String imageUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                weatherIcon.setImage(new javafx.scene.image.Image(imageUrl));
            } else {
                output.setText("Иконка погоды отсутствует.");
            }
        } else {
            output.setText("Данные отсутствуют.");
        }
    } catch (SQLException e) {
        output.setText("Ошибка: " + e.getMessage());
    }
}






    ///////////////////////////////////////////////////////////////////
}
