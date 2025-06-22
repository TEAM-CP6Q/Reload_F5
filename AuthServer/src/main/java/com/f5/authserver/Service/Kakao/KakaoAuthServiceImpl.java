//package com.f5.authserver.Service.Kakao;
//
//import com.f5.authserver.DAO.User.UserDAO;
//import com.f5.authserver.DTO.Kakao.AddressDTO;
//import com.f5.authserver.DTO.Kakao.IntegrationDTO;
//import com.f5.authserver.DTO.Auth.RegisterDTO;
//import com.f5.authserver.DTO.Kakao.KakaoLoginDTO;
//import com.f5.authserver.DTO.Kakao.UserKakaoDTO;
//import com.f5.authserver.Repository.UserRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.client.HttpClientErrorException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.LinkedHashMap;
//import java.util.List;
//
//@Slf4j
//@Service
//public class KakaoAuthServiceImpl implements KakaoAuthService {
//
//    private final UserDAO userDAO;
//    private final UserRepository userRepository;
//
//    @Value("${kakao.client-id}") // application.properties에서 클라이언트 ID 가져오기
//    private String clientId;
//
//    @Value("${kakao.redirect-uri}") // 리다이렉트 URI
//    private String redirectUri;
//
//    //private final RestTemplate restTemplate;
//    private final ObjectMapper objectMapper;
//
//    public KakaoAuthServiceImpl(UserDAO userDAO, ObjectMapper objectMapper, UserRepository userRepository) {
//        this.userDAO = userDAO;
//        //this.restTemplate = restTemplate;
//        this.objectMapper = objectMapper;
//        this.userRepository = userRepository;
//    }
//
//    private LinkedHashMap<String, Object> getKakaoInfo(String authCode) throws URISyntaxException {
//        RestTemplate rt = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");
//        params.add("client_id", clientId);
//        params.add("redirect_uri", redirectUri);
//        params.add("code", authCode);
//
//        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
//        ResponseEntity<LinkedHashMap> accessTokenResponse = rt.exchange(
//                "https://kauth.kakao.com/oauth/token",
//                HttpMethod.POST,
//                kakaoTokenRequest,
//                LinkedHashMap.class
//        );
//        String accessToken = accessTokenResponse.getBody().get("access_token").toString();
//        LinkedHashMap<String, Object> value = fetchKakaoUserData(accessToken);
//        value.put("access_token", accessToken);
//        return value;
//    }
//
//    @Override
//    public String integrationAccount(String authCode) throws URISyntaxException {
//        LinkedHashMap<String, Object> value = this.getKakaoInfo(authCode);
//        IntegrationDTO integrationDTO = IntegrationDTO.builder()
//                .email(value.get("email").toString())
//                .userId(value.get("id").toString())
//                .build();
//        try{
//            userDAO.integrationInfo(integrationDTO);
//            return "계정 통합에 성공하였습니다.";
//        } catch (Exception e) {
//            throw new IllegalStateException("계정 통합에 실패하였습니다.");
//        }
//    }
//
//    @Override
//    public KakaoLoginDTO loginKakao(String authCode) {
//        try {
//            LinkedHashMap<String, Object> value = this.getKakaoInfo(authCode);
//
//            String email = (String) value.get("email");
//            Long id = (Long) value.get("id");
//            String phoneNumber = (String) value.get("phone_number");
//            String name = value.get("name").toString();
//
//            System.out.printf("email: " + email + ", id: " +
//                    id + ", phoneNumber: " + phoneNumber +
//                    ", name: " + name + "\n");
//            log.info("일단 정보 가져오는거 성공");
//            Boolean status = userRepository.existsByEmail(email);
//            log.info("exists 성공");
//            if(!status) {
//                log.info("회갑 필요");
//                throw new IllegalStateException("Need register");
//            }
//            else if (!userDAO.getKakaoByEmail(email).getKakao()) {
//                log.info("통합 필요");
//                throw new IllegalStateException("Need integration");
//            }
//            log.info("status 검증 성공");
//
//            UserKakaoDTO userKakaoDTO = userDAO.getKakaoByEmail(email);
//            log.info("서비스단에선 성공");
//            return KakaoLoginDTO.builder()
//                    .email(userKakaoDTO.getEmail())
//                    .userId(userKakaoDTO.getUserId())
//                    .build();
//
//        } catch (HttpClientErrorException e) {
//            System.err.println("카카오 API 요청 실패: " + e.getResponseBodyAsString());
//            throw new RuntimeException("카카오 API 요청 실패", e);
//        } catch (IllegalStateException e) {
//            throw new RuntimeException(e.getMessage());
//        } catch (URISyntaxException e) {
//            throw new RuntimeException("JSON 파싱 오류");
//        }
//    }
//
//    @Override
//    public RegisterDTO registerKakao(String authCode) throws URISyntaxException {
//        LinkedHashMap<String, Object> value = this.getKakaoInfo(authCode);
//        AddressDTO addressDTO = this.getShippingAddress(value.get("access_token").toString());
//        return RegisterDTO.builder()
//                .email(value.get("email").toString())
//                .password(value.get("id").toString())
//                .name(value.get("name").toString())
//                .roadNameAddress(addressDTO.getRoadNameAddress())
//                .detailedAddress(addressDTO.getDetailedAddress())
//                .postalCode(addressDTO.getPostalCode())
//                .phoneNumber(value.get("phone_number").toString())
//                .build();
//    }
//
//    private LinkedHashMap<String, Object> fetchKakaoUserData(String kakaoAccessToken) throws URISyntaxException {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("Authorization", "Bearer " + kakaoAccessToken);
//        HttpEntity<?> http = new HttpEntity<>(headers);
//        URI uri = new URI("https://kapi.kakao.com/v2/user/me");
//
//        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uri, HttpMethod.GET, http, LinkedHashMap.class);
//        Long id = (Long) response.getBody().get("id");
//        LinkedHashMap<String, Object> value = (LinkedHashMap<String, Object>) response.getBody().get("kakao_account");
//        value.put("id", id);
//        return value;
//    }
//
//    private AddressDTO getShippingAddress(String kakaoAccessToken) throws URISyntaxException {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("Authorization", "Bearer " + kakaoAccessToken);
//        HttpEntity<?> http = new HttpEntity<>(headers);
//        URI uri = new URI("https://kapi.kakao.com/v1/user/shipping_address");
//
//        // Send the request and get the response
//        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uri, HttpMethod.GET, http, LinkedHashMap.class);
//
//        // Extract the body from the response
//        LinkedHashMap responseBody = response.getBody();
//        List<LinkedHashMap> addressList = (List<LinkedHashMap>) responseBody.get("shipping_addresses"); // Adjust key if necessary
//
//        AddressDTO addressDTO = null;
//
//        for (LinkedHashMap address : addressList) {
//            Boolean isDefault = (Boolean) address.get("is_default");
//            if (isDefault != null && isDefault) {
//                addressDTO = new AddressDTO();
//                addressDTO.setRoadNameAddress((String) address.get("base_address"));
//                addressDTO.setDetailedAddress((String) address.get("detail_address"));
//                addressDTO.setPostalCode((String) address.get("zone_number"));
//                break;
//            }
//        }
//
//        if (addressDTO == null) {
//            throw new RuntimeException("No default shipping address found");
//        }
//
//        return addressDTO;
//    }
//
//}
