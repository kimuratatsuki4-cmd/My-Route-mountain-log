package com.example.mountainlog.controller;

import com.example.mountainlog.dto.WeatherResponseDto;
import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.service.MountainService;
import com.example.mountainlog.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/premium/weather")
public class PremiumWeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private MountainService mountainService;

    /**
     * 特定の山の現在の天気を取得する（プレミアム会員専用）
     */
    @GetMapping("/{mountainId}")
    @PreAuthorize("hasRole('ROLE_PREMIUM')")
    public ResponseEntity<WeatherResponseDto> getMountainWeather(@PathVariable Integer mountainId, Locale locale) {

        Optional<Mountain> mountainOpt = mountainService.findById(mountainId);

        if (mountainOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Mountain mountain = mountainOpt.get();
        WeatherResponseDto weather = weatherService.getCurrentWeather(mountain, locale.getLanguage());

        if (weather == null) {
            // 緯度経度がない場合やAPIエラーの場合
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        return ResponseEntity.ok(weather);
    }
}
