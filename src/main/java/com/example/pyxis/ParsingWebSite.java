package com.example.pyxis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.*;
import java.io.*;
import java.sql.*;

public class ParsingWebSite {
    public void parseData() throws IOException {

        String apiKey = "12556c21f1bc4b493c409a2c6fcedeb2";  // Не трогать
        String lat = "40.7128";
        String lon = "-74.0060";

        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseBody = response.toString();
            System.out.println("Ответ от API: " + responseBody);

            // Парсинг JSON ответа
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // Извлечение данных
            String cityName = rootNode.path("name").asText();
            int idWeather = rootNode.path("weather").get(0).path("id").asInt();
            long forecastDateUnix = rootNode.path("dt").asLong();
            double temperatureKelvin = rootNode.path("main").path("temp").asDouble();
            String description = rootNode.path("weather").get(0).path("description").asText();
            int humidity = rootNode.path("main").path("humidity").asInt();
            double windSpeed = rootNode.path("wind").path("speed").asDouble();
            int windDirection = rootNode.path("wind").path("deg").asInt();
            double precipitationProbability = 0; // API текущей погоды не предоставляет эти данные
            long sunriseUnix = rootNode.path("sys").path("sunrise").asLong();
            long sunsetUnix = rootNode.path("sys").path("sunset").asLong();
            String weatherIcon = rootNode.path("weather").get(0).path("icon").asText();

            // Конвертация Unix-времени в Timestamp
            Timestamp forecastDate = new Timestamp(forecastDateUnix * 1000);
            Timestamp sunriseTime = new Timestamp(sunriseUnix * 1000);
            Timestamp sunsetTime = new Timestamp(sunsetUnix * 1000);

            // Конвертация температуры из Кельвинов в Цельсии
            double temperatureCelsius = temperatureKelvin - 273.15;

            // Установка текущего времени для forecastTime
            Timestamp forecastTime = new Timestamp(System.currentTimeMillis());

            // Вставка данных в базу данных
            insertDataIntoDatabase(cityName, idWeather, forecastDate, temperatureCelsius, description, humidity,
                    windSpeed, windDirection, precipitationProbability, sunriseTime, sunsetTime, weatherIcon, forecastTime);

        } else {
            System.out.println("Ошибка: " + responseCode);
        }
    }

    private void insertDataIntoDatabase(String cityName, int idWeather, Timestamp forecastDate, double temperature,
                                        String description, int humidity, double windSpeed, int windDirection,
                                        double precipitationProbability, Timestamp sunriseTime,
                                        Timestamp sunsetTime, String weatherIcon, Timestamp forecastTime) {

        Conection connectionClass = new Conection();
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Получение соединения с базой данных через ваш класс Conection
            conn = connectionClass.getDbConnection();

            // Подготовка SQL-запроса для вставки данных
            String sql = "INSERT INTO " + DatabaseFields.Table_weather + " (" +
                    DatabaseFields.city + ", " +
                    DatabaseFields.idWeather + ", " +
                    DatabaseFields.forecast_date + ", " +
                    DatabaseFields.temperature + ", " +
                    DatabaseFields.description + ", " +
                    DatabaseFields.humidity + ", " +
                    DatabaseFields.wind_speed + ", " +
                    DatabaseFields.wind_direction + ", " +
                    DatabaseFields.precipitation_probability + ", " +
                    DatabaseFields.sunrise_time + ", " +
                    DatabaseFields.sunset_time + ", " +
                    DatabaseFields.weather_icon + ", " +
                    DatabaseFields.forecast_time +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cityName);
            pstmt.setInt(2, idWeather);
            pstmt.setTimestamp(3, forecastDate);
            pstmt.setDouble(4, temperature);
            pstmt.setString(5, description);
            pstmt.setInt(6, humidity);
            pstmt.setDouble(7, windSpeed);
            pstmt.setInt(8, windDirection);
            pstmt.setDouble(9, precipitationProbability);
            pstmt.setTime(10, new Time(sunriseTime.getTime()));
            pstmt.setTime(11, new Time(sunsetTime.getTime()));
            pstmt.setString(12, weatherIcon);
            pstmt.setTimestamp(13, forecastTime); // Изменено на setTimestamp

            // Выполнение вставки данных
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Данные успешно вставлены в базу данных.");
            } else {
                System.out.println("Не удалось вставить данные в базу данных.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Закрытие ресурсов
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

}