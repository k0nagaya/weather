package com.emias.weather.repo;

import com.emias.weather.model.Weather;
import org.springframework.data.repository.CrudRepository;

public interface WeatherRepository extends CrudRepository<Weather, Long> {

}
