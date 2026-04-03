package com.example.mountainlog.repository;

import com.example.mountainlog.entity.Mountain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MountainRepository extends JpaRepository<Mountain, Integer> {

        // 山名（部分一致）で検索。ひらがな/カタカナ（name_kana）、英語名（name_en）でも検索可能
        @Query("SELECT m FROM Mountain m WHERE m.name LIKE %:keyword% OR m.nameEn LIKE %:keyword% OR m.nameKana LIKE %:keyword% OR m.prefecture LIKE %:keyword% ORDER BY m.name")
        List<Mountain> searchByKeyword(@Param("keyword") String keyword);

        @Query("SELECT m FROM Mountain m WHERE m.name LIKE %:keyword% OR m.nameEn LIKE %:keyword% OR m.nameKana LIKE %:keyword% OR m.prefecture LIKE %:keyword% ORDER BY m.name")
        List<Mountain> searchByKeyword(@Param("keyword") String keyword,
                        org.springframework.data.domain.Pageable pageable);

        // 百名山のみ取得
        List<Mountain> findByIsHyakumeizanTrue();

        // 都道府県で絞り込み
        List<Mountain> findByPrefectureContaining(String prefecture);

        // ページネーション対応の検索
        @Query("SELECT m FROM Mountain m WHERE m.name LIKE %:keyword% OR m.nameEn LIKE %:keyword% OR m.nameKana LIKE %:keyword% OR m.prefecture LIKE %:keyword%")
        org.springframework.data.domain.Page<Mountain> searchMountains(@Param("keyword") String keyword,
                        org.springframework.data.domain.Pageable pageable);

        // フィルタ機能付き検索
        @Query("SELECT m FROM Mountain m WHERE " +
                        "(:keyword IS NULL OR :keyword = '' OR m.name LIKE %:keyword% OR m.nameEn LIKE %:keyword% OR m.nameKana LIKE %:keyword%) AND "
                        +
                        "(:prefecture IS NULL OR :prefecture = '' OR m.prefecture LIKE %:prefecture%) AND " +
                        "(:difficulty IS NULL OR :difficulty = '' OR m.difficulty = :difficulty) AND " +
                        "(:minElevation IS NULL OR m.elevation >= :minElevation) AND " +
                        "(:maxElevation IS NULL OR m.elevation < :maxElevation)")
        org.springframework.data.domain.Page<Mountain> searchMountainsWithFilter(
                        @Param("keyword") String keyword,
                        @Param("prefecture") String prefecture,
                        @Param("difficulty") String difficulty,
                        @Param("minElevation") Integer minElevation,
                        @Param("maxElevation") Integer maxElevation,
                        org.springframework.data.domain.Pageable pageable);

}
