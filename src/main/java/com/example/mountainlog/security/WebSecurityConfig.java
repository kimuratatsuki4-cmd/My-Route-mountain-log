package com.example.mountainlog.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests((requests) -> requests
                                                .requestMatchers("/", "/css/**", "/js/**", "/images/**",
                                                                "/h2-console/**", "/auth/**")
                                                .permitAll()
                                                .requestMatchers("/subscription/register", "/subscription/create")
                                                .authenticated()
                                                .requestMatchers("/admin/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers("/subscription/edit", "/subscription/update",
                                                                "/subscription/cancel",
                                                                "/subscription/delete", "/premium/**")
                                                .hasRole("PREMIUM")
                                                .anyRequest().authenticated())
                                .csrf((csrf) -> csrf.ignoringRequestMatchers("/h2-console/**"))
                                .headers((headers) -> headers.frameOptions((frame) -> frame.sameOrigin()))
                                .formLogin((form) -> form
                                                .loginPage("/auth/login")
                                                .loginProcessingUrl("/auth/login")
                                                .defaultSuccessUrl("/", true)
                                                .failureUrl("/auth/login?error")
                                                .permitAll())
                                .logout((logout) -> logout.logoutSuccessUrl("/").permitAll());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
