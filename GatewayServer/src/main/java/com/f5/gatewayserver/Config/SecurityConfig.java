package com.f5.gatewayserver.Config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableWebFluxSecurity

public class SecurityConfig {
// 2차 CI/CD 테스트
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("SecurityConfig.securityWebFilterChain()" + http);
        http
                .authorizeExchange(exchanges -> exchanges
                        // GET 요청 허용
                        .pathMatchers(HttpMethod.GET,
                                "/api/auth/register/exist-email/**",
                                "/api/account/search-account/**",
                                "/api/auth/user-info/**",
                                "/api/auth/login/kakao/page",
                                "/api/account/user-list",
                                "/api/auth/email/**",
                                "/api/pickup/waste/type-list",
                                "/api/pickup/my-pickup/**",
                                "/api/pickup/get-details",
                                "/api/pickup/get-all-pickups",
                                "/api/chat/all",
                                "/api/chat/user-chats",
                                "/api/pickup/get-location",
                                "/api/account/designer/get-designer/**",
                                "/api/account/designer/all-designer",
                                "/api/product/product-list",
                                "/api/**",
                                "/api/home",
                                "/v1/**").permitAll()

                        // POST 요청 허용
                        .pathMatchers(HttpMethod.POST,
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/admin/login",
                                "/api/get-secret",
                                "/api/auth/login/kakao/token",
                                "/api/account/designer/add-designer",
                                "/api/auth/kakao/login",
                                "/api/auth/kakao/register",
                                "/api/pickup/new-pickup",
                                "/api/chat/create-chat",
                                "/api/pickup/update-location",
                                "/api/auth/register/deliver",
                                "/api/**",
                                "/v1/**").permitAll()

                        // PATCH 요청 허용
                        .pathMatchers(HttpMethod.PATCH,
                                "/api/account/update-account",
                                "/api/auth/kakao/integration",
                                "/api/pickup/update-pickup",
                                "/api/account/designer/update-designer",
                                "/api/**",
                                "/v1/**").permitAll()

                        .pathMatchers(HttpMethod.DELETE,
                                "/api/account/designer/remove-designer",
                                "/api/pickup/delete-pickup",
                                "/api/auth/withdraw",
                                "/api/**",
                                "/api/pickup/delete-location",
                                "/v1/**").permitAll()

                        .anyExchange().authenticated() // 그 외의 요청은 인증 필요
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable); // CSRF 비활성화

        return http.build();
    }

    // CORS 설정 추가 (도메인 제한 걸어버림. 이거만 있을 때엔 포스트맨은 먹히긴 함. 프론트에서 실험 좀 해봐야 할 듯)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://refresh-f5.store", "http://127.0.0.1:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}