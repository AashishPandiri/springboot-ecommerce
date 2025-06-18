package com.backend.ecommerce.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class WebSecurityConfig {

    private JWTRequestFilter jwtRequestFilter;

    public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf().disable().cors().disable();
        httpSecurity.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);
        httpSecurity.authorizeHttpRequests()
                .requestMatchers("/product", "/auth/register", "/auth/login", "/auth/verify",
                        "/auth/forgot", "/auth/reset", "/user/", "/error", "/websocket", "/websocket/**").permitAll()
                .anyRequest().authenticated();
        return httpSecurity.build();
    }
}
