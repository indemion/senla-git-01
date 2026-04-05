package ru.indemion.carservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.indemion.carservice.security.CustomAccessDeniedHandler;
import ru.indemion.carservice.security.CustomAuthenticationEntryPoint;
import ru.indemion.carservice.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/health", "/auth/token").permitAll()
                        .requestMatchers(HttpMethod.GET, "/orders").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/orders/*/start").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/orders/*/close").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/orders/*/cancel").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/orders/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/masters").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/masters/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/garage-spots").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/garage-spots/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
