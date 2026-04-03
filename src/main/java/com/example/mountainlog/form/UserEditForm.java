package com.example.mountainlog.form;

import com.example.mountainlog.entity.ExperienceLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UserEditForm {

    private Integer userId;

    @NotBlank(message = "ユーザー名を入力してください")
    @Size(max = 50, message = "ユーザー名は50文字以内で入力してください")
    private String username;

    // メールアドレスは変更不可とするため、バリデーションは不要だが表示用に保持
    private String email;

    @Size(max = 255, message = "住所は255文字以内で入力してください")
    private String address;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private ExperienceLevel experienceLevel;
}
