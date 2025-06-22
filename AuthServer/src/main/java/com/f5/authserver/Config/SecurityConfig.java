//package com.f5.authserver.Config;
//
//import lombok.RequiredArgsConstructor;
//import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authorization.AuthorizationDecision;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//@KeycloakConfiguration
//public class SecurityConfig {
//
//    @Bean
//    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorizeRequests ->
//                        authorizeRequests
//                                // 특정 IP에서만 허용
//                                .requestMatchers(HttpMethod.GET, "/api/auth/key")
//                                .access((authenticationSupplier, object) -> {
//                                    Authentication authentication = authenticationSupplier.get();
//                                    String ipAddress = object.getRequest().getRemoteAddr();
//
//                                    if (ipAddress.equals("3.37.122.192") || ipAddress.equals("121.182.42.114")) {
//                                        return new AuthorizationDecision(true);
//                                    } else if (authentication != null && authentication.isAuthenticated()) {
//                                        return new AuthorizationDecision(true);
//                                    } else {
//                                        return new AuthorizationDecision(false);
//                                    }
//                                })
//
//                                // 인증 없이 접근 가능한 엔드포인트
//                                .requestMatchers(HttpMethod.GET,
//                                        "/actuator/health", "/actuator/info",
//                                        "/api/auth/register/exist-email/**",
//                                        "/api/auth/user-info/**",
//                                        "/api/auth/key",
//                                        "/api/auth/email/**")
//                                .permitAll()
//                                .requestMatchers(HttpMethod.POST,
//                                        "/api/auth/login",
//                                        "/api/auth/register",
//                                        "/api/auth/admin/register",
//                                        "/api/auth/admin/login",
//                                        "/api/auth/login/kakao/token",
//                                        "/api/auth/kakao/login",
//                                        "/api/auth/kakao/register",
//                                        "/api/auth/register/deliver")
//                                .permitAll()
//                                .requestMatchers(HttpMethod.PATCH,
//                                        "/api/auth/kakao/integration")
//                                .permitAll()
//                                .requestMatchers(HttpMethod.DELETE,
//                                        "/api/auth/withdraw")
//                                .permitAll()
//
//                                // 나머지 요청은 인증 필요
//                                .anyRequest().authenticated()
//                )
//                // OAuth2 로그인을 키클락으로 처리
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/oauth2/authorization/keycloak") // 로그인 페이지 지정
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())); // JWT 기반 인증
//
//        return http.build();
//    }
//
//// 기존 시큐리티 코드
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http
////                .authorizeHttpRequests(authorizeRequests ->
////                        authorizeRequests
////                                // Key 요청은 특정 IP에서만 허용 (127.0.0.1은 localhost)
////                                .requestMatchers(HttpMethod.GET, "/api/auth/key")
////                                .access((authenticationSupplier, object) -> {
////                                    // Supplier에서 Authentication 객체를 가져옴
////                                    Authentication authentication = authenticationSupplier.get();
////                                    String ipAddress = object.getRequest().getRemoteAddr();
////
////                                    if (ipAddress.equals("3.37.122.192") || ipAddress.equals("121.182.42.114")) {
////                                        return new AuthorizationDecision(true); // 허용
////                                    } else if (authentication != null && authentication.isAuthenticated()) {
////                                        return new AuthorizationDecision(true); // 인증된 경우 허용
////                                    } else {
////                                        return new AuthorizationDecision(false); // 그 외의 경우 접근 거부
////                                    }
////                                })
////
////                                // /actuator/health와 같은 관리 엔드포인트는 인증 없이 접근 허용
////                                .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info")
////                                .permitAll()
////
////                                // GET 요청 허용
////                                .requestMatchers(HttpMethod.GET,
////                                        "/api/auth/register/exist-email/**",
////                                        "/api/auth/user-info/**",
////                                        "/api/auth/key",
////                                        "/api/auth/email/**")
////                                .permitAll()
////
////                                // POST 요청 허용
////                                .requestMatchers(HttpMethod.POST,
////                                        "/api/auth/login",
////                                        "/api/auth/register",
////                                        "/api/auth/admin/register",
////                                        "/api/auth/admin/login",
////                                        "/api/auth/login/kakao/token",
////                                        "/api/auth/kakao/login",
////                                        "/api/auth/kakao/register",
////                                        "/api/auth/register/deliver")
////                                .permitAll()
////
////                                // PATCH 요청 허용
////                                .requestMatchers(HttpMethod.PATCH,
////                                        "/api/auth/kakao/integration")
////                                .permitAll()
////
////                                .requestMatchers(HttpMethod.DELETE,
////                                        "/api/auth/withdraw")
////                                .permitAll()
////
////                                // 그 외의 요청은 인증 필요
////                                .anyRequest().authenticated()
////                )
////                .csrf(AbstractHttpConfigurer::disable); // CSRF 비활성화
////
////        return http.build();
////    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//
//}

package com.f5.authserver.Config;

import com.f5.authserver.Service.User.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@KeycloakConfiguration
//@ConditionalOnProperty(name = "spring.security.oauth2.client.registration.enabled", havingValue = "false", matchIfMissing = true)
public class SecurityConfig {

    @Bean
    public KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }

    /**
     * Register Keycloak authentication provider and map roles with ROLE_ prefix
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider provider = new KeycloakAuthenticationProvider();
        provider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(provider);
    }

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // (2) AuthenticationManager Bean
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider()) // 👈 여기 중요
                .build();
    }



    /**
     * Define session authentication strategy for Keycloak
     */
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests

                                // /actuator/health와 같은 관리 엔드포인트는 인증 없이 접근 허용
                                .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info")
                                .permitAll()

                                // GET 요청 허용
                                .requestMatchers(HttpMethod.GET,
                                        "/api/auth/register/exist-email/**",
                                        "/api/auth/user-info/**",
                                        "/api/auth/key",
                                        "/api/auth/email/**")
                                .permitAll()

                                // POST 요청 허용
                                .requestMatchers(HttpMethod.POST,
                                        "/api/auth/login",
                                        "/api/auth/register",
                                        "/api/auth/admin/register",
                                        "/api/auth/admin/login",
                                        "/api/auth/login/kakao/token",
                                        "/api/auth/kakao/login",
                                        "/api/auth/kakao/register",
                                        "/api/auth/register/deliver")
                                .permitAll()

                                // PATCH 요청 허용
                                .requestMatchers(HttpMethod.PATCH,
                                        "/api/auth/kakao/integration")
                                .permitAll()

                                .requestMatchers(HttpMethod.DELETE,
                                        "/api/auth/withdraw")
                                .permitAll()

                                // 그 외의 요청은 인증 필요
                                .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .csrf(AbstractHttpConfigurer::disable); // CSRF 비활성화

//                .oauth2Login(Customizer.withDefaults())
//                .oauth2ResourceServer(rs -> rs.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
}