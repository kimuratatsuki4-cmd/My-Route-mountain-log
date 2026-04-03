package com.example.mountainlog.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * OpenWeatherMap APIからのレスポンス結果を受け取るDTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponseDto {

    private List<WeatherStatus> weather;
    private MainData main;
    private WindData wind;

    // 取得対象の山（フロントエンド返却時に付与する用）
    private Integer mountainId;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WeatherStatus {
        private int id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MainData {
        private double temp;
        @JsonProperty("feels_like")
        private double feelsLike;
        @JsonProperty("temp_min")
        private double tempMin;
        @JsonProperty("temp_max")
        private double tempMax;
        private int humidity;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WindData {
        private double speed;
        private int deg;
    }
}
