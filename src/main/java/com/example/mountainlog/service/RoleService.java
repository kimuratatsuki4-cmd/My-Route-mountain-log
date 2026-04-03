package com.example.mountainlog.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mountainlog.entity.Role;
import com.example.mountainlog.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

// ロール情報の管理を行うサービスクラス
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    // 全てのロールを取得する
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Role名からRoleを取得する (初期データ登録時などに使用)
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
