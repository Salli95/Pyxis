package com.example.pyxis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;


public class testDataBase {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Conection conect = new Conection();
        Connection connection = conect.getDbConnection();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите значение поля " + DatabaseFields.city + ": ");
        String city = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.idWeather + ": ");
        String idWeather = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.forecast_date + ": ");
        String forecast_date = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.temperature + ": ");
        String temperature = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.description + ": ");
        String description = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.humidity + ": ");
        String humidity = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.wind_speed + ": ");
        String wind_speed = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.wind_direction + ": ");
        String wind_direction = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.precipitation_probability + ": ");
        String precipitation_probability = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.sunrise_time + ": ");
        String sunrise_time = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.sunset_time + ": ");
        String sunset_time = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.weather_icon + ": ");
        String weather_icon = scanner.nextLine();

        System.out.print("Введите значение поля " + DatabaseFields.forecast_time + ": ");
        String forecast_time = scanner.nextLine();

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " + DatabaseFields.Table_weather + " (" + DatabaseFields.city + ", " + DatabaseFields.idWeather + ", " + DatabaseFields.forecast_date + ", " + DatabaseFields.temperature + ", " + DatabaseFields.description + ", " + DatabaseFields.humidity + ", " + DatabaseFields.wind_speed + ", " + DatabaseFields.wind_direction + ", " + DatabaseFields.precipitation_probability + ", " + DatabaseFields.sunrise_time + ", " + DatabaseFields.sunset_time + ", " + DatabaseFields.weather_icon + ", " + DatabaseFields.forecast_time + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, city);
            statement.setString(2, idWeather);
            statement.setString(3, forecast_date);
            statement.setString(4, temperature);
            statement.setString(5, description);
            statement.setString(6, humidity);
            statement.setString(7, wind_speed);
            statement.setString(8, wind_direction);
            statement.setString(9, precipitation_probability);
            statement.setString(10, sunrise_time);
            statement.setString(11, sunset_time);
            statement.setString(12, weather_icon);
            statement.setString(13, forecast_time);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка ввода данных в базу данных: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Ошибка закрытия подключения к базе данных: " + e.getMessage());
                }
            }
        }
    }
}