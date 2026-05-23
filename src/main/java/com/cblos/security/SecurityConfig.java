package com.cblos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/approvals/**").hasAnyRole("OFFICER", "MANAGER")
                        .requestMatchers("/api/credit/**").hasAnyRole("OFFICER", "MANAGER")
                        .requestMatchers("/api/officers/**").hasAnyRole("OFFICER", "MANAGER")
                        .requestMatchers("/api/disbursement/**").hasAnyRole("OFFICER", "MANAGER")
                        .requestMatchers("/api/repayments/**").hasAnyRole("OFFICER", "MANAGER")
                        .requestMatchers(HttpMethod.POST, "/api/customers/onboard").hasAnyRole("OFFICER", "MANAGER")
                        .requestMatchers("/api/customers/all").hasAnyRole("OFFICER", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/documents/validate/**").hasAnyRole("OFFICER", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/documents/validate-all/**").hasAnyRole("OFFICER", "MANAGER")
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
