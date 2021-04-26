package com.emias.weather.service;

import com.emias.weather.config.AppProperties;
import com.emias.weather.model.Weather;
import com.emias.weather.repo.WeatherRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class WeatherServiceTest {

    private WeatherService service;

    private AutoCloseable closeable;

    @Mock
    private WeatherRepository repository;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AppProperties appProperties;

    @Mock
    private Weather weatherMsc1;

    @Mock
    private Weather weatherMsc2;

    @Mock
    private Weather weatherSpb;

    private final String mscName = "Moscow,ru";
    private final String spbName = "Saint Petersburg,ru";

    private final LocalDate dateOld = LocalDate.of(2021, 4, 22);
    private final LocalDate dateNew = LocalDate.of(2021, 4, 25);

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);

        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        service = new WeatherService(repository, restTemplateBuilder, appProperties);

        when(weatherMsc1.getId()).thenReturn(1L);
        when(weatherMsc2.getId()).thenReturn(2L);
        when(weatherSpb.getId()).thenReturn(3L);

        when(weatherMsc1.getCity()).thenReturn(mscName);
        when(weatherMsc2.getCity()).thenReturn(mscName);
        when(weatherSpb.getCity()).thenReturn(spbName);

        when(weatherMsc1.getDate()).thenReturn(dateOld);
        when(weatherMsc2.getDate()).thenReturn(dateNew);
        when(weatherSpb.getDate()).thenReturn(dateNew);

        when(repository.findAll()).thenReturn(Arrays.asList(weatherMsc1, weatherMsc2, weatherSpb));
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }

    @Test
    public void testGetLastNotFound() throws Exception {
        Exception exception = assertThrows(Exception.class, () -> service.getLast("Prague,cz"));
        assertEquals(exception.getMessage(), "Weather for city Prague,cz not found");
    }

    @Test
    public void testGetLast() throws Exception {
        assertSame(weatherMsc2, service.getLast(mscName));
    }

    @Test
    public void testGetList() {
        assertIterableEquals(Collections.singletonList(weatherSpb), service.getList(spbName, dateNew));
    }

    @Test
    public void testObtainTemp() throws Exception {
        ResponseEntity<String> responseEntity = mock(ResponseEntity.class);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(String.class)))
                .thenReturn(responseEntity);

        when(appProperties.getCities()).thenReturn(Arrays.asList(mscName, spbName));

        when(responseEntity.getStatusCode()).thenReturn(HttpStatus.OK);
        when(responseEntity.getBody()).thenReturn("{ \"main\": { \"temp\": 300.00 }}");

        when(repository.save(any(Weather.class))).thenReturn(weatherSpb);

        service.obtainTemperatures();
    }

    @Test
    public void testCreateFails() {
        when(weatherSpb.getId()).thenReturn(null);
        when(repository.save(any(Weather.class))).thenReturn(weatherSpb);

        assertThrows(RuntimeException.class, () -> service.create(spbName, 13D));
    }

    @Test
    public void testCreate() {
        when(repository.save(any(Weather.class))).thenReturn(weatherSpb);

        service.create(spbName, 13D);

        WeatherMatcher weatherMatcher = new WeatherMatcher(new Weather(spbName, 13.00D, LocalDate.now()));
        verify(repository, times(1)).save(argThat(weatherMatcher));
    }

    private static class WeatherMatcher implements ArgumentMatcher<Weather> {

        private final Weather left;

        public WeatherMatcher(Weather left) {
            this.left = left;
        }

        @Override
        public boolean matches(Weather right) {
            if (left.getId() == null && right.getId() == null) {
                return left.getCity().equals(right.getCity())
                        && left.getTemp() == right.getTemp()
                        && left.getDate().equals(right.getDate());
            } else if (left.getId() != null && right.getId() != null) {
                return left.getId().equals(right.getId());
            } else {
                return false;
            }
        }
    }
}