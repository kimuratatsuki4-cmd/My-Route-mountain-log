package com.example.mountainlog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 年別の統計データを保持するDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearlyStatsDto {
    private int year;
    private long totalActivities;
    private double totalDistance;
    private long totalElevation;
}
