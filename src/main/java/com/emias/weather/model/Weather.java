package com.emias.weather.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class Weather {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private double temp;

    @Column(nullable = false)
    private LocalDate date;

    public Weather() {}

    public Weather(String city, double temp, LocalDate date) {
        this.city = city;
        this.temp = temp;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public double getTemp() {
        return temp;
    }

    public LocalDate getDate() {
        return date;
    }
}