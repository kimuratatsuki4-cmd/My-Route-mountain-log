package com.example.mountainlog.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "activity_details")
@Data
public class ActivityDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Integer detailId;

    // Activityとの1対1の関係。外部キーを持つ側
    @OneToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    // 活動距離 (km)
    @Column(name = "distance_km")
    private Double distanceKm;

    // 活動時間 (分)
    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // 獲得標高 (m)
    @Column(name = "elevation_gain")
    private Integer elevationGain;

    // 最高地点の標高 (m)
    @Column(name = "max_elevation")
    private Integer maxElevation;

    // ペース（標準タイム比など、必要であれば）
    @Column(name = "pace_notes")
    private String paceNotes;
}
