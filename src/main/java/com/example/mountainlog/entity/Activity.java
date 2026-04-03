package com.example.mountainlog.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.sql.Timestamp;

// 活動ログ（登山や旅行の基本情報）を管理するエンティティ
@Entity
@Table(name = "activities")
@Data
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Integer activityId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "activity_date", nullable = false)
    private Date activityDate;

    // 山マスタとの関連（任意）。選択すると距離・標高等が自動入力される
    @ManyToOne
    @JoinColumn(name = "mountain_id")
    private Mountain mountain;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @OneToOne(mappedBy = "activity", cascade = CascadeType.ALL)
    private ActivityDetail activityDetail;

    @Column(name = "image_name")
    private String imageName;
}
