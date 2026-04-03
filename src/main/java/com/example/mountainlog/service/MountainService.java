package com.example.mountainlog.service;

import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.repository.ActivityRepository;
import com.example.mountainlog.repository.MountainRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MountainService {

    private final MountainRepository mountainRepository;
    private final ActivityRepository activityRepository;

    public MountainService(MountainRepository mountainRepository, ActivityRepository activityRepository) {
        this.mountainRepository = mountainRepository;
        this.activityRepository = activityRepository;
    }

    // 作成画面用（リスト返却）
    public List<Mountain> searchMountains(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        // サジェスト用に最大20件に制限
        return mountainRepository.searchByKeyword(keyword.trim(), PageRequest.of(0, 20));
    }

    // 一覧画面用（ページネーション）
    public Page<Mountain> searchMountains(String keyword, Pageable pageable) {
        return searchMountainsWithFilter(keyword, null, null, null, null, pageable);
    }

    // フィルタ機能付き検索
    public Page<Mountain> searchMountainsWithFilter(String keyword, String prefecture, String difficulty,
            Integer minElevation, Integer maxElevation, Pageable pageable) {
        String safeKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        String safePrefecture = (prefecture != null && !prefecture.isBlank()) ? prefecture.trim() : null;
        String safeDifficulty = (difficulty != null && !difficulty.isBlank() && !difficulty.equals("ALL"))
                ? difficulty.trim()
                : null;

        return mountainRepository.searchMountainsWithFilter(safeKeyword, safePrefecture, safeDifficulty,
                minElevation, maxElevation, Objects.requireNonNull(pageable));
    }

    // 全件取得（ページネーション）
    public Page<Mountain> getMountains(Pageable pageable) {
        return mountainRepository.findAll(Objects.requireNonNull(pageable));
    }

    public Optional<Mountain> findById(Integer id) {
        return mountainRepository.findById(Objects.requireNonNull(id));
    }

    public List<Mountain> findAll() {
        return mountainRepository.findAll();
    }

    public List<Mountain> findHyakumeizan() {
        return mountainRepository.findByIsHyakumeizanTrue();
    }

    /**
     * ユーザーにおすすめの山を提案する
     * 1. まだ登っていない山
     * 2. ユーザーのレベルに合った難易度
     * 3. ユーザーの住所（都道府県）に近い山を優先
     */
    public List<Mountain> getRecommendedMountains(User user) {
        // 1. 既登の山IDを取得
        List<Integer> visitedIds = activityRepository.findVisitedMountainIdsByUser(user);

        // 2. 全山取得
        List<Mountain> allMountains = mountainRepository.findAll();

        // 3. ユーザーのレベルに応じた難易度フィルタ
        Set<String> targetDifficulties = getTargetDifficulties(user);

        // 4. ユーザーの住所から都道府県抽出
        String userPrefecture = extractPrefecture(user.getAddress());

        // 5. フィルタリングとスコアリング
        List<Mountain> candidates = allMountains.stream()
                // 既登の山を除外
                .filter(m -> !visitedIds.contains(m.getMountainId()))
                // 難易度フィルタ (上級者はフィルタなし)
                .filter(m -> targetDifficulties.isEmpty()
                        || (m.getDifficulty() != null && targetDifficulties.contains(m.getDifficulty())))
                .collect(Collectors.toList());

        // 候補がない場合は全山から未登のものを選ぶ
        if (candidates.isEmpty()) {
            candidates = allMountains.stream().filter(m -> !visitedIds.contains(m.getMountainId()))
                    .collect(Collectors.toList());
        }

        // それでもなければ空リストを返す
        if (candidates.isEmpty()) {
            return List.of();
        }

        // 6. 都道府県マッチで優先度付けしてランダム選出
        // 同じ都道府県の山を優先リストへ
        List<Mountain> prefMatchMountains = new ArrayList<>();
        List<Mountain> otherMountains = new ArrayList<>();

        if (userPrefecture != null) {
            for (Mountain m : candidates) {
                if (m.getPrefecture() != null && m.getPrefecture().contains(userPrefecture)) {
                    prefMatchMountains.add(m);
                } else {
                    otherMountains.add(m);
                }
            }
        } else {
            otherMountains.addAll(candidates);
        }

        Collections.shuffle(prefMatchMountains);
        Collections.shuffle(otherMountains);

        // 最大3件返す (都道府県マッチから最大2件 + 残り)
        List<Mountain> result = new ArrayList<>();

        // 都道府県マッチから2件とる
        result.addAll(prefMatchMountains.stream().limit(2).collect(Collectors.toList()));

        // 残り枠をその他から埋める
        int remaining = 3 - result.size();
        if (remaining > 0) {
            result.addAll(otherMountains.stream().limit(remaining).collect(Collectors.toList()));
        }

        return result;
    }

    private Set<String> getTargetDifficulties(User user) {
        Set<String> difficulties = new HashSet<>();
        if (user.getExperienceLevel() == null) {
            difficulties.add("EASY");
            return difficulties;
        }

        switch (user.getExperienceLevel()) {
            case BEGINNER:
                difficulties.add("EASY");
                break;
            case INTERMEDIATE:
                difficulties.add("EASY");
                difficulties.add("MODERATE");
                break;
            case ADVANCED:
                // 空なら制限なしとする
                break;
        }
        return difficulties;
    }

    private String extractPrefecture(String address) {
        if (address == null || address.isEmpty()) {
            return null;
        }
        // 単純に "都", "道", "府", "県" のいずれかを含む最初の数文字を抽出
        // 例: 東京都新宿区 -> 東京都
        // 神奈川県横浜市 -> 神奈川県
        int index = -1;
        if ((index = address.indexOf("県")) != -1)
            return address.substring(0, index + 1);
        if ((index = address.indexOf("都")) != -1)
            return address.substring(0, index + 1);
        if ((index = address.indexOf("府")) != -1)
            return address.substring(0, index + 1);
        if ((index = address.indexOf("道")) != -1)
            return address.substring(0, index + 1);

        return null;
    }

    public List<com.example.mountainlog.dto.MountainMapDto> getMountainMapData(User user) {
        // 全山取得
        List<Mountain> allMountains = mountainRepository.findAll();

        // 登頂済み情報のマップ作成 (MountainID -> LastClimbDate)
        Map<Integer, java.time.LocalDate> climbedMap = new HashMap<>();
        if (user != null) {
            List<com.example.mountainlog.entity.Activity> activities = activityRepository
                    .findByUserAndMountainNotNull(user);
            for (com.example.mountainlog.entity.Activity activity : activities) {
                Integer mId = activity.getMountain().getMountainId();
                // java.sql.Date -> java.time.LocalDate conversion
                java.time.LocalDate date = activity.getActivityDate().toLocalDate();

                // Keep the latest date
                if (!climbedMap.containsKey(mId) || date.isAfter(climbedMap.get(mId))) {
                    climbedMap.put(mId, date);
                }
            }
        }

        return allMountains.stream()
                .map(m -> new com.example.mountainlog.dto.MountainMapDto(
                        m.getMountainId(),
                        m.getName(),
                        m.getLatitude(),
                        m.getLongitude(),
                        m.getElevation(),
                        climbedMap.containsKey(m.getMountainId()),
                        climbedMap.get(m.getMountainId())))
                .collect(Collectors.toList());
    }
}
