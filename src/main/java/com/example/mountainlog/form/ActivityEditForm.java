package com.example.mountainlog.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

// 作成用とは別に、更新用のFormクラスを作成
@Data
public class ActivityEditForm {

    // IDはURLパスで受け取るため、ここには含めなくても良いが、確認用として持っていても良い
    private Integer id;

    @Size(min = 1, max = 100, message = "タイトルは1文字以上100文字以内で入力してください")
    private String title;

    @NotNull(message = "日付を選択してください")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityDate;

    // 山マスタからの選択（任意）
    private Integer mountainId;

    @Size(max = 100, message = "場所は100文字以内で入力してください")
    private String location;

    private String description;

    // 以下、詳細情報 (ActivityDetail)
    private Double distanceKm;
    private Integer durationMinutes;
    private Integer elevationGain;
    private Integer maxElevation;
    private String paceNotes;

    // 画像アップロード用（更新時は必須ではないためNotNullは付けない）
    private MultipartFile imageFile;
}
