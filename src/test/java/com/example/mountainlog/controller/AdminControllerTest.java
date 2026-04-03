package com.example.mountainlog.controller;

import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.Role;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.form.MountainForm;
import com.example.mountainlog.service.AdminService;
import com.example.mountainlog.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    private Mountain testMountain;
    private User testUser;
    private UserDetailsImpl adminUserDetails;

    @BeforeEach
    void setUp() {
        testMountain = new Mountain();
        testMountain.setMountainId(1);
        testMountain.setName("テスト山");

        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");

        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("AdminUser");
        testUser.setRole(adminRole);

        adminUserDetails = new UserDetailsImpl(testUser,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void testDashboard_Success() throws Exception {
        when(adminService.getTotalUsers()).thenReturn(100L);
        when(adminService.getPremiumUsers()).thenReturn(20L);
        when(adminService.getTotalMountains()).thenReturn(50L);
        when(adminService.getRecentActivities()).thenReturn(15L);
        when(adminService.getSalesForecastData()).thenReturn(new HashMap<>());

        mockMvc.perform(get("/admin/dashboard").with(user(adminUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("totalUsers", "premiumUsers", "totalMountains", "recentActivities",
                        "salesData"));
    }

    @Test
    void testUserList_Success() throws Exception {
        Page<User> userPage = new PageImpl<>(Collections.singletonList(testUser));
        when(adminService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/admin/users").with(user(adminUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/index"))
                .andExpect(model().attributeExists("userPage"));
    }

    @Test
    void testMountainList_Success() throws Exception {
        Page<Mountain> mountainPage = new PageImpl<>(Collections.singletonList(testMountain));
        when(adminService.getAllMountains(any(Pageable.class))).thenReturn(mountainPage);

        mockMvc.perform(get("/admin/mountains").with(user(adminUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/mountains/index"))
                .andExpect(model().attributeExists("mountainPage"));
    }

    @Test
    void testNewMountain_Success() throws Exception {
        mockMvc.perform(get("/admin/mountains/new").with(user(adminUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/mountains/form"))
                .andExpect(model().attributeExists("mountainForm"));
    }

    @Test
    void testCreateMountain_Success() throws Exception {
        doNothing().when(adminService).saveMountainFromForm(any(MountainForm.class));

        mockMvc.perform(post("/admin/mountains/create")
                .with(user(adminUserDetails))
                .with(csrf())
                .param("name", "新規山")
                .param("elevation", "1000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/mountains"))
                .andExpect(flash().attribute("successMessage", "山データを新規登録しました。"));
    }

    @Test
    void testCreateMountain_ValidationFail() throws Exception {
        mockMvc.perform(post("/admin/mountains/create")
                .with(user(adminUserDetails))
                .with(csrf())
                // Missing required name
                .param("elevation", "1000"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/mountains/form"));
    }

    @Test
    void testEditMountain_Success() throws Exception {
        when(adminService.getMountainById(1)).thenReturn(Optional.of(testMountain));

        mockMvc.perform(get("/admin/mountains/1/edit").with(user(adminUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/mountains/form"))
                .andExpect(model().attributeExists("mountainForm"));
    }

    @Test
    void testEditMountain_NotFound() throws Exception {
        when(adminService.getMountainById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/mountains/999/edit").with(user(adminUserDetails)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/mountains"))
                .andExpect(flash().attribute("errorMessage", "指定された山が見つかりませんでした。"));
    }

    @Test
    void testUpdateMountain_Success() throws Exception {
        doNothing().when(adminService).saveMountainFromForm(any(MountainForm.class));

        mockMvc.perform(post("/admin/mountains/1/update")
                .with(user(adminUserDetails))
                .with(csrf())
                .param("mountainId", "1")
                .param("name", "更新山")
                .param("elevation", "2000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/mountains"))
                .andExpect(flash().attribute("successMessage", "山データを更新しました。"));
    }

    @Test
    void testDeleteMountain_Success() throws Exception {
        doNothing().when(adminService).deleteMountain(1);

        mockMvc.perform(post("/admin/mountains/1/delete")
                .with(user(adminUserDetails))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/mountains"))
                .andExpect(flash().attribute("successMessage", "山データを削除しました。"));
    }

    @Test
    void testNewUser_Success() throws Exception {
        when(adminService.getAllRoles()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/admin/users/new").with(user(adminUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/form"))
                .andExpect(model().attributeExists("adminUserForm", "roles"));
    }

    @Test
    void testCreateUser_Success() throws Exception {
        doNothing().when(adminService).saveUserFromAdmin(any(com.example.mountainlog.form.AdminUserForm.class));
        when(adminService.getAllRoles()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/users/create")
                .with(user(adminUserDetails))
                .with(csrf())
                .param("username", "新規ユーザー")
                .param("email", "newuser@example.com")
                .param("password", "password123")
                .param("roleId", "1")
                .param("enabled", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("successMessage", "ユーザーを新規登録しました。"));
    }

    @Test
    void testCreateUser_ValidationFail() throws Exception {
        when(adminService.getAllRoles()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/users/create")
                .with(user(adminUserDetails))
                .with(csrf())
                // Missing required password for create
                .param("username", "新規ユーザー")
                .param("email", "newuser@example.com")
                .param("roleId", "1")
                .param("enabled", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/form"));
    }

    @Test
    void testEditUser_Success() throws Exception {
        when(adminService.getUserById(1)).thenReturn(Optional.of(testUser));
        when(adminService.getAllRoles()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/users/1/edit").with(user(adminUserDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users/form"))
                .andExpect(model().attributeExists("adminUserForm", "roles"));
    }

    @Test
    void testEditUser_NotFound() throws Exception {
        when(adminService.getUserById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/admin/users/999/edit").with(user(adminUserDetails)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("errorMessage", "指定されたユーザーが見つかりませんでした。"));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        doNothing().when(adminService).saveUserFromAdmin(any());
        when(adminService.getAllRoles()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/admin/users/1/update")
                .with(user(adminUserDetails))
                .with(csrf())
                .param("userId", "1")
                .param("username", "更新ユーザー")
                .param("email", "update@example.com")
                // Password can be empty for update
                .param("roleId", "1")
                .param("enabled", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("successMessage", "ユーザー情報を更新しました。"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(adminService).deleteUser(1);

        mockMvc.perform(post("/admin/users/1/delete")
                .with(user(adminUserDetails))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attribute("successMessage", "ユーザーを削除しました。"));
    }

    // Role test
    @Test
    @WithMockUser(username = "user@example.com", roles = "GENERAL")
    void testAdminAccess_ForbiddenForGeneralUser() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }
}
