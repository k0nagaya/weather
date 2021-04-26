package com.emias.weather.quartz;

import com.emias.weather.service.WeatherService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ObtainWeatherJob implements Job {

    @Autowired
    WeatherService service;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            service.obtainTemperatures();
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
