package com.example.mountainlog.controller;

import com.example.mountainlog.entity.User;
import com.example.mountainlog.entity.VerificationToken;
import com.example.mountainlog.form.UserRegistrationForm;
import com.example.mountainlog.service.UserService;
import com.example.mountainlog.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.mountainlog.event.SignupEventPublisher;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final SignupEventPublisher signupEventPublisher;

    public AuthController(UserService userService, VerificationTokenService verificationTokenService,
            SignupEventPublisher signupEventPublisher) {
        this.userService = userService;
        this.verificationTokenService = verificationTokenService;
        this.signupEventPublisher = signupEventPublisher;
    }

    // 登録画面の表示
    @GetMapping("/register")
    public String registerForm(Model model) {
        // 初回表示用に空のフォームをセット
        model.addAttribute("userRegistrationForm", new UserRegistrationForm());
        return "auth/register";
    }

    // 登録処理
    @PostMapping("/register")
    public String register(@Validated @ModelAttribute UserRegistrationForm form,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) { // URL生成用にRequestを受け取る

        // バリデーションエラー（入力値がNullや空など）の場合
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "入力内容に不備があります。");
            return "auth/register";
        }

        // 重複チェック
        if (userService.findUserByUsername(form.getUsername()).isPresent()) {
            result.rejectValue("username", "error.username", "このユーザー名は既に使用されています。");
        }
        if (userService.findUserByEmail(form.getEmail()).isPresent()) {
            result.rejectValue("email", "error.email", "このメールアドレスは既に使用されています。");
        }

        // パスワード一致チェック
        if (!form.getPassword().equals(form.getPasswordConfirmation())) {
            result.rejectValue("passwordConfirmation", "error.passwordConfirmation", "パスワードと確認用パスワードが一致しません。");
        }

        // 重複エラーや不整合があればフォームに戻す
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "入力内容を確認してください。");
            return "auth/register";
        }

        try {
            // FormからEntityへの変換
            User user = new User();
            user.setUsername(form.getUsername());
            user.setEmail(form.getEmail());
            user.setAddress(form.getAddress());
            if (form.getBirthDate() != null) {
                user.setBirthDate(java.sql.Date.valueOf(form.getBirthDate()));
            }
            user.setExperienceLevel(form.getExperienceLevel());

            // サービスを使って保存（Userが返ってくるように変更済み）
            User savedUser = userService.registerUser(user, form.getPassword());

            // イベント発行
            String appUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
            signupEventPublisher.publishSignupEvent(savedUser, appUrl);

            // 成功メッセージをRediredctAttributesにセット
            redirectAttributes.addFlashAttribute("successMessage", "会員登録が完了しました。メールを確認して認証を行ってください。");

            // ログイン画面へリダイレクト
            return "redirect:/auth/login";

        } catch (Exception e) {
            // その他エラー（メールアドレス重複など）の場合
            model.addAttribute("errorMessage", "登録中にエラーが発生しました。");
            return "auth/register";
        }
    }

    // ログイン画面の表示
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // メール認証処理
    @GetMapping("/verify")
    public String verifyUser(@RequestParam("token") String token, Model model) {
        VerificationToken verificationToken = verificationTokenService.validateToken(token);

        if (verificationToken == null) {
            model.addAttribute("errorMessage", "無効なトークンまたは期限切れです。");
            return "auth/login"; // ログイン画面にエラー表示するようにしても良い
        }

        model.addAttribute("successMessage", "メール認証が完了しました。ログインしてください。");
        return "auth/login";
    }
}
