package com.example.pyxis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class WeatherDay implements Comparable<WeatherDay> {
    private String forecastDate;
    private double temperature;
    private double windSpeed;
    private int precipitationProbability;
    private String description; // New field for weather description

    // Getter and Setter for forecastDate
    public String getForecastDate() {
        return forecastDate;
    }

    public void setForecastDate(String forecastDate) {
        this.forecastDate = forecastDate;
    }

    // Getter and Setter for temperature
    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    // Getter and Setter for windSpeed
    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    // Getter and Setter for precipitationProbability
    public int getPrecipitationProbability() {
        return precipitationProbability;
    }

    public void setPrecipitationProbability(int precipitationProbability) {
        this.precipitationProbability = precipitationProbability;
    }

    // Getter and Setter for description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // New method to get the day of the week
    public String getDayOfWeek() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(this.forecastDate, formatter);
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    @Override
    public String toString() {
        return String.format("%s (%s)\nDescription: %s\nTemperature: %.2f°C\nWind Speed: %.2f m/s\nProbability: %d%%",
                getDayOfWeek(), forecastDate, description, temperature, windSpeed, precipitationProbability);
    }

    @Override
    public int compareTo(WeatherDay other) {
        return LocalDate.parse(this.forecastDate).compareTo(LocalDate.parse(other.forecastDate));
    }
}
