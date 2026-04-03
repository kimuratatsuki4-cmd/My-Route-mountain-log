package com.example.mountainlog.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mountains")
@Data
public class Mountain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mountain_id")
    private Integer mountainId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "name_en", length = 100)
    private String nameEn;

    @Column(name = "name_kana", length = 100)
    private String nameKana;

    @Column(name = "elevation", nullable = false)
    private Integer elevation;

    @Column(name = "prefecture", length = 100)
    private String prefecture;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "typical_distance_km")
    private Double typicalDistanceKm;

    @Column(name = "typical_duration_minutes")
    private Integer typicalDurationMinutes;

    @Column(name = "typical_elevation_gain")
    private Integer typicalElevationGain;

    @Column(name = "difficulty", length = 20)
    private String difficulty;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_hyakumeizan")
    private Boolean isHyakumeizan;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_citation")
    private String imageCitation;
}
