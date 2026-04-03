package com.example.mountainlog.repository;

import com.example.mountainlog.entity.Activity;
import com.example.mountainlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    // 特定のユーザーの活動を日付の新しい順に取得
    List<Activity> findByUserOrderByActivityDateDesc(User user);

    // カテゴリ（登山・旅行）ごとに絞り込み
    // List<Activity> findByUserAndCategory(User user, Category category);

    // 最新3件を取得
    List<Activity> findTop3ByUserOrderByActivityDateDesc(User user);

    // 特定の日付以降のアクティビティ数を取得 (統計用)
    long countByActivityDateAfter(java.sql.Date date);

    // ソート条件を指定してユーザーの活動を取得
    List<Activity> findByUser(User user, Sort sort);

    // 統計用: ユーザーの全活動件数
    long countByUser(User user);

    // 統計用: 総距離 (ActivityDetailとJOIN)
    @Query("SELECT SUM(d.distanceKm) FROM Activity a JOIN a.activityDetail d WHERE a.user = :user")
    Double sumDistanceByUser(User user);

    // 統計用: 総獲得標高
    @Query("SELECT SUM(d.elevationGain) FROM Activity a JOIN a.activityDetail d WHERE a.user = :user")
    Long sumElevationGainByUser(User user);

    // 統計用: 総活動時間
    @Query("SELECT SUM(d.durationMinutes) FROM Activity a JOIN a.activityDetail d WHERE a.user = :user")
    Long sumDurationMinutesByUser(User user);

    // ユーザーが既に登った山のIDリストを取得
    @Query("SELECT DISTINCT a.mountain.mountainId FROM Activity a WHERE a.user = :user AND a.mountain IS NOT NULL")
    List<Integer> findVisitedMountainIdsByUser(User user);

    // 特定のユーザーと山に関連する活動を取得（新しい順）
    List<Activity> findByUserAndMountainOrderByActivityDateDesc(User user,
            com.example.mountainlog.entity.Mountain mountain);

    // ユーザーの活動のうち、山が紐付いているものを取得
    List<Activity> findByUserAndMountainNotNull(User user);
    
    // ユーザーのランダムな活動履歴を取得 (MySQL/H2対応)
    @Query(value = "SELECT * FROM activities WHERE user_id = :userId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Activity> findRandomActivitiesByUser(Integer userId, Integer limit);
}
