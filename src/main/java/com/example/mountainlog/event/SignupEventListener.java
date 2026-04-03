package com.example.mountainlog.event;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.mountainlog.entity.User;
import com.example.mountainlog.service.VerificationTokenService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SignupEventListener {

    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender javaMailSender;

    @EventListener
    public void onSignupEvent(SignupEvent event) {
        User user = event.getUser();
        String appUrl = event.getAppUrl();

        // トークン生成と保存
        String token = verificationTokenService.generateToken();
        verificationTokenService.create(user, token);

        // 認証用URLの生成
        String verifyUrl = appUrl + "/auth/verify?token=" + token;

        // メール送信処理
        String subject = "会員登録認証";
        String message = "以下のリンクをクリックして会員登録を完了させてください。";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setText(message + "\n" + verifyUrl);
        javaMailSender.send(mailMessage);

        // コンソールにも出力（デバッグ用）
        System.out.println("========================================");
        System.out.println("Verification Email for: " + user.getEmail());
        System.out.println("Click to Verify: " + verifyUrl);
        System.out.println("========================================");
    }
}
