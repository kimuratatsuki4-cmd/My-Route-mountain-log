package com.example.mountainlog.service;

import java.util.UUID;
import java.util.Calendar;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mountainlog.entity.User;
import com.example.mountainlog.entity.VerificationToken;
import com.example.mountainlog.repository.VerificationTokenRepository;
import com.example.mountainlog.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// メール認証トークンの管理を行うサービス
@Service
@RequiredArgsConstructor
public class VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    // トークンの有効期限（例：24時間）
    private static final int EXPIRATION_MINUTES = 60;

    // ユーザーとトークンを紐付けて保存
    @Transactional
    public void create(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        tokenRepository.save(verificationToken);
    }

    // トークン文字列を生成
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    // トークンを検証し、有効であればユーザーを有効化する
    @Transactional
    public VerificationToken validateToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null) {
            return null; // トークンが存在しない
        }

        User user = verificationToken.getUser();

        // 有効期限のチェック（作成日時 + 24時間 < 現在日時 なら期限切れ）
        Calendar cal = Calendar.getInstance();
        cal.setTime(verificationToken.getCreatedAt());
        cal.add(Calendar.MINUTE, EXPIRATION_MINUTES);
        if (cal.getTime().before(new java.util.Date())) {
            // 期限切れの場合の処理（トークン削除など）も検討できますが、
            // 今回は単純にnullを返すか、エラーとして扱います
            return null;
        }

        // ユーザーを有効化
        user.setEnabled(true);
        userRepository.save(user);

        // 検証完了後にトークンを削除する場合
        // tokenRepository.delete(verificationToken);

        return verificationToken;
    }
}
