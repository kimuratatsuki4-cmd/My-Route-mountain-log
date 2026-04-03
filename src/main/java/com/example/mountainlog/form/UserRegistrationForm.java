package com.example.mountainlog.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationForm {

    @NotBlank(message = "ユーザー名を入力してください")
    private String username;

    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "正しいメールアドレス形式で入力してください")
    private String email;

    @NotBlank(message = "パスワードを入力してください")
    @Pattern(regexp = "^[a-zA-Z0-9]{8,}$", message = "パスワードは英数字8文字以上で入力してください")
    private String password;

    @NotBlank(message = "パスワード（確認用）を入力してください")
    private String passwordConfirmation;

    @NotBlank(message = "住所を入力してください")
    @Size(max = 255, message = "住所は255文字以内で入力してください")
    private String address;

    @jakarta.validation.constraints.NotNull(message = "誕生日を入力してください")
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private java.time.LocalDate birthDate;

    @NotNull(message = "経験レベルを入力してください")
    private com.example.mountainlog.entity.ExperienceLevel experienceLevel;
}
