package com.example.mountainlog.repository;

import com.example.mountainlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // ログイン時などに使用
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    // ロールごとのユーザー数を取得
    long countByRole_Name(String roleName);
}
