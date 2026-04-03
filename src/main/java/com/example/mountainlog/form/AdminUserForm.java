package com.example.mountainlog.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminUserForm {

    private Integer userId;

    @NotBlank(message = "ユーザー名を入力してください")
    private String username;

    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "正しいメールアドレス形式で入力してください")
    private String email;

    // パスワードは更新時には任意とするため、必須バリデーションは外す
    private String password;

    @NotNull(message = "権限を選択してください")
    private Integer roleId;

    @NotNull(message = "有効状態を選択してください")
    private Boolean enabled = true;
}
