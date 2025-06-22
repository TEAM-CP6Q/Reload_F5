package com.f5.authserver.Controller;

import com.f5.authserver.DAO.User.UserDAO;
import com.f5.authserver.DTO.Auth.RegisterDTO;
import com.f5.authserver.DTO.StatusCodeDTO;
import com.f5.authserver.DTO.User.UserDTO;
import com.f5.authserver.Service.User.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/register")
public class RegisterController {
    private final UserService userService;
    private final UserDAO userDAO;
//    private RestTemplate restTemplate;

//    @PostMapping
//    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
//        try {
//            UserDTO savedUser = userService.registerUser(registerDTO);
//            return ResponseEntity.ok(savedUser);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(404).body(StatusCodeDTO.builder()
//                            .Code(404L)
//                            .Msg(e.getMessage())
//                            .build());
//        } catch (IllegalStateException e) {
//            return ResponseEntity.status(405).body(StatusCodeDTO.builder()
//                            .Code(405L)
//                            .Msg(e.getMessage())
//                            .build());
//        }
//    }

    public String getAdminAccessToken() {
        String tokenUrl = "http://13.124.171.192:8090/realms/master/protocol/openid-connect/token";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "admin-cli"); // 확인 필요: 관리자용 클라이언트는 보통 'admin-cli'
        params.add("username", "admin");
        params.add("password", "qwer1234");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);
            System.out.println("Keycloak 응답 상태: " + response.getStatusCode());
            System.out.println("Keycloak 응답 바디: " + response.getBody());

            if (response.getStatusCode() == HttpStatus.OK) {
                return (String) response.getBody().get("access_token");
            } else {
                throw new RuntimeException("Keycloak 관리자 토큰 획득 실패 - 응답 코드: " + response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            System.out.println("Keycloak 요청 실패 (HttpClientErrorException)");
            System.out.println("응답 코드: " + e.getStatusCode());
            System.out.println("응답 바디: " + e.getResponseBodyAsString());
            throw new RuntimeException("Keycloak 관리자 토큰 요청 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Keycloak 요청 실패 (기타 예외)");
            e.printStackTrace();
            throw new RuntimeException("예상치 못한 오류: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
        try {
            // 1. 먼저 내부 시스템(DB)에 사용자 등록
            UserDTO savedUser = userService.registerUser(registerDTO);

            // 2. Keycloak 사용자 등록용 토큰 발급
            String keycloakAdminToken = getAdminAccessToken();

            // 3. Keycloak 요청 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(keycloakAdminToken);

            // 4. Keycloak 사용자 등록 요청 Body 생성
            Map<String, Object> user = new HashMap<>();
            user.put("username", registerDTO.getEmail()); // 일반적으로 email을 username으로 설정
            user.put("enabled", true);
            user.put("email", registerDTO.getEmail());

            Map<String, Object> credentials = new HashMap<>();
            credentials.put("type", "password");
            credentials.put("value", registerDTO.getPassword());
            credentials.put("temporary", false); // boolean이므로 문자열이 아닌 false로 설정

            user.put("credentials", List.of(credentials));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);
            RestTemplate restTemplate = new RestTemplate();
            // 5. Keycloak 사용자 등록 요청
            ResponseEntity<Void> keycloakResponse = restTemplate.postForEntity(
                    "http://13.124.171.192:8090/admin/realms/test/users",
                    request,
                    Void.class
            );

            // 6. 응답 처리
            if (keycloakResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(savedUser);
            } else {
                return ResponseEntity.status(502).body(StatusCodeDTO.builder()
                        .Code(502L)
                        .Msg("Keycloak 사용자 등록 실패")
                        .build());
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(StatusCodeDTO.builder()
                    .Code(404L)
                    .Msg(e.getMessage())
                    .build());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(405).body(StatusCodeDTO.builder()
                    .Code(405L)
                    .Msg(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(StatusCodeDTO.builder()
                    .Code(500L)
                    .Msg("서버 오류: " + e.getMessage())
                    .build());
        }
    }



    @PostMapping("/deliver")
    public ResponseEntity<?> deliver(@RequestParam("email") String email, @RequestParam("password") String password) {
        try{
            UserDTO saveDeliver = userService.registerDeliver(UserDTO.builder()
                            .email(email)
                            .password(password)
                            .build());
            return ResponseEntity.ok(saveDeliver);
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(404).body(StatusCodeDTO.builder()
                            .Code(404L)
                            .Msg(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/exist-email/{email}")
    public ResponseEntity<?> existEmail(@PathVariable String email) {
        if(userDAO.existsByEmail(email)) {
            return ResponseEntity.status(404).body(StatusCodeDTO.builder()
                            .Code(404L)
                            .Msg("이미 존재하는 이메일")
                            .build());
        } else return ResponseEntity.ok(StatusCodeDTO.builder()
                        .Code(200L)
                        .Msg("사용 가능한 이메일")
                        .build());
    }
}
