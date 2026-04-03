package com.example.mountainlog.service;

import com.example.mountainlog.entity.Role;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.form.UserEditForm;
import com.example.mountainlog.repository.RoleRepository;
import com.example.mountainlog.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;
import java.util.ArrayList;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // コンストラクタインジェクション（推奨されるDIの方法）
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * @param user        登録するユーザー情報
     * @param rawPassword 平文のパスワード
     */
    @Transactional
    public User registerUser(User user, String rawPassword) {
        // Optionalを使ってNullチェック
        User userToSave = Optional.ofNullable(user)
                .orElseThrow(() -> new IllegalArgumentException("User must not be null"));
        String passwordToEncode = Optional.ofNullable(rawPassword)
                .orElseThrow(() -> new IllegalArgumentException("Password must not be null"));

        // パスワードをBCryptでハッシュ化（暗号化）
        String encodedPassword = passwordEncoder.encode(passwordToEncode);
        userToSave.setPassword(encodedPassword);

        // デフォルトロール(ROLE_GENERAL)を設定
        Role role = roleRepository.findByName("ROLE_GENERAL");
        if (role == null) {
            throw new IllegalStateException("ROLE_GENERAL not found in database.");
        }
        userToSave.setRole(role);

        // 有効フラグ設定（メール認証後にtureにするため、ここではfalse）
        userToSave.setEnabled(false);

        // データベースに保存
        return userRepository.save(userToSave);
    }

    /**
     * メールアドレスからユーザーを検索
     * 
     * @param email メールアドレス
     * @return ユーザー（存在しない場合はEmpty）
     */
    public Optional<User> findUserByEmail(String email) {
        return Optional.ofNullable(email)
                .flatMap(userRepository::findByEmail);
    }

    /**
     * IDからユーザーを検索
     * 
     * @param id ユーザーID
     * @return ユーザー（存在しない場合はEmpty）
     */
    public Optional<User> findUserById(Integer id) {
        return Optional.ofNullable(id)
                .flatMap(userRepository::findById);
    }

    /**
     * ユーザー名からユーザーを検索
     * 
     * @param username ユーザー名
     * @return ユーザー（存在しない場合はEmpty）
     */
    public Optional<User> findUserByUsername(String username) {
        return Optional.ofNullable(username)
                .flatMap(userRepository::findByUsername);
    }

    /**
     * ユーザー情報を更新します（プロフィール編集）。
     * 
     * @param user 更新対象のユーザー（Entity）
     * @param form 更新内容を持つフォーム
     */
    @Transactional
    public void updateUser(User user, UserEditForm form) {
        if (form.getUsername() != null && !form.getUsername().isBlank()) {
            user.setUsername(form.getUsername());
        }

        user.setAddress(form.getAddress());

        if (form.getBirthDate() != null) {
            user.setBirthDate(java.sql.Date.valueOf(form.getBirthDate()));
        } else {
            user.setBirthDate(null);
        }

        user.setExperienceLevel(form.getExperienceLevel());

        userRepository.save(user);
    }

    @Transactional
    public void saveStripeId(String email, String stripeId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setStripeId(stripeId);
        userRepository.save(user);
    }

    @Transactional
    public void updateRole(String email, String roleName) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            throw new IllegalStateException("Role not found: " + roleName);
        }
        user.setRole(role);
        userRepository.save(user); // Make sure user object is persisted. Wait, findByEmail returns user.
    }

    /**
     * @param newRoleName 新しいロール名（例: "ROLE_PREMIUM"）
     */
    public void refreshAuthenticationByRole(String newRoleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        List<GrantedAuthority> newAuthorities = new ArrayList<>();
        newAuthorities.add(new SimpleGrantedAuthority(newRoleName));

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                auth.getPrincipal(),
                auth.getCredentials(),
                newAuthorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
