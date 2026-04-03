package com.example.mountainlog.service;

import com.example.mountainlog.dto.WeatherResponseDto;
import com.example.mountainlog.entity.Mountain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${openweathermap.api.key}")
    private String apiKey;

    // OpenWeatherMapのCurrent Weather APIエンドポイント
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/weather";

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 指定された山の緯度・経度から現在の天気を取得する
     * 
     * @param mountain 天気を取得したい山
     * @param lang     言語 (en, ja など)
     * @return OpenWeatherMapからのレスポンス結果
     */
    public WeatherResponseDto getCurrentWeather(Mountain mountain, String lang) {
        // 緯度経度が登録されていない場合はnullを返すか例外を投げる
        if (mountain.getLatitude() == null || mountain.getLongitude() == null) {
            return null;
        }

        // URIを構築する（units=metricで摂氏温度を指定、langで言語を指定）
        URI uri = UriComponentsBuilder.fromHttpUrl(API_URL)
                .queryParam("lat", mountain.getLatitude())
                .queryParam("lon", mountain.getLongitude())
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("lang", lang)
                .build()
                .toUri();

        // APIをコールしてDTOにマッピング
        WeatherResponseDto response = restTemplate.getForObject(uri, WeatherResponseDto.class);

        // 取得成功時は対象の山IDを付与して返す
        if (response != null) {
            response.setMountainId(mountain.getMountainId());
        }

        return response;
    }
}
