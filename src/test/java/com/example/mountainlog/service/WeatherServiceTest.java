package com.example.mountainlog.service;

import com.example.mountainlog.dto.WeatherResponseDto;
import com.example.mountainlog.entity.Mountain;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Import;
import com.example.mountainlog.config.RestTemplateConfig;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(WeatherService.class)
@Import(RestTemplateConfig.class)
@TestPropertySource(properties = {
        "openweathermap.api.key=test_api_key"
})
@SuppressWarnings("null")
public class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private Mountain testMountain;

    @BeforeEach
    void setUp() {
        testMountain = new Mountain();
        testMountain.setMountainId(1);
        testMountain.setName("富士山");
        testMountain.setLatitude(35.3606);
        testMountain.setLongitude(138.7274);
    }

    @Test
    void testGetCurrentWeather_Success() throws JsonProcessingException {
        // Arrange
        WeatherResponseDto mockResponse = new WeatherResponseDto();
        mockResponse.setMountainId(1);

        String expectedUri = "https://api.openweathermap.org/data/2.5/weather?lat=35.3606&lon=138.7274&appid=test_api_key&units=metric&lang=ja";

        mockServer.expect(requestTo(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON));

        // Act
        WeatherResponseDto result = weatherService.getCurrentWeather(testMountain, "ja");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMountainId()).isEqualTo(testMountain.getMountainId());
        mockServer.verify();
    }

    @Test
    void testGetCurrentWeather_NoCoordinates() {
        // Arrange
        Mountain noCoordMountain = new Mountain();
        noCoordMountain.setMountainId(2);
        noCoordMountain.setName("座標なしの山");
        // Latitude and Longitude are null
        // Act
        WeatherResponseDto result = weatherService.getCurrentWeather(noCoordMountain, "ja");

        // Assert
        assertThat(result).isNull();
        // Since it returns early, no API call is made. Verification of 0 calls is
        // implicit
        // because we didn't setup mockServer.expect()
    }
}
