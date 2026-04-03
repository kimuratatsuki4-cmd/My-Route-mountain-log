package com.example.mountainlog.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mountainlog.entity.Role;
import com.example.mountainlog.service.RoleService;

import lombok.RequiredArgsConstructor;

// ロール情報の取得を行うコントローラー
// 主に管理者機能などでロール一覧を表示する際に利用されることを想定
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * ロール一覧を取得するAPI
     * 
     * @return ロールのリスト
     */
    @GetMapping
    public List<Role> getAllRoles() {
        return roleService.getAllRoles();
    }
}
