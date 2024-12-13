package com.example.pyxis;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;

import static com.example.pyxis.Conection.*;

public class WeatherController {

    @FXML
    private Label Day1;

    @FXML
    private Label Day2;

    @FXML
    private Label Day3;

    @FXML
    private Label Day4;

    @FXML
    private Label Day5;

    @FXML
    private Button Update;

    @FXML
    private Label city;

    @FXML
    private VBox day1;

    @FXML
    private ImageView day1ImageView;

    @FXML
    private VBox day2;

    @FXML
    private ImageView day2ImageView;

    @FXML
    private VBox day3;

    @FXML
    private ImageView day3ImageView;

    @FXML
    private VBox day4;

    @FXML
    private ImageView day4ImageView;

    @FXML
    private VBox day5;

    @FXML
    private ImageView day5ImageView;

    @FXML
    private HBox mainpanel;

    @FXML
    private Label output;

    @FXML
    private Label probability1;

    @FXML
    private Label probability2;

    @FXML
    private Label probability3;

    @FXML
    private Label probability4;

    @FXML
    private Label probability5;

    @FXML
    private Label temperature1;

    @FXML
    private Label temperature2;

    @FXML
    private Label temperature3;

    @FXML
    private Label temperature4;

    @FXML
    private Label temperature5;

    @FXML
    private Label time_of_update;

    @FXML
    private Label windspped1;

    @FXML
    private Label windspped2;

    @FXML
    private Label windspped3;

    @FXML
    private Label windspped4;

    @FXML
    private Label windspped5;
    @FXML
    public void initialize() throws SQLException, ClassNotFoundException {
        selectCity();
    }
    @FXML
    private void showMoreDay1() throws SQLException, ClassNotFoundException {
        showMoreForDay(Day1);
    }

    @FXML
    private void showMoreDay2() throws SQLException, ClassNotFoundException {
        showMoreForDay(Day2);
    }

    @FXML
    private void showMoreDay3() throws SQLException, ClassNotFoundException {
        showMoreForDay(Day3);
    }

    @FXML
    private void showMoreDay4() throws SQLException, ClassNotFoundException {
        showMoreForDay(Day4);
    }

    @FXML
    private void showMoreDay5() throws SQLException, ClassNotFoundException {
        showMoreForDay(Day5);
    }


    @FXML
    private void updateWeather() throws IOException, SQLException, ClassNotFoundException {
        ParsingWebSite parsingWebSite = new ParsingWebSite();
        parsingWebSite.parseData();
        selectCity();
        output.setText("Погода успешно обновлена на ближайшие 5 дней.");
        displayWeatherIcons();
    }

    private void showMoreForDay(Label dayLabel) throws SQLException, ClassNotFoundException {
        String date = (String) dayLabel.getUserData();
        showAdditionalWeatherData(date);
    }
    private void showAdditionalWeatherData(String date) throws SQLException, ClassNotFoundException {
        Connection connection = new Conection().getDbConnection();

        // Добавляем поля wind_speed и description в запрос
        String sql = "SELECT "
                + DatabaseFields.humidity + ", "
                + DatabaseFields.wind_direction + ", "
                + DatabaseFields.precipitation_probability + ", "
                + DatabaseFields.wind_speed + ", "
                + DatabaseFields.description + " "
                + "FROM " + DatabaseFields.Table_weather + " WHERE " + DatabaseFields.forecast_date + " = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, date);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int humidity = resultSet.getInt(DatabaseFields.humidity);
                    double windDirection = resultSet.getDouble(DatabaseFields.wind_direction);
                    int precipitationProbability = resultSet.getInt(DatabaseFields.precipitation_probability);
                    double windSpeed = resultSet.getDouble(DatabaseFields.wind_speed);
                    String desc = resultSet.getString(DatabaseFields.description);

                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    alert.setTitle("More Weather Details");
                    alert.setHeaderText("Additional details for " + date);
                    alert.setContentText(
                            "Description: " + desc + "\n" +
                                    "Humidity: " + humidity + "%\n" +
                                    "Wind Direction: " + windDirection + "°\n" +
                                    "Wind Speed: " + windSpeed + " m/s\n" +
                                    "Precipitation Probability: " + precipitationProbability + "%"
                    );
                    alert.showAndWait();
                } else {
                    output.setText("No additional data available for " + date);
                }
            }
        } catch (SQLException e) {
            output.setText("Error retrieving additional weather data: " + e.getMessage());
        } finally {
            connection.close();
        }
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

