package com.example.mountainlog.service;

import com.example.mountainlog.entity.ExperienceLevel;
import com.example.mountainlog.entity.Role;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.form.UserEditForm;
import com.example.mountainlog.repository.RoleRepository;
import com.example.mountainlog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role generalRole;
    private Role premiumRole;

    @BeforeEach
    void setUp() {
        generalRole = new Role();
        generalRole.setId(1);
        generalRole.setName("ROLE_GENERAL");

        premiumRole = new Role();
        premiumRole.setId(2);
        premiumRole.setName("ROLE_PREMIUM");

        testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(generalRole);
        testUser.setEnabled(true);
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");

        when(passwordEncoder.encode("rawPass")).thenReturn("encodedPass");
        when(roleRepository.findByName("ROLE_GENERAL")).thenReturn(generalRole);
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User saved = i.getArgument(0);
            saved.setUserId(2);
            return saved;
        });

        // Act
        User result = userService.registerUser(newUser, "rawPass");

        // Assert
        assertThat(result.getUserId()).isEqualTo(2);
        assertThat(result.getPassword()).isEqualTo("encodedPass");
        assertThat(result.getRole().getName()).isEqualTo("ROLE_GENERAL");
        assertThat(result.getEnabled()).isFalse(); // Should be false by default

        verify(passwordEncoder, times(1)).encode("rawPass");
        verify(roleRepository, times(1)).findByName("ROLE_GENERAL");
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void testRegisterUser_NullPassword_ThrowsException() {
        User newUser = new User();

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(newUser, null);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void testFindUserByEmail() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.findUserByEmail("test@example.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void testFindUserById() {
        // Arrange
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        // Act
        Optional<User> result = userService.findUserById(1);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testUpdateUser() {
        // Arrange
        UserEditForm form = new UserEditForm();
        form.setUsername("updatedUsername");
        form.setAddress("Tokyo");
        form.setBirthDate(LocalDate.of(1990, 1, 1));
        form.setExperienceLevel(ExperienceLevel.INTERMEDIATE);

        // Act
        userService.updateUser(testUser, form);

        // Assert
        assertThat(testUser.getUsername()).isEqualTo("updatedUsername");
        assertThat(testUser.getAddress()).isEqualTo("Tokyo");
        assertThat(testUser.getBirthDate().toString()).isEqualTo("1990-01-01");
        assertThat(testUser.getExperienceLevel()).isEqualTo(ExperienceLevel.INTERMEDIATE);

        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testSaveStripeId() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act
        userService.saveStripeId("test@example.com", "cus_12345");

        // Assert
        assertThat(testUser.getStripeId()).isEqualTo("cus_12345");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testUpdateRole_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("ROLE_PREMIUM")).thenReturn(premiumRole);

        // Act
        userService.updateRole("test@example.com", "ROLE_PREMIUM");

        // Assert
        assertThat(testUser.getRole().getName()).isEqualTo("ROLE_PREMIUM");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testRefreshAuthenticationByRole() {
        // Arrange
        List<GrantedAuthority> initialAuthorities = new ArrayList<>();
        initialAuthorities.add(new SimpleGrantedAuthority("ROLE_GENERAL"));
        Authentication initialAuth = new UsernamePasswordAuthenticationToken(
                "principal", "credentials", initialAuthorities);

        SecurityContextHolder.getContext().setAuthentication(initialAuth);

        // Act
        userService.refreshAuthenticationByRole("ROLE_PREMIUM");

        // Assert
        Authentication updatedAuth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(updatedAuth.getAuthorities()).hasSize(1);
        assertThat(updatedAuth.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_PREMIUM");

        // Cleanup explicitly
        SecurityContextHolder.clearContext();
    }
}
