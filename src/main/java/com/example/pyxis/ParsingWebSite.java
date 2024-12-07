package com.example.pyxis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.*;
import java.io.*;
import java.sql.*;

public class ParsingWebSite {
    public void parseData() throws IOException {

        String apiKey = "12556c21f1bc4b493c409a2c6fcedeb2";  // Не трогать
        String lat = "43.2220"; // Широта Алматы
        String lon = "76.8512"; // Долгота Алматы

        URL url = new URL("https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey);
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

            // Получаем список прогнозов
            JsonNode listNode = rootNode.path("list");

            // Счетчик для ограничения до 5 дней
            int dayCount = 0;
            String lastDate = "";

            for (JsonNode forecastNode : listNode) {
                // Извлечение данных
                String dt_txt = forecastNode.path("dt_txt").asText();
                String datePart = dt_txt.split(" ")[0]; // Получаем дату (YYYY-MM-DD)

                // Проверяем, изменился ли день
                if (!datePart.equals(lastDate)) {
                    lastDate = datePart;
                    dayCount++;
                    if (dayCount > 5) {
                        break;
                    }

                    // Извлечение данных для прогноза
                    long forecastDateUnix = forecastNode.path("dt").asLong();
                    JsonNode mainNode = forecastNode.path("main");
                    double temperatureKelvin = mainNode.path("temp").asDouble();
                    int humidity = mainNode.path("humidity").asInt();
                    int pressure = mainNode.path("pressure").asInt();
                    double feelsLikeKelvin = mainNode.path("feels_like").asDouble();

                    double temperatureCelsius = temperatureKelvin - 273.15;
                    double feelsLikeCelsius = feelsLikeKelvin - 273.15;

                    JsonNode weatherArray = forecastNode.path("weather");
                    String description = weatherArray.get(0).path("description").asText();
                    String weatherIcon = weatherArray.get(0).path("icon").asText();

                    JsonNode windNode = forecastNode.path("wind");
                    double windSpeed = windNode.path("speed").asDouble();
                    int windDirection = windNode.path("deg").asInt();

                    double precipitationProbability = forecastNode.path("pop").asDouble(); // Вероятность осадков

                    // Конвертация Unix-времени в Timestamp
                    Timestamp forecastDate = new Timestamp(forecastDateUnix * 1000);

                    // Поскольку время восхода и заката не предоставляется в этом API, устанавливаем null
                    Timestamp sunriseTime = null;
                    Timestamp sunsetTime = null;

                    // Установка текущего времени для forecastTime
                    Timestamp forecastTime = new Timestamp(System.currentTimeMillis());

                    // Преобразование направления ветра в строку
                    String windDirectionStr = getWindDirection(windDirection);

                    // Вставка данных в базу данных
                    insertDataIntoDatabase("Алматы", forecastDate, temperatureCelsius, description, humidity,
                            windSpeed, String.valueOf(windDirection), (int) (precipitationProbability * 100), weatherIcon, forecastTime);
                }
            }

        } else {
            System.out.println("Ошибка: " + responseCode);
        }
    }

    private void insertDataIntoDatabase(String cityName, Timestamp forecastDate, double temperature,
                                        String description, int humidity, double windSpeed, String windDirection,
                                        int precipitationProbability, String weatherIcon, Timestamp forecastTime) {

        Conection connectionClass = new Conection();
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // Получение соединения с базой данных через ваш класс Conection
            conn = connectionClass.getDbConnection();

            // Подготовка SQL-запроса для вставки данных
            String sql = "INSERT INTO " + DatabaseFields.Table_weather + " (" +
                    DatabaseFields.city + ", " +
                    DatabaseFields.forecast_date + ", " +
                    DatabaseFields.temperature + ", " +
                    DatabaseFields.description + ", " +
                    DatabaseFields.humidity + ", " +
                    DatabaseFields.wind_speed + ", " +
                    DatabaseFields.wind_direction + ", " +
                    DatabaseFields.precipitation_probability + ", " +
                    DatabaseFields.weather_icon + ", " +
                    DatabaseFields.forecast_time +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cityName);
            pstmt.setTimestamp(2, forecastDate); // forecast_date (DATE)
            pstmt.setDouble(3, temperature);
            pstmt.setString(4, description);
            pstmt.setInt(5, humidity);
            pstmt.setDouble(6, windSpeed);
            pstmt.setString(7, windDirection); // wind_direction (VARCHAR)
            pstmt.setInt(8, precipitationProbability);
            pstmt.setString(9, weatherIcon);
            pstmt.setTimestamp(10, forecastTime); // forecast_timeDATETIME)

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

    // Метод для преобразования градусов в направление ветра
    public static String getWindDirection(int degrees) {
        String[] directions = {"С", "ССВ", "СВ", "ВСВ", "В", "ВЮВ", "ЮВ", "ЮЮВ",
                "Ю", "ЮЮЗ", "ЮЗ", "ЗЮЗ", "З", "ЗСЗ", "СЗ", "ССЗ"};
        int index = (int)((degrees / 22.5) + 0.5) % 16;
        return directions[index];
    }
}