package com.weather.weather_app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WeatherController {

    @GetMapping("/weather")
    public Map<String, Object> getWeather(@RequestParam String city) {

        RestTemplate restTemplate = new RestTemplate();

        // Step 1: Get latitude & longitude from city name
        String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name="
        + city + "&count=1&language=en&countryCode=IN";

        Map geoResponse = restTemplate.getForObject(geoUrl, Map.class);

        if (geoResponse == null || geoResponse.get("results") == null) {
            throw new RuntimeException("City not found");
        }

        java.util.List results = (java.util.List) geoResponse.get("results");
        Map firstResult = (Map) results.get(0);

        Double latitude = (Double) firstResult.get("latitude");
        Double longitude = (Double) firstResult.get("longitude");

        // Step 2: Get weather data
 	String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude="
        + latitude + "&longitude=" + longitude
        + "&current=temperature_2m,relative_humidity_2m,weather_code";

        Map weatherResponse = restTemplate.getForObject(weatherUrl, Map.class);

        Map current = (Map) weatherResponse.get("current");

        Map<String, Object> result = new HashMap<>();
        result.put("city", city);
        result.put("temperature", current.get("temperature_2m") + " °C");
        result.put("humidity", current.get("relative_humidity_2m") + " %");


Number weatherCodeNumber = (Number) current.get("weather_code");
int weatherCode = weatherCodeNumber.intValue();

String condition;

if (weatherCode == 0) {
    condition = "clear sky";
} else if (weatherCode == 1 || weatherCode == 2 || weatherCode == 3) {
    condition = "cloudy";
} else if ((weatherCode >= 51 && weatherCode <= 67) || (weatherCode >= 80 && weatherCode <= 99)) {
    condition = "rain";
} else if (weatherCode >= 71 && weatherCode <= 77) {
    condition = "snow";
} else {
    condition = "cloudy";
}

result.put("condition", condition);
return result;
    }
}
