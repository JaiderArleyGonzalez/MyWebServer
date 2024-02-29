package edu.arep.controller;
import edu.arep.runtime.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/movie")
public class MovieController {
    private static String apiKey = "1892fc8d";
    private static ArrayList<String> favorites  = new ArrayList<>();

    @GetMapping("/detail")
    public  static String getByName(String name) throws IOException {
        String urlStr = "http://www.omdbapi.com/?apikey=" + apiKey + "&t="+name;
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
    @PostMapping("/favorite")
    public static String saveFavorite(String name) throws IOException {
        String urlStr = "http://www.omdbapi.com/?apikey=" + apiKey + "&t="+name;
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        favorites.add(response.toString());
        return "Se ha añadido la película: "+ response.toString();
    }
}
