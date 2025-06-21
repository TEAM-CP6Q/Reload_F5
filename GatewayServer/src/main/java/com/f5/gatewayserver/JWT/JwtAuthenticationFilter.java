package com.f5.gatewayserver.JWT;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // JWT 검증 로직
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().toString(); // 권한 설정의 기반을 다졌도다

        // 지금 대칭키 검증을 쓰면서 secretKey 를 받아와서 쓰고 있음.
        // 이제 이걸 수정해서 auth 랑 비대칭키 인증으로 변경하고 토큰 유효성 검사를 조져야함.
        // 아니면 여기에서 토큰 검증을 auth 쪽에 엔드포인트 만들어서 검사 시키는 방법도 있긴 한데, 굳이? 싶음.

        // JWT 검증을 생략할 경로 설정
        if (path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/register/exist-email") ||
                path.startsWith("/api/auth/admin/login") ||
                path.startsWith("/api/get-secret") ||
                path.startsWith("/api/auth/login/kakao/token") ||
                path.startsWith("/api/auth/admin/register") ||
                path.startsWith("/api/auth/kakao/login") ||
                path.startsWith("/api/auth/kakao/register") ||
                path.startsWith("/api/auth/kakao/integration") ||
                path.startsWith("/api/account/user-list") ||
                path.startsWith("/api/auth/user-info") ||
                path.startsWith("/api/auth/email") ||
                path.startsWith("/api/pickup/waste/type-list") ||
                path.startsWith("/api/pickup/update-location") ||
                path.startsWith("/api/pickup/get-location") ||
                path.startsWith("/api/account/designer/get-designer") ||
                path.startsWith("/api/account/designer/all-designer") ||
                path.startsWith("/api/product/product-list") ||
                path.startsWith("/api/product/") ||
                path.startsWith("/api/auth/register/deliver") ||
                path.startsWith("/api/") ||
                path.startsWith("/v1/")){
            return chain.filter(exchange);  // 위의 경로는 JWT 검증 생략
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Invalid JWT Token for path 1: " + path); // 로그 추가
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT Token is missing");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            System.out.println("Invalid JWT Token for path 2: " + path); // 로그 추가
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT Token");
        }

        if((path.equals("/api/account/designer/update-designer") && method.equals("PATCH")) ||
                (path.equals("/api/account/designer/add-designer") && method.equals("POST"))) {
            String role = jwtUtil.getRoleFromToken(token);
            if (!Objects.equals(role, "admin")) { // ADMIN이 아니면 차단
                System.out.println("Access Denied: Admin role required : " + path);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Access Denied: Admin role required");
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // 필터 우선순위 설정
    }
}
