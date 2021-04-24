package com.emias.weather.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Weather {
    @Id
    @GeneratedValue
    // TODO без id?
    private Long id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private float celcius;

    // fixme нужен ли часовой пояс? или хранить только в базе?
    @Column(nullable = false)
    private Timestamp date;

    public Weather() {}

    public Weather(Long id, String city, float celcius, Timestamp date) {
        this.id = id;
        this.city = city;
        this.celcius = celcius;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public float getCelcius() {
        return celcius;
    }

    public Timestamp getDate() {
        return date;
    }
}