package com.example.mountainlog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MountainMapDto {
    private Integer id;
    private String name;
    private Double lat;
    private Double lng;
    private Integer elevation;
    private Boolean isClimbed;
    private LocalDate lastClimbDate;
}
