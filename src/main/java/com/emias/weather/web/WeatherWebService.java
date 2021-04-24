package com.emias.weather.web;

import com.emias.weather.model.Weather;
import com.emias.weather.service.WeatherService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api")
public class WeatherWebService {
    final WeatherService service;

    private final RestTemplate restTemplate;

    private final String apiKey = "86b939caa0mshb0eff32f50b862ep13fa54jsnad2520933c65";

    public WeatherWebService(WeatherService service, RestTemplateBuilder builder) {
        this.service = service;
        this.restTemplate = builder.build(); // fixme передавать сразу template?
    }

    @GetMapping("/asdf")
    public List<Weather> find() {
        String url = "https://community-open-weather-map.p.rapidapi.com/weather" +
                "?q=London%2Cuk" +
                "&lat=0&lon=0" +
                "&callback=test&id=2172797" +
                "&lang=null&units=%22metric%22%20or%20%22imperial%22&mode=json%2C%20html\"";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("x-rapidapi-key", "86b939caa0mshb0eff32f50b862ep13fa54jsnad2520933c65");
        headers.set("x-rapidapi-host", "community-open-weather-map.p.rapidapi.com");

        ResponseEntity<String> response = this.restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        return null;
    }
}
