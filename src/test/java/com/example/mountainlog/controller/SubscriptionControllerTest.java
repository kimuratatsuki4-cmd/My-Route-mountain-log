package com.example.mountainlog.controller;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.example.mountainlog.entity.Role;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.security.UserDetailsImpl;
import com.example.mountainlog.service.SubscriptionService;
import com.example.mountainlog.service.UserService;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("null")
public class SubscriptionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private SubscriptionService subscriptionService;

        @MockBean
        private UserService userService;

        private User testUser;
        private UserDetailsImpl userDetails;

        @BeforeEach
        void setUp() {
                Role role = new Role();
                role.setName("ROLE_PREMIUM");

                testUser = new User();
                testUser.setUserId(1);
                testUser.setUsername("Premium User");
                testUser.setEmail("premium@example.com");
                testUser.setRole(role);
                testUser.setStripeId("cus_test123");

                userDetails = new UserDetailsImpl(testUser,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_PREMIUM")));

                when(userService.findUserByEmail("premium@example.com")).thenReturn(Optional.of(testUser));
        }

        @Test
        void create_createsCustomerAndSessionAndRedirects() throws Exception {
                // モックの一般ユーザーを準備
                Role generalRole = new Role();
                generalRole.setName("ROLE_GENERAL");
                User generalUser = new User();
                generalUser.setEmail("general@example.com");
                generalUser.setRole(generalRole);
                // 初期状態ではStripe IDを持たない
                UserDetailsImpl generalUserDetails = new UserDetailsImpl(generalUser,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_GENERAL")));

                when(userService.findUserByEmail("general@example.com")).thenReturn(Optional.of(generalUser));

                Customer mockCustomer = mock(Customer.class);
                when(mockCustomer.getId()).thenReturn("cus_new456");
                when(subscriptionService.createUser(any(User.class))).thenReturn(mockCustomer);

                Session mockSession = mock(Session.class);
                when(mockSession.getUrl()).thenReturn("https://checkout.stripe.test/c/pay/cs_test_123");
                when(subscriptionService.createStripeSession(any(), anyString(), anyString(), anyString()))
                                .thenReturn(mockSession);

                mockMvc.perform(post("/subscription/create")
                                .with(user(generalUserDetails))
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("https://checkout.stripe.test/c/pay/cs_test_123"));

                verify(subscriptionService, times(1)).createUser(any(User.class));
                verify(userService, times(1)).saveStripeId("general@example.com", "cus_new456");
        }

        @Test
        void update_updatesPaymentMethodsAndRedirects() throws Exception {
                when(subscriptionService.getDefaultPaymentMethodId("cus_test123")).thenReturn("pm_old_123");

                mockMvc.perform(post("/subscription/update")
                                .param("paymentMethodId", "pm_new_456")
                                .with(user(userDetails))
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/"));

                verify(subscriptionService, times(1)).attachPaymentMethodToCustomer("pm_new_456", "cus_test123");
                verify(subscriptionService, times(1)).setDefaultPaymentMethod("pm_new_456", "cus_test123");
                verify(subscriptionService, times(1)).updateSubscriptionPaymentMethod("cus_test123", "pm_new_456");
                verify(subscriptionService, times(1)).detachPaymentMethodFromCustomer("pm_old_123");
        }

        @Test
        void delete_cancelsSubscriptionAndRedirects() throws Exception {
                when(subscriptionService.getDefaultPaymentMethodId("cus_test123")).thenReturn("pm_test_123");

                mockMvc.perform(post("/subscription/delete")
                                .with(user(userDetails))
                                .with(csrf()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/"));

                verify(subscriptionService, times(1)).getSubscriptions("cus_test123");
                verify(subscriptionService, times(1)).cancelSubscriptions(any());
                verify(subscriptionService, times(1)).detachPaymentMethodFromCustomer("pm_test_123");
                verify(userService, times(1)).updateRole("premium@example.com", "ROLE_GENERAL");
        }
}
