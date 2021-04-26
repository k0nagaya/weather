package com.emias.weather.web;

import com.emias.weather.model.Weather;
import com.emias.weather.repo.WeatherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext // нужно чтобы не падали джобы
class WeatherApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WeatherRepository repository;

	private final LocalDate date = LocalDate.of(2001,1,2);

	@BeforeEach
	public void setup() {
		if (repository.count() == 0) {
			repository.save(new Weather("London", 50D, date));
			repository.save(new Weather("London", 40D, date));
			repository.save(new Weather("London", 30D, date.plusDays(3)));
		}
	}

	@Test
	public void testGetLast() throws Exception {
		mockMvc.perform(get("/api/temp/")
				.contentType("application/json")
				.param("city", "London"))
				.andExpect(status().isOk())
				.andExpect(content().string("30.0"));
	}

	@Test
	public void testGetList() throws Exception {
		mockMvc.perform(get("/api/temp/")
				.contentType("application/json")
				.param("city", "London")
				.param("date", date.toString()))
				.andExpect(status().isOk())
				.andExpect(content().string("[50.0,40.0]"));
	}
}
