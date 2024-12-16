


package com.example.pyxis;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.io.IOException;

public class WeatherController {

    @FXML private Label Day1, Day2, Day3, Day4, Day5;
    @FXML private Label temperature1, temperature2, temperature3, temperature4, temperature5;
    @FXML private Label windspped1, windspped2, windspped3, windspped4, windspped5;
    @FXML private Label probability1, probability2, probability3, probability4, probability5;

    @FXML private Button Update;
    @FXML private Label city, output, time_of_update;

    @FXML private ImageView day1ImageView, day2ImageView, day3ImageView, day4ImageView, day5ImageView;
    @FXML private VBox day1, day2, day3, day4, day5;

    private final DatabaseConnection dbConnection = new DatabaseConnection();

    @FXML
    public void initialize() throws SQLException, ClassNotFoundException {
        selectCity();
        fillWeatherData();
        displayWeatherIcons();
    }

    @FXML
    private void updateWeather() throws IOException, SQLException, ClassNotFoundException {
        ParsingWebSite parsingWebSite = new ParsingWebSite();
        parsingWebSite.parseData(); // Simulates data fetching from an external source

        selectCity();
        fillWeatherData();
        displayWeatherIcons();
        output.setText("Weather successfully updated for the next 5 days.");
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

    private void showMoreForDay(Label dayLabel) throws SQLException, ClassNotFoundException {
        String date = (String) dayLabel.getUserData();
        showAdditionalWeatherData(date);
    }

    private void showAdditionalWeatherData(String date) throws SQLException, ClassNotFoundException {
        if (date == null) {
            output.setText("No additional data available for null");
            return;
        }

        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT forecast_date, temperature, humidity, wind_direction, precipitation_probability, wind_speed, description "
                             + "FROM weather WHERE forecast_date = ? LIMIT 1")) {

            statement.setString(1, date);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String forecastDate = resultSet.getString("forecast_date");
                    double temperature = resultSet.getDouble("temperature");
                    int humidity = resultSet.getInt("humidity");
                    double windDirection = resultSet.getDouble("wind_direction");
                    int precipitationProbability = resultSet.getInt("precipitation_probability");
                    double windSpeed = resultSet.getDouble("wind_speed");
                    String description = resultSet.getString("description");

                    // Convert forecast date to day of the week
                    String dayOfWeek = LocalDate.parse(forecastDate).getDayOfWeek()
                            .getDisplayName(TextStyle.FULL, Locale.ENGLISH);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("AdditionalInfoView.fxml"));
                    VBox root = loader.load();

                    AdditionalInfoController controller = loader.getController();
                    controller.setData(
                            dayOfWeek + " (" + forecastDate + ")", // Show day of week + date
                            temperature, description, humidity, windDirection, windSpeed, precipitationProbability
                    );

                    Stage stage = new Stage();
                    stage.setTitle("Additional Weather Details");
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    output.setText("No additional data available for " + date);
                }
            }
        } catch (IOException | SQLException e) {
            output.setText("Error retrieving additional weather data: " + e.getMessage());
        }
    }

    @FXML
    public void selectCity() throws SQLException, ClassNotFoundException {
        try (Connection connection = dbConnection.getDbConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT city, forecast_time FROM weather LIMIT 1");
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                city.setText("City: " + resultSet.getString("city"));
                Timestamp lastUpdated = resultSet.getTimestamp("forecast_time");
                if (lastUpdated != null) {
                    String formattedTime = new SimpleDateFormat("dd.MM.yyyy \n HH:mm:ss").format(lastUpdated);
                    time_of_update.setText("Last update: \n" + formattedTime);
                }
            }
        } catch (SQLException e) {
            output.setText("Error retrieving city: " + e.getMessage());
        }
    }

    @FXML
    private void displayWeatherIcons() throws SQLException, ClassNotFoundException {
        String query = "SELECT weather_icon FROM weather ORDER BY forecast_date ASC LIMIT 5";
        List<String> weatherIcons = new ArrayList<>();

        try (Connection connection = dbConnection.getDbConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            // Fetch the weather_icon codes from the database
            while (resultSet.next()) {
                String iconCode = resultSet.getString("weather_icon");

                // Ensure iconCode is not null or empty
                if (iconCode != null && !iconCode.isEmpty()) {
                    // Modify specific cases for snowflakes, clouds, and the sun
                    if (iconCode.startsWith("13")) {
                        // Snowflakes: Always dark
                        iconCode = "13n";
                    } else if (iconCode.startsWith("02") || iconCode.startsWith("03") || iconCode.startsWith("04")) {
                        // Clouds: Always bright
                        iconCode = iconCode.replace("n", "d");
                    } else if (iconCode.startsWith("01")) {
                        // Sun: Always bright orange
                        iconCode = "01d";
                    }

                    weatherIcons.add(iconCode);
                }
            }

            // Array of ImageViews for the days
            ImageView[] iconViews = {day1ImageView, day2ImageView, day3ImageView, day4ImageView, day5ImageView};

            // Update each ImageView with the corresponding weather icon
            for (int i = 0; i < weatherIcons.size() && i < iconViews.length; i++) {
                String iconCode = weatherIcons.get(i);
                String imageUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                // Set the image for the corresponding day
                iconViews[i].setImage(new javafx.scene.image.Image(imageUrl));
            }
        } catch (SQLException e) {
            output.setText("Error displaying weather icons: " + e.getMessage());
        }
    }


    @FXML
    protected void fillWeatherData() throws SQLException, ClassNotFoundException {
        String query = "SELECT forecast_date, temperature, wind_speed, precipitation_probability, description FROM weather ORDER BY forecast_date ASC";
        List<WeatherDay> weatherDays = new ArrayList<>();

        try (Connection connection = dbConnection.getDbConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                WeatherDay weatherDay = new WeatherDay();
                weatherDay.setForecastDate(resultSet.getString("forecast_date"));
                weatherDay.setTemperature(resultSet.getDouble("temperature"));
                weatherDay.setWindSpeed(resultSet.getDouble("wind_speed"));
                weatherDay.setPrecipitationProbability(resultSet.getInt("precipitation_probability"));
                weatherDay.setDescription(resultSet.getString("description"));
                weatherDays.add(weatherDay);
            }

            Collections.sort(weatherDays);

            // Labels for display
            Label[] dayLabels = {Day1, Day2, Day3, Day4, Day5};
            Label[] tempLabels = {temperature1, temperature2, temperature3, temperature4, temperature5};
            Label[] windLabels = {windspped1, windspped2, windspped3, windspped4, windspped5};
            Label[] probLabels = {probability1, probability2, probability3, probability4, probability5};

            // Ensure forecast dates dynamically determine day names
            for (int i = 0; i < weatherDays.size() && i < dayLabels.length; i++) {
                WeatherDay weatherDay = weatherDays.get(i);

                // Convert forecast_date to a LocalDate for proper day of the week
                LocalDate forecastDate = LocalDate.parse(weatherDay.getForecastDate());
                String dayOfWeek = forecastDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

                dayLabels[i].setText(dayOfWeek);  // Display the correct day name
                tempLabels[i].setText("Temperature: " + weatherDay.getTemperature() + "°C");
                windLabels[i].setText("Wind Speed: " + weatherDay.getWindSpeed() + " m/s");
                probLabels[i].setText("Probability: " + weatherDay.getPrecipitationProbability() + "%");

                dayLabels[i].setUserData(weatherDay.getForecastDate()); // Keep the correct date for 'More' buttons
            }
        } catch (SQLException e) {
            output.setText("Error filling weather data: " + e.getMessage());
        }
    }



}

