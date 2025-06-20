//package com.f5.authserver.Controller;
//
//import com.f5.authserver.DAO.User.UserDAO;
//import com.f5.authserver.DTO.Auth.RegisterDTO;
//import com.f5.authserver.DTO.StatusCodeDTO;
//import com.f5.authserver.DTO.Kakao.KakaoAuthRequestDTO;
//import com.f5.authserver.DTO.Kakao.KakaoLoginDTO;
//import com.f5.authserver.DTO.User.UserDTO;
//import com.f5.authserver.Entity.UserEntity;
//import com.f5.authserver.JWT.JwtTokenUtil;
//import com.f5.authserver.Repository.UserRepository;
//import com.f5.authserver.Service.Kakao.KakaoAuthService;
//import com.f5.authserver.Service.User.CustomUserDetailsService;
//import com.f5.authserver.Service.User.UserService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import java.net.URISyntaxException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import static org.springframework.http.ResponseEntity.*;
//
//@RestController
//@RequestMapping("/api/auth/kakao")
//public class KakaoAuthController {
//    private final UserService userService;
//    private final JwtTokenUtil jwtTokenUtil;
//    private final CustomUserDetailsService customUserDetailsService;
//    private final UserDAO userDAO;
//
//    private static final Logger logger = LoggerFactory.getLogger(KakaoAuthController.class);
//
//    private final KakaoAuthService kakaoAuthService;
//    private final UserRepository userRepository;
//
//    public KakaoAuthController(UserService userService, JwtTokenUtil jwtTokenUtil, CustomUserDetailsService customUserDetailsService, UserDAO userDAO, KakaoAuthService kakaoAuthService, UserRepository userRepository) {
//        this.userService = userService;
//        this.jwtTokenUtil = jwtTokenUtil;
//        this.customUserDetailsService = customUserDetailsService;
//        this.userDAO = userDAO;
//        this.kakaoAuthService = kakaoAuthService;
//        this.userRepository = userRepository;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> kakaoLogin(@RequestBody KakaoAuthRequestDTO request) throws AuthenticationException {
//        try {
//            logger.info("Received Kakao auth code: {}", request.getCode());
//            KakaoLoginDTO status;
//            status = kakaoAuthService.loginKakao(request.getCode());
//            String email = status.getEmail();
//
//            UserEntity loggedInUser = userService.getLoggedInUserEntity(email);
//
//
//            final UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
//            final String token = jwtTokenUtil.generateToken(userDetails.getUsername(), loggedInUser.getRole());
//
//            // UserEntity를 서비스 메서드를 통해 가져옴
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("token", token);
//            response.put("user", loggedInUser);
//            return ok(response);
//
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            if (e.getMessage().equals("Need register")) {
//                return ResponseEntity.status(404).body(StatusCodeDTO.builder()
//                        .Code(404L)
//                        .Msg("카카오 회원가입을 진행해주세요.")
//                        .build());
//            } else if (e.getMessage().equals("Need integration")) {
//                return ResponseEntity.status(405).body(StatusCodeDTO.builder()
//                        .Code(405L)
//                        .Msg("통합을 진행해주세요.")
//                        .build());
//            } else {
//                return status(406).body(StatusCodeDTO.builder()
//                        .Code(406L)
//                        .Msg("토큰 처리 중 서버 오류")
//                        .build());
//            }
//        }
//    }
//
//    @PatchMapping("/integration")
//    public ResponseEntity<?> integrationAccount(@RequestBody KakaoAuthRequestDTO request) {
//        try{
//            logger.info("Received Kakao auth code: {}", request.getCode());
//            String status = kakaoAuthService.integrationAccount(request.getCode());
//            return ok(StatusCodeDTO.builder()
//                    .Code(200L)
//                    .Msg(status)
//                    .build());
//        } catch (URISyntaxException e) {
//            return status(404).body(StatusCodeDTO.builder()
//                            .Code(404L)
//                            .Msg("통합 실패")
//                            .build());
//        }
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<?> registerUser(@RequestBody KakaoAuthRequestDTO request) {
//        try {
//            RegisterDTO registerDTO = kakaoAuthService.registerKakao(request.getCode());
//            try {
//                UserDTO savedUser = userService.registerKakaoUser(registerDTO);
//                return ok(savedUser);
//            } catch (IllegalArgumentException e) {
//                logger.error("이미 가입된 계정");
//                return ResponseEntity.status(404).body(StatusCodeDTO.builder()
//                                .Code(404L)
//                                .Msg(e.getMessage())
//                                .build());
//            } catch (IllegalStateException e) {
//                logger.error("탈퇴한 계정");
//                return ResponseEntity.status(405).body(StatusCodeDTO.builder()
//                        .Code(405L)
//                        .Msg(e.getMessage())
//                        .build());
//            }
//        } catch (URISyntaxException e) {
//            return ResponseEntity.status(406).body(StatusCodeDTO.builder()
//                            .Code(406L)
//                            .Msg("카카오톡 회원가입 실패")
//                            .build());
//        }
//    }
//}
//
