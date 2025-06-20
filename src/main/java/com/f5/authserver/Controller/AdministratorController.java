package com.f5.authserver.Controller;

import com.f5.authserver.DTO.Auth.AdminLoginDTO;
import com.f5.authserver.DTO.StatusCodeDTO;
import com.f5.authserver.DTO.User.AdministratorDTO;
import com.f5.authserver.Entity.AdministratorEntity;
import com.f5.authserver.JWT.JwtTokenUtil;
import com.f5.authserver.Repository.AdministratorRepository;
import com.f5.authserver.Service.Administrator.AdministratorService;
import com.f5.authserver.Service.User.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/auth/admin")
//@CrossOrigin(origins = {"http://127.0.0.1:3000", "http://192.168.0.11:3000", "http://121.182.42.161:8000"})
public class AdministratorController {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AdministratorService administratorService;
    private final AdministratorRepository administratorRepository;

    public AdministratorController(PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService, JwtTokenUtil jwtTokenUtil, AdministratorService administratorService, AdministratorRepository administratorRepository) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.administratorService = administratorService;
        this.administratorRepository = administratorRepository;
    }


    // Spring Security 의 UserDetails 를 이용한 JWT 토큰 발급 후 리턴
//    @PostMapping("/login")
//    public ResponseEntity<?> createAuthenticationToken(@RequestBody AdminLoginDTO admin) throws AuthenticationException {
//        // 전체 어드민 리스트 가져오기
//        List<AdministratorEntity> adminList = administratorRepository.findAll();
//
//        // 일치하는 adminCode를 가진 어드민 찾기
//        AdministratorEntity adminEntity = adminList.stream()
//                .filter(a -> passwordEncoder.matches(admin.getAdminCode(), a.getAdminCode()))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("해당 어드민을 찾을 수 없습니다."));
//
//        // 인증 처리
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(adminEntity.getAdminName(), admin.getAdminCode())
//        );
//
//        // UserDetails로부터 JWT 토큰 생성
//        final UserDetails adminDetails = customUserDetailsService.loadUserByUsername(adminEntity.getAdminName());
//        final String token = jwtTokenUtil.generateToken(adminDetails.getUsername(), "admin");
//
//        // 어드민 정보와 JWT 토큰 응답
//        Map<String, Object> response = new HashMap<>();
//        response.put("token", token);
//        response.put("admin", adminEntity);
//
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AdminLoginDTO admin) {
        log.info("[ADMIN LOGIN] 로그인 요청 - AdminCode: {}", admin.getAdminCode());

        try {
            // 전체 어드민 리스트 가져오기
            List<AdministratorEntity> adminList = administratorRepository.findAll();
            log.debug("[ADMIN LOGIN] 어드민 전체 수: {}", adminList.size());

            // 일치하는 adminCode를 가진 어드민 찾기
            AdministratorEntity adminEntity = adminList.stream()
                    .filter(a -> passwordEncoder.matches(admin.getAdminCode(), a.getAdminCode()))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.warn("[ADMIN LOGIN] 일치하는 AdminCode 없음");
                        return new IllegalArgumentException("해당 어드민을 찾을 수 없습니다.");
                    });

            log.info("[ADMIN LOGIN] 어드민 인증 시도 - AdminName: {}", adminEntity.getAdminName());

            // 인증 처리
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(adminEntity.getAdminName(), admin.getAdminCode())
            );

            log.info("[ADMIN LOGIN] Spring Security 인증 성공 - {}", adminEntity.getAdminName());

            // UserDetails로부터 JWT 토큰 생성
            final UserDetails adminDetails = customUserDetailsService.loadUserByUsername(adminEntity.getAdminName());
            final String token = jwtTokenUtil.generateToken(adminDetails.getUsername(), "admin");

            // 어드민 정보와 JWT 토큰 응답
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("admin", adminEntity);

            log.info("[ADMIN LOGIN] JWT 토큰 발급 완료 - AdminName: {}", adminEntity.getAdminName());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("[ADMIN LOGIN] 인증 실패 - 잘못된 자격 증명", e);
            return ResponseEntity.status(403).body(
                    Map.of("msg", "잘못된 인증 정보입니다.", "code", 403)
            );
        } catch (IllegalArgumentException e) {
            log.warn("[ADMIN LOGIN] 어드민 찾기 실패: {}", e.getMessage());
            return ResponseEntity.status(404).body(
                    Map.of("msg", e.getMessage(), "code", 404)
            );
        } catch (Exception e) {
            log.error("[ADMIN LOGIN] 서버 오류 발생", e);
            return ResponseEntity.status(500).body(
                    Map.of("msg", "서버 오류: " + e.getMessage(), "code", 500)
            );
        }
    }


//    @PostMapping("/login")
//    public ResponseEntity<?> createAuthenticationToken(@RequestBody AdminLoginDTO admin) throws AuthenticationException {
//        try {
//            // Keycloak 토큰 엔드포인트
//            String tokenUrl = "http://13.124.171.192:8090/realms/test/protocol/openid-connect/token"; //추가
//            RestTemplate restTemplate = new RestTemplate(); //추가
//
//            HttpHeaders headers = new HttpHeaders(); //추가
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED); //추가
//
//            MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); //추가
//            params.add("grant_type", "password"); //추가
//            params.add("client_id", "testClient"); //추가
//            params.add("client_secret", "QvyL4HTKlaUAloJ3DzHSu1RZRynYlHRE"); //클라이언트가 공개 타입이 아니라면 필수 //추가
//            params.add("username", test.getAdminName()); //추가
//            params.add("password", "test1234"); //비밀번호 대신 adminCode를 ID로 사용하는 경우가 아니라면 수정 필요 //추가
//
//            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers); //추가
//
//            ResponseEntity<Map> keycloakResponse = restTemplate.postForEntity(tokenUrl, request, Map.class); //추가
//
//            return ResponseEntity.ok(keycloakResponse.getBody()); //추가
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                    Map.of("error", "Keycloak 인증 실패", "details", e.getMessage()) //추가
//            );
//        }
//    }

    // 쓸 일이 있으려나 싶지만 일단 어드민 추가
    @PostMapping("/register")
    public ResponseEntity<?> addAdmin(@RequestBody AdministratorDTO admin) throws AuthenticationException {
        try{
            AdministratorDTO saveAdmin = administratorService.addAdministrator(admin);
            return ResponseEntity.ok(saveAdmin);
        } catch (Exception e){
            return ResponseEntity.status(404).body(StatusCodeDTO.builder()
                            .Code(404L)
                            .Msg(e.getMessage())
                            .build());
        }
    }
}

