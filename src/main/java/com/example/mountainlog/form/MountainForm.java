package com.example.mountainlog.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MountainForm {

    private Integer mountainId;

    @NotBlank(message = "山名を入力してください")
    @Size(max = 100, message = "山名は100文字以内で入力してください")
    private String name;

    @Size(max = 100, message = "英語名は100文字以内で入力してください")
    private String nameEn;

    @Size(max = 100, message = "よみがなは100文字以内で入力してください")
    private String nameKana;

    @NotNull(message = "標高を入力してください")
    @Min(value = 0, message = "標高は0以上の数値を入力してください")
    private Integer elevation;

    @Size(max = 100, message = "都道府県は100文字以内で入力してください")
    private String prefecture;

    private Double latitude;

    private Double longitude;

    private Double typicalDistanceKm;

    @Min(value = 0, message = "時間は0以上の数値を入力してください")
    private Integer typicalDurationMinutes;

    @Min(value = 0, message = "累積標高は0以上の数値を入力してください")
    private Integer typicalElevationGain;

    @Size(max = 20, message = "難易度は20文字以内で入力してください")
    private String difficulty;

    private String description;

    private Boolean isHyakumeizan = false;

    private String imageUrl;

    private String imageCitation;
}
