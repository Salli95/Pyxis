package com.example.pyxis;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ParsingWebSite {
    public void parseData() throws IOException {



        String apiKey = "12556c21f1bc4b493c409a2c6fcedeb2";
        String url = "https://api.weather.com/v3/current/weather?apiKey=" + apiKey + "&units=m&language=ru-RU&geocode=40.7128,-74.0060";

        Connection.Response response = Jsoup.connect(url).ignoreHttpErrors(true).execute();
        String json = response.body();

        System.out.println(json);

    }
}