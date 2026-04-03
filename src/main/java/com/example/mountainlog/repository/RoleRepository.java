package com.example.mountainlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.mountainlog.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
