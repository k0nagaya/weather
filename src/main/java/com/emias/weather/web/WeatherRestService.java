package com.emias.weather.web;

import com.emias.weather.model.Weather;
import com.emias.weather.service.WeatherService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class WeatherRestService {
    final WeatherService service;

    public WeatherRestService(WeatherService service) {
        this.service = service;
    }

    @GetMapping(value = "/temp", params = {"city"})
    public Double getTemp(@RequestParam String city) throws Exception {
        return service.getLast(city).getTemp();
    }

    @GetMapping(value = "/temp", params = {"city", "date"})
    public List<Double> getTemp(@RequestParam String city,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.getList(city, date)
                .stream()
                .map(Weather::getTemp)
                .collect(Collectors.toList());
    }
}
