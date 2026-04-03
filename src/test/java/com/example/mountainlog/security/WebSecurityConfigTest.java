package com.example.mountainlog.security;

import com.example.mountainlog.entity.Role;
import com.example.mountainlog.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
public class WebSecurityConfigTest {

        @Autowired
        private MockMvc mockMvc;

        private UserDetailsImpl generalDetails;

        @BeforeEach
        void setUp() {
                User generalUser = new User();
                generalUser.setEmail("general@example.com");
                generalUser.setPassword("pass");
                generalUser.setEnabled(true);
                Role generalRole = new Role();
                generalRole.setName("ROLE_GENERAL");
                generalUser.setRole(generalRole);
                generalDetails = new UserDetailsImpl(generalUser,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_GENERAL")));

        }

        @Test
        void testPublicEndpoints_Unauthenticated_Allowed() throws Exception {
                mockMvc.perform(get("/"))
                                .andExpect(status().isOk());

                mockMvc.perform(get("/auth/login"))
                                .andExpect(status().isOk());
        }

        @Test
        void testProtectedEndpoints_Unauthenticated_Redirected() throws Exception {
                mockMvc.perform(get("/admin/dashboard"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrlPattern("**/auth/login"));

                mockMvc.perform(get("/premium/gear-guide"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrlPattern("**/auth/login"));
        }

        @Test
        void testAdminEndpoints_GeneralUser_Forbidden() throws Exception {
                mockMvc.perform(get("/admin/dashboard").with(user(generalDetails)))
                                .andExpect(status().isForbidden());
        }

        @Test
        void testPremiumEndpoints_GeneralUser_Forbidden() throws Exception {
                mockMvc.perform(get("/premium/gear-guide").with(user(generalDetails)))
                                .andExpect(status().isForbidden());
        }
}
