package com.hodos.hermes.config;

import com.hodos.hermes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                // Allow unrestricted access to the following endpoints
                .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/auth/send-otp",
                                "/api/v1/auth/verify-otp", "/api/v1/auth/request-token",
                                "/api/v1/auth/**")
                        .permitAll())
                // Restrict access to /api/v1/admin to users with the ROLE_ADMIN authority
                .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/admin")
                        .hasAnyAuthority("ROLE_ADMIN"))
                // Restrict access to /api/v1/user/** to users with the ROLE_USER authority
                .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/user/u")
                        .hasAnyAuthority("ROLE_USER"))
                // Apply the authentication to any other reque st (i.e., default rule)
                .authorizeHttpRequests(request -> request.anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
