package com.example.mountainlog.service;

import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.Role;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.form.AdminUserForm;
import com.example.mountainlog.form.MountainForm;
import com.example.mountainlog.repository.ActivityRepository;
import com.example.mountainlog.repository.MountainRepository;
import com.example.mountainlog.repository.RoleRepository;
import com.example.mountainlog.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MountainRepository mountainRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    private Mountain testMountain;

    @BeforeEach
    void setUp() {
        testMountain = new Mountain();
        testMountain.setMountainId(1);
        testMountain.setName("テスト山");
        testMountain.setElevation(1000);
        testMountain.setDifficulty("EASY");
    }

    @Test
    void testGetTotalUsers() {
        // Arrange
        when(userRepository.count()).thenReturn(100L);

        // Act
        long totalUsers = adminService.getTotalUsers();

        // Assert
        assertThat(totalUsers).isEqualTo(100L);
        verify(userRepository, times(1)).count();
    }

    @Test
    void testGetPremiumUsers() {
        // Arrange
        when(userRepository.countByRole_Name("ROLE_PREMIUM")).thenReturn(20L);

        // Act
        long premiumUsers = adminService.getPremiumUsers();

        // Assert
        assertThat(premiumUsers).isEqualTo(20L);
        verify(userRepository, times(1)).countByRole_Name("ROLE_PREMIUM");
    }

    @Test
    void testGetTotalMountains() {
        // Arrange
        when(mountainRepository.count()).thenReturn(50L);

        // Act
        long totalMountains = adminService.getTotalMountains();

        // Assert
        assertThat(totalMountains).isEqualTo(50L);
        verify(mountainRepository, times(1)).count();
    }

    @Test
    void testGetRecentActivities() {
        // Arrange
        when(activityRepository.countByActivityDateAfter(any(Date.class))).thenReturn(15L);

        // Act
        long recentActivities = adminService.getRecentActivities();

        // Assert
        assertThat(recentActivities).isEqualTo(15L);
        verify(activityRepository, times(1)).countByActivityDateAfter(any(Date.class));
    }

    @Test
    void testGetAllMountains() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Mountain> mountainPage = new PageImpl<>(Collections.singletonList(testMountain));
        when(mountainRepository.findAll(pageable)).thenReturn(mountainPage);

        // Act
        Page<Mountain> result = adminService.getAllMountains(pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("テスト山");
        verify(mountainRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetMountainById() {
        // Arrange
        when(mountainRepository.findById(1)).thenReturn(Optional.of(testMountain));

        // Act
        Optional<Mountain> result = adminService.getMountainById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("テスト山");
        verify(mountainRepository, times(1)).findById(1);
    }

    @Test
    void testGetMountainById_NotFound() {
        // Arrange
        when(mountainRepository.findById(999)).thenReturn(Optional.empty());

        // Act
        Optional<Mountain> result = adminService.getMountainById(999);

        // Assert
        assertThat(result).isNotPresent();
        verify(mountainRepository, times(1)).findById(999);
    }

    @Test
    void testSaveMountainFromForm_New() {
        // Arrange
        MountainForm form = new MountainForm();
        form.setName("新規山");
        form.setElevation(2000);

        when(mountainRepository.save(any(Mountain.class))).thenAnswer(invocation -> {
            Mountain saved = invocation.getArgument(0);
            saved.setMountainId(2);
            return saved;
        });

        // Act
        adminService.saveMountainFromForm(form);

        // Assert
        // verify that finding by id is skipped when form id is null
        verify(mountainRepository, never()).findById(any());
        // verify save is called once
        verify(mountainRepository, times(1)).save(any(Mountain.class));
    }

    @Test
    void testSaveMountainFromForm_Update() {
        // Arrange
        MountainForm form = new MountainForm();
        form.setMountainId(1);
        form.setName("更新山");

        when(mountainRepository.findById(1)).thenReturn(Optional.of(testMountain));
        when(mountainRepository.save(any(Mountain.class))).thenReturn(testMountain);

        // Act
        adminService.saveMountainFromForm(form);

        // Assert
        verify(mountainRepository, times(1)).findById(1);
        verify(mountainRepository, times(1)).save(any(Mountain.class));
        // testMountain self should be updated
        assertThat(testMountain.getName()).isEqualTo("更新山");
    }

    @Test
    void testDeleteMountain() {
        // Arrange & Act
        adminService.deleteMountain(1);

        // Assert
        verify(mountainRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        User testUser = new User();
        testUser.setUsername("テストユーザー");
        Page<User> userPage = new PageImpl<>(Collections.singletonList(testUser));
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // Act
        Page<User> result = adminService.getAllUsers(pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("テストユーザー");
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetUserById() {
        User user = new User();
        user.setUserId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        Optional<User> result = adminService.getUserById(1);

        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(1);
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    void testGetAllRoles() {
        Role role = new Role();
        role.setName("ROLE_GENERAL");
        when(roleRepository.findAll()).thenReturn(Collections.singletonList(role));

        List<Role> result = adminService.getAllRoles();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("ROLE_GENERAL");
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testSaveUserFromAdmin_New() {
        AdminUserForm form = new AdminUserForm();
        form.setUsername("新規ユーザー");
        form.setEmail("new@example.com");
        form.setRoleId(1);
        form.setPassword("password123");
        form.setEnabled(true);

        Role role = new Role();
        role.setId(1);

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("encoded_pass");

        adminService.saveUserFromAdmin(form);

        verify(userRepository, never()).findById(any());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    void testSaveUserFromAdmin_Update_NoPassword() {
        AdminUserForm form = new AdminUserForm();
        form.setUserId(1);
        form.setUsername("更新ユーザー");
        form.setRoleId(1);
        form.setPassword("");
        form.setEnabled(true);

        User existingUser = new User();
        existingUser.setUserId(1);
        existingUser.setPassword("old_encoded_pass");

        Role role = new Role();
        role.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        adminService.saveUserFromAdmin(form);

        verify(userRepository, times(1)).findById(1);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(existingUser.getPassword()).isEqualTo("old_encoded_pass");
    }

    @Test
    void testDeleteUser() {
        adminService.deleteUser(1);
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void testGetSalesForecastData() {
        // Act
        java.util.Map<String, Object> result = adminService.getSalesForecastData();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.get("labels")).isInstanceOf(java.util.List.class);
        assertThat(result.get("actual")).isInstanceOf(java.util.List.class);
        assertThat(result.get("forecast")).isInstanceOf(java.util.List.class);
    }
}
