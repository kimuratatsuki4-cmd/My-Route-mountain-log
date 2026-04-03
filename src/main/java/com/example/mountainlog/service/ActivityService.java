package com.example.mountainlog.service;

import com.example.mountainlog.entity.Activity;
import com.example.mountainlog.entity.ActivityDetail;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /**
     * 活動ログを新規登録します。
     * Activity（基本情報）とActivityDetail（詳細情報）を紐付けて一括保存します。
     *
     * @param activity  活動の基本情報
     * @param detail    活動の詳細情報（数値データなど）
     * @param imageFile 画像ファイル
     */
    @Transactional
    public Activity createActivity(Activity activity, ActivityDetail detail, MultipartFile imageFile) {
        // nullチェック
        Objects.requireNonNull(activity, "Activity must not be null");
        Objects.requireNonNull(detail, "ActivityDetail must not be null");

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename();
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/images/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            activity.setImageName(hashedImageName);
        }

        // 詳細情報に親となるActivityをセット（双方向の紐付け）
        detail.setActivity(activity);

        // Activity側に詳細情報をセット
        activity.setActivityDetail(detail);

        // Activityを保存すると、Cascade設定によりActivityDetailも自動的に保存されます
        return activityRepository.save(activity);
    }

    /**
     * 活動ログを更新します。
     * 既存のActivityが見つからない場合はEntityNotFoundException（またはRuntimeException）を投げます。
     *
     * @param id              更新対象のActivity ID
     * @param updatedActivity 更新内容を含むActivity
     * @param updatedDetail   更新内容を含むActivityDetail
     * @param imageFile       画像ファイル
     * @return 更新後のActivity
     */
    @Transactional
    public Activity updateActivity(Integer id, Activity updatedActivity, ActivityDetail updatedDetail,
            MultipartFile imageFile) {
        // idがnullの場合は新規作成
        if (id == null) {
            return createActivity(updatedActivity, updatedDetail, imageFile);
        }
        // 既存データの取得
        Activity existingActivity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid activity Id:" + id));

        // 画像の更新処理
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageName = imageFile.getOriginalFilename();
            String hashedImageName = generateNewFileName(imageName);
            Path filePath = Paths.get("src/main/resources/static/images/" + hashedImageName);
            copyImageFile(imageFile, filePath);
            existingActivity.setImageName(hashedImageName);
        }

        // Activityの基本情報を更新
        existingActivity.setTitle(updatedActivity.getTitle());
        existingActivity.setActivityDate(updatedActivity.getActivityDate());
        // existingActivity.setCategory(updatedActivity.getCategory());
        existingActivity.setLocation(updatedActivity.getLocation());
        existingActivity.setDescription(updatedActivity.getDescription());

        // ActivityDetailの更新
        ActivityDetail existingDetail = existingActivity.getActivityDetail();
        if (existingDetail == null) {
            // 万が一Detailが存在しない場合は新規作成して紐付ける
            existingDetail = new ActivityDetail();
            existingDetail.setActivity(existingActivity);
            existingActivity.setActivityDetail(existingDetail);
        }
        existingDetail.setDistanceKm(updatedDetail.getDistanceKm());
        existingDetail.setDurationMinutes(updatedDetail.getDurationMinutes());
        existingDetail.setElevationGain(updatedDetail.getElevationGain());
        existingDetail.setMaxElevation(updatedDetail.getMaxElevation());
        existingDetail.setPaceNotes(updatedDetail.getPaceNotes());

        // 保存（Managed状態なのでsaveしなくてもコミット時に反映されるが、明示的にsaveしても良い）
        return activityRepository.save(existingActivity);
    }

    /**
     * そのユーザーの全活動記録を日付の新しい順に取得します。
     * ユーザーがnullの場合は空のリストを返します。
     */
    public List<Activity> findAllActivitiesByUser(User user) {
        return Optional.ofNullable(user)
                .map(activityRepository::findByUserOrderByActivityDateDesc)
                .orElse(List.of());
    }

    /**
     * ユーザーの全活動記録を指定されたソート順で取得します。
     * 
     * @param user
     * @param sortKey (date_desc, date_asc, elevation_desc, elevation_asc)
     * @return
     */
    public List<Activity> getActivitiesByUserWithSort(User user, String sortKey) {
        if (user == null) {
            return List.of();
        }

        Sort sort;

        switch (sortKey) {
            case "date_asc":
                sort = Sort.by("activityDate").ascending();
                break;
            case "elevation_desc":
                // 標高順（詳細情報とJOINしてソート）
                // ActivityDetail.maxElevation でソートする場合
                sort = Sort.by("activityDetail.maxElevation").descending();
                break;
            case "elevation_asc":
                sort = Sort.by("activityDetail.maxElevation").ascending();
                break;
            case "date_desc":
            default:
                sort = Sort.by("activityDate").descending();
                break;
        }

        return activityRepository.findByUser(user, sort);
    }

    /**
     * ユーザーの活動統計情報を取得します。
     * 
     * @param user
     * @return ActivityStatsDto
     */
    public com.example.mountainlog.dto.ActivityStatsDto getActivityStats(User user) {
        if (user == null) {
            return new com.example.mountainlog.dto.ActivityStatsDto();
        }

        // 全件数を取得（カテゴリ区別なし）
        long totalCount = activityRepository.countByUser(user);

        Double totalDistance = activityRepository.sumDistanceByUser(user);
        Long totalElevation = activityRepository.sumElevationGainByUser(user);
        Long totalDuration = activityRepository.sumDurationMinutesByUser(user);

        return new com.example.mountainlog.dto.ActivityStatsDto(
                totalCount,
                totalDistance != null ? totalDistance : 0.0,
                totalElevation != null ? totalElevation : 0,
                totalDuration != null ? totalDuration : 0);
    }

    /**
     * そのユーザーの最新活動記録を3件取得します。
     * ユーザーがnullの場合は空のリストを返します。
     */
    public List<Activity> findTop3ActivitiesByUser(User user) {
        return Optional.ofNullable(user)
                .map(activityRepository::findTop3ByUserOrderByActivityDateDesc)
                .orElse(List.of());
    }

    /**
     * ID指定で活動ログを1件取得します。
     */
    public Optional<Activity> findActivityById(Integer id) {
        return Optional.ofNullable(id)
                .flatMap(activityRepository::findById);
    }

    /**
     * 活動ログを削除します。
     * Cascade設定により、関連するDetailやImageも削除されます。
     */
    @Transactional
    public void deleteActivity(Integer id) {
        activityRepository.deleteById(Objects.requireNonNull(id));
    }

    /**
     * ユーザーの年別統計データを取得します（折れ線グラフ用）。
     * 各年ごとに活動回数・総距離・総獲得標高を集計します。
     *
     * @param user 対象ユーザー
     * @return 年別統計のリスト（年の昇順）
     */
    public List<com.example.mountainlog.dto.YearlyStatsDto> getYearlyStats(User user) {
        if (user == null) {
            return List.of();
        }

        // ユーザーの全活動を取得
        List<Activity> activities = activityRepository.findByUserOrderByActivityDateDesc(user);

        // java.util.stream を使って年ごとにグループ化し、集計する
        // Activity.activityDate は java.sql.Date なので、toLocalDate().getYear() で年を取得
        java.util.Map<Integer, List<Activity>> byYear = activities.stream()
                .filter(a -> a.getActivityDate() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        a -> a.getActivityDate().toLocalDate().getYear()));

        // 年ごとに集計して YearlyStatsDto のリストを作成
        return byYear.entrySet().stream()
                .map(entry -> {
                    int year = entry.getKey();
                    List<Activity> yearActivities = entry.getValue();

                    long count = yearActivities.size();

                    // 距離の合計（ActivityDetailがnullでない場合のみ）
                    double distance = yearActivities.stream()
                            .filter(a -> a.getActivityDetail() != null && a.getActivityDetail().getDistanceKm() != null)
                            .mapToDouble(a -> a.getActivityDetail().getDistanceKm())
                            .sum();

                    // 獲得標高の合計
                    long elevation = yearActivities.stream()
                            .filter(a -> a.getActivityDetail() != null
                                    && a.getActivityDetail().getElevationGain() != null)
                            .mapToLong(a -> a.getActivityDetail().getElevationGain())
                            .sum();

                    return new com.example.mountainlog.dto.YearlyStatsDto(year, count, distance, elevation);
                })
                .sorted(java.util.Comparator.comparingInt(com.example.mountainlog.dto.YearlyStatsDto::getYear))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * ユーザーと山を指定して、その山に関連する活動ログを取得します。
     * 
     * @param user
     * @param mountain
     * @return
     */
    public List<Activity> getActivitiesByUserAndMountain(User user, com.example.mountainlog.entity.Mountain mountain) {
        if (user == null || mountain == null) {
            return List.of();
        }
        return activityRepository.findByUserAndMountainOrderByActivityDateDesc(user, mountain);
    }

    /**
     * ユーザーのランダムな活動履歴を指定件数取得します。
     */
    public List<Activity> getRandomActivities(User user, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        if (user == null || user.getUserId() == null) {
            return List.of();
        }
        return activityRepository.findRandomActivitiesByUser(user.getUserId(), pageable);
    }

    // UUIDを使って生成したファイル名を返す
    public String generateNewFileName(String fileName) {
        String[] fileNames = fileName.split("\\.");

        for (int i = 0; i < fileNames.length - 1; i++) {
            fileNames[i] = UUID.randomUUID().toString();
        }

        String hashedFileName = String.join(".", fileNames);

        return hashedFileName;
    }

    // 画像ファイルを指定したファイルにコピーする
    public void copyImageFile(MultipartFile imageFile, Path filePath) {
        try {
            Files.copy(imageFile.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
