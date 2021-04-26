package com.emias.weather.service;

import com.emias.weather.config.AppProperties;
import com.emias.weather.model.Weather;
import com.emias.weather.repo.WeatherRepository;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class WeatherService {

    final WeatherRepository repository;

    final RestTemplate restTemplate;

    final AppProperties properties;

    public WeatherService(WeatherRepository repository,
                          RestTemplateBuilder restTemplateBuilder,
                          AppProperties properties) {
        this.repository = repository;
        this.restTemplate = restTemplateBuilder.build();
        this.properties = properties;
    }

    public Weather getLast(String city) throws Exception {
        Optional<Weather> weather = StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(w -> city.equals(w.getCity()))
                .max(Comparator.comparingLong(Weather::getId));

        if (!weather.isPresent()) {
            throw new Exception("Weather for city " + city + " not found");
        }

        return weather.get();
    }

    public List<Weather> getList(String city, LocalDate date) {
        return StreamSupport.stream(repository.findAll().spliterator(), false)
                .filter(w -> date.equals(w.getDate()) && city.equals(w.getCity()))
                .collect(Collectors.toList());
    }

    public void obtainTemperatures() throws Exception {
        for (String s : properties.getCities()) {
            obtainTempFromOpenWeatherMap(s);
        }
    }

    private void obtainTempFromOpenWeatherMap(String city) throws Exception {
        String url = "https://community-open-weather-map.p.rapidapi.com/weather" +
                "?q=" + city + "&units=%22metric";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        // Ключа хватит на 600 запросов, потом надо делать новый
        headers.set("x-rapidapi-key", "86b939caa0mshb0eff32f50b862ep13fa54jsnad2520933c65");
        headers.set("x-rapidapi-host", "community-open-weather-map.p.rapidapi.com");

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new Exception("Something gone wrong");
        }

        // сервис возвращает кельвины
        double temp = extractTempRest(response.getBody()) - 273;

        create(city, temp);
    }

    // ради одного поля описывать отдельный дто для маппинга не стал
    private Double extractTempRest(String json) {
        JsonParser parser = new JacksonJsonParser();
        return (Double)((Map<String, Object>) parser.parseMap(json).get("main")).get("temp");
    }

    public void create(String city, double temp) {
        Weather weather = repository.save(new Weather(city, temp, LocalDate.now()));

        if (weather.getId() == null) {
            throw new RuntimeException("Fail on saving entity");
        }
    }
}
