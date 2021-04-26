package com.emias.weather.config;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AppProperties {

    private List<String> cities;

    /**
     * Интервал между запросами для кварца
     */
    private int interval;

    public AppProperties() {
        this.cities = Arrays.asList("London,uk", "Irkutsk,ru", "Tokyo,jp");
        this.interval = 180; // раз в три минуты
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
