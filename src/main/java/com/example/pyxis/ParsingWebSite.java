package com.example.pyxis;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.*;
import java.io.*;

import java.io.IOException;

public class ParsingWebSite {
    public void parseData() throws IOException {

        String apiKey = "12556c21f1bc4b493c409a2c6fcedeb2";  //api не  трогать .Я его получил через регистрацию
        //эти поля это координаты где нужно узнать погоду(сейчас стоит нюйорк вроде)
        String lat = "40.7128";
        String lon = "-74.0060";

        URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseBody = response.toString();
            System.out.println("Ответ от API: " + responseBody);
        } else {
            System.out.println("Ошибка: " + responseCode);
        }
    }

    }
