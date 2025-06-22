package com.f5.authserver.Controller;

import com.f5.authserver.DTO.StatusCodeDTO;
import com.f5.authserver.DTO.User.UserDTO;
import com.f5.authserver.Entity.UserEntity;
import com.f5.authserver.JWT.JwtTokenUtil;
import com.f5.authserver.Service.Communication.AccountCommunicationService;
import com.f5.authserver.Service.User.CustomUserDetailsService;
import com.f5.authserver.Service.User.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
//@CrossOrigin(origins = {"http://127.0.0.1:3000", "http://192.168.0.11:3000", "http://121.182.42.161:8000"})
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final AccountCommunicationService accountCommunicationService;

// dd
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody UserDTO user) throws AuthenticationException {
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
//
//            final UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
//            final String token = jwtTokenUtil.generateToken(userDetails.getUsername(), "user");
//
//            // UserEntity를 서비스 메서드를 통해 가져옴
//            UserEntity loggedInUser = userService.getLoggedInUserEntity(user.getEmail());
//
//            if (!loggedInUser.getKakao()) {
//                Map<String, Object> response = new HashMap<>();
//                response.put("token", token);
//                response.put("user", loggedInUser);
//                return ResponseEntity.ok(response);
//            } else {
//                return ResponseEntity.ok().body(StatusCodeDTO.builder()
//                        .Code(400L)
//                        .Msg("카카오 계정으로 로그인 해주세요.")
//                        .build());
//            }
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(402).body(StatusCodeDTO.builder()
//                            .Code(402L)
//                            .Msg("계정이 없음")
//                            .build());
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO user) {
        log.info("[LOGIN] 로그인 요청 - 이메일: {}", user.getEmail());

        try {
            String tokenUrl = "http://13.124.171.192:8090/realms/test/protocol/openid-connect/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "password");
            params.add("client_id", "testClient");
            params.add("client_secret", "cKqNJl0AdmttMi5Fq4SOTCm6AmSz8dlX"); // confidential client일 경우
            params.add("username", user.getEmail());
            params.add("password", user.getPassword());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String accessToken = (String) response.getBody().get("access_token");

                UserEntity loggedInUser = userService.getLoggedInUserEntity(user.getEmail());

                if (!loggedInUser.getKakao()) {
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("token", accessToken);
                    responseMap.put("user", loggedInUser);

                    log.info("[LOGIN SUCCESS] 사용자 로그인 성공 - 이메일: {}", user.getEmail());
                    return ResponseEntity.ok(responseMap);
                } else {
                    log.warn("[LOGIN BLOCKED] 카카오 계정 로그인 시도 차단 - 이메일: {}", user.getEmail());
                    return ResponseEntity.badRequest().body(StatusCodeDTO.builder()
                            .Code(400L)
                            .Msg("카카오 계정으로 로그인 해주세요.")
                            .build());
                }
            } else {
                log.warn("[LOGIN FAILED] Keycloak 응답 비정상 - 이메일: {}, 응답 코드: {}", user.getEmail(), response.getStatusCode());
                return ResponseEntity.status(401).body(StatusCodeDTO.builder()
                        .Code(401L)
                        .Msg("로그인 실패: Keycloak 응답 오류")
                        .build());
            }

        } catch (HttpClientErrorException e) {
            log.warn("[LOGIN FAILED] Keycloak 인증 실패 - 이메일: {}, 오류: {}", user.getEmail(), e.getMessage());
            return ResponseEntity.status(401).body(StatusCodeDTO.builder()
                    .Code(401L)
                    .Msg("Keycloak 인증 실패: " + e.getMessage())
                    .build());

        } catch (Exception e) {
            log.error("[LOGIN ERROR] 서버 내부 오류 - 이메일: {}, 예외: {}", user.getEmail(), e.getMessage(), e);
            return ResponseEntity.status(500).body(StatusCodeDTO.builder()
                    .Code(500L)
                    .Msg("서버 오류: " + e.getMessage())
                    .build());
        }
    }

    // 원래 이렇게 하면 안돼지만 귀찮으므로 걍 함
    @GetMapping("/user-info/{email}")
    public ResponseEntity<?> userInfo(@PathVariable String email) throws IllegalStateException {
        return ResponseEntity.ok(userService.getLoggedInUserEntity(email));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdraw(@RequestBody UserDTO user) {
        try {
            // 아이디와 비밀번호 인증
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            // 인증 성공 시 탈퇴 로직 진행
            userService.deleteAccount(user);
            return ResponseEntity.ok(StatusCodeDTO.builder()
                            .Code(200L)
                            .Msg("탈퇴 성공")
                            .build());

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(StatusCodeDTO.builder()
                            .Code(401L)
                            .Msg("아이디 또는 비밀번호가 올바르지 않습니다.")
                            .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(403).body(StatusCodeDTO.builder()
                            .Code(403L)
                            .Msg("탈퇴 실패")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(StatusCodeDTO.builder()
                            .Code(500L)
                            .Msg("서버 오류 발생")
                            .build());
        }
    }

    @GetMapping("/email/{id}")
    public ResponseEntity<?> emails(@PathVariable Long id) {
        try{
            return ResponseEntity.ok(userService.getEmailById(id));
        } catch (Exception e) {
            return ResponseEntity.ok().body(StatusCodeDTO.builder()
                            .Code(400L)
                            .Msg(e.getMessage())
                            .build());
        }
    }
}