//            String iconCode = resultSet.getString("weather_icon");
//            if (iconCode != null) {
//                String imageUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
//                weatherIcon.setImage(new javafx.scene.image.Image(imageUrl));
//            } else {
//                output.setText("Иконка погоды отсутствует.");
//            }


        } else {
            output.setText("Данные отсутствуют.");
        }
    } catch (SQLException e) {
        output.setText("Ошибка: " + e.getMessage());
    }
}


    @FXML
    public void displayWeatherIcons() throws SQLException, ClassNotFoundException {
        Connection connection = new Conection().getDbConnection();

        String sql = "SELECT weather_icon FROM " + DatabaseFields.Table_weather + " ORDER BY forecast_time LIMIT 5";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            int dayIndex = 0;
            while (resultSet.next()) {
                String iconCode = resultSet.getString("weather_icon");
                iconCode = iconCode.replace("n", "d");
                if (iconCode != null) {
                    String imageUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                    ImageView iconImageView = getIconImageView(dayIndex);
                    iconImageView.setImage(new javafx.scene.image.Image(imageUrl));
                } else {
                    output.setText("Иконка погоды отсутствует.");
                }
                dayIndex++;
            }
        } catch (SQLException e) {
            output.setText("Ошибка: " + e.getMessage());
        }
    }

    private ImageView getIconImageView(int dayIndex) {
        switch (dayIndex) {
            case 0:
                return day1ImageView;
            case 1:
                return day2ImageView;
            case 2:
                return day3ImageView;
            case 3:
                return day4ImageView;
            case 4:
                return day5ImageView;
            default:
                return null;
        }
    }


    @FXML
    protected void fillWeatherData() {
        Conection conection = new Conection();
        Connection conn = null;

        try {
            conn = conection.getDbConnection();
            System.out.println("Connected to the database successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (conn != null) {
            try {
                String query = "SELECT forecast_date, temperature, wind_speed, precipitation_probability FROM weather";
                Statement statement = conn.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                System.out.println("Query executed successfully.");

                // Массив русских названий дней недели:
                // DayOfWeek.getValue() дает: Monday=1, Tuesday=2, ..., Sunday=7
                String[] daysEng = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

                int i = 0;
                while (resultSet.next()) {
                    if (i >= mainpanel.getChildren().size()) {
                        System.out.println("Not enough VBox elements in mainpanel.");
                        break;
                    }

                    VBox vbox = (VBox) mainpanel.getChildren().get(i);
                    if (vbox == null) {
                        System.out.println("VBox is null at index " + i);
                        continue;
                    }

                    if (vbox.getChildren().size() >= 5) {
                        Label dayLabel = (Label) vbox.getChildren().get(1); // Второй элемент в VBox
                        Label temperatureLabel = (Label) vbox.getChildren().get(2); // Третий
                        Label windSpeedLabel = (Label) vbox.getChildren().get(3); // Четвёртый
                        Label probabilityLabel = (Label) vbox.getChildren().get(4); // Пятый

                        String forecastDate = resultSet.getString("forecast_date");
                        double temperature = resultSet.getDouble("temperature");
                        double windSpeed = resultSet.getDouble("wind_speed");
                        int precipitationProbability = resultSet.getInt("precipitation_probability");

                        // Преобразуем строку с датой в LocalDate
                        // Предполагается, что forecast_date в формате "YYYY-MM-DD"
                        LocalDate date = LocalDate.parse(forecastDate);

                        // Получаем день недели
                        DayOfWeek dayOfWeek = date.getDayOfWeek();
                        // Конвертируем его в индекс для массива daysRus
                        // Monday=1, ... Sunday=7
                        int dayIndex = dayOfWeek.getValue();
                        String dayName = daysEng[dayIndex - 1];

                        // Устанавливаем значения
                        dayLabel.setText("Day: " + dayName);
                        dayLabel.setUserData(forecastDate);
                        temperatureLabel.setText("Temperature: " + temperature + "°C");
                        windSpeedLabel.setText("Wind Speed: " + windSpeed + " m/s");
                        probabilityLabel.setText("Probability: " + precipitationProbability + "%");
                    } else {
                        System.out.println("VBox does not have enough children at index " + i);
                    }

                    i++;
                    if (i >= 5) break;
                }
            } catch (SQLException e) {
                System.out.println("Error while executing the query: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    conn.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    System.out.println("Error closing database connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Database connection is null.");
        }
    }



    ///////////////////////////////////////////////////////////////////
}
