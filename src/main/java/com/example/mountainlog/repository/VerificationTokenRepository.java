package com.example.mountainlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
