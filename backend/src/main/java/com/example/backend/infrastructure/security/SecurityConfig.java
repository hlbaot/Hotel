package com.example.backend.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Cấu hình bảo mật hệ thống: Phân quyền theo URL và vai trò người dùng (ADMIN, CUSTOMER).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Các API xác thực (đăng nhập, đăng ký): Cho phép tất cả
                        .requestMatchers("/api/auth/**").permitAll()

                        // 2. Xem danh sách và chi tiết phòng trọ: Ai cũng xem được
                        .requestMatchers(HttpMethod.GET, "/api/rooms/**").permitAll()

                        // 3. Thêm, sửa, xóa phòng trọ: Chỉ ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/rooms/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/rooms/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/rooms/**").hasRole("ADMIN")

                        // 4. API thống kê báo cáo: Chỉ ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // 5. API thông tin cá nhân: Cần đăng nhập
                        .requestMatchers("/api/users/me").authenticated()

                        // 6. Quản lý người dùng khác: Chỉ ADMIN
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // 7. Các API còn lại (hợp đồng, hóa đơn, yêu cầu thuê): Phải đăng nhập
                        .anyRequest().authenticated()
                );

        // Đặt JWT filter phía trước UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
