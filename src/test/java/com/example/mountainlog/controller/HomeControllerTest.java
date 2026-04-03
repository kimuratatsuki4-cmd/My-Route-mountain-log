package com.example.mountainlog.controller;

import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.entity.Role;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.service.MountainService;
import com.example.mountainlog.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.context.ActiveProfiles;

import com.example.mountainlog.security.UserDetailsImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
public class HomeControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private MountainService mountainService;

        @MockBean
        private UserService userService;

        @MockBean
        private com.example.mountainlog.service.ActivityService activityService;

        private User premiumUser;
        private User generalUser;

        @BeforeEach
        void setUp() {
                Role premiumRole = new Role();
                premiumRole.setName("ROLE_PREMIUM");

                Role generalRole = new Role();
                generalRole.setName("ROLE_GENERAL");

                premiumUser = new User();
                premiumUser.setUsername("premiumUser");
                premiumUser.setEmail("premium@example.com");
                premiumUser.setRole(premiumRole);
                premiumUser.setPassword("pass");
                premiumUser.setEnabled(true);

                generalUser = new User();
                generalUser.setUsername("generalUser");
                generalUser.setEmail("general@example.com");
                generalUser.setRole(generalRole);
                generalUser.setPassword("pass");
                generalUser.setEnabled(true);
        }

        @Test
        void testIndexUnauthenticated() throws Exception {
                mockMvc.perform(get("/"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("index"))
                                .andExpect(model().attributeDoesNotExist("recommendedMountains"));
        }

        @Test
        void testIndexGeneralUser() throws Exception {
                UserDetailsImpl generalDetails = new UserDetailsImpl(generalUser,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_GENERAL")));
                when(userService.findUserByEmail("general@example.com")).thenReturn(Optional.of(generalUser));

                mockMvc.perform(get("/").with(user(generalDetails)))
                                .andExpect(status().isOk())
                                .andExpect(view().name("index"))
                                .andExpect(model().attributeDoesNotExist("recommendedMountains"));
        }

        @Test
        void testIndexPremiumUser() throws Exception {
                Mountain mockMountain = new Mountain();
                mockMountain.setMountainId(1);
                mockMountain.setName("おすすめの山");

                UserDetailsImpl premiumDetails = new UserDetailsImpl(premiumUser,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PREMIUM")));

                when(userService.findUserByEmail("premium@example.com")).thenReturn(Optional.of(premiumUser));
                when(mountainService.getRecommendedMountains(any(User.class)))
                                .thenReturn(Collections.singletonList(mockMountain));
                                
                com.example.mountainlog.dto.ActivityStatsDto mockStats = new com.example.mountainlog.dto.ActivityStatsDto(10, 100.0, 5000, 600);
                when(activityService.getActivityStats(any(User.class))).thenReturn(mockStats);

                mockMvc.perform(get("/").with(user(premiumDetails)))
                                .andExpect(status().isOk())
                                .andExpect(view().name("index"))
                                .andExpect(model().attributeExists("recommendedMountains"))
                                .andExpect(model().attributeExists("randomActivities"));
        }
}
