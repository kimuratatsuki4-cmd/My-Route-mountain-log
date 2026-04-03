package com.example.mountainlog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatsDto {
    private long totalActivities;
    private double totalDistance;
    private long totalElevation;
    private long totalDuration;
}
