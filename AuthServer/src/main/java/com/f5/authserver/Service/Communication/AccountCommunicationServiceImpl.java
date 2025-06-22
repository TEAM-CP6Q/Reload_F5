package com.f5.authserver.Service.Communication;

import com.f5.authserver.DTO.StatusCodeDTO;
import com.f5.authserver.DTO.User.UserDetailDTO;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Random;

@Service
public class AccountCommunicationServiceImpl implements AccountCommunicationService {

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    public AccountCommunicationServiceImpl(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
    }

    @Override
    public void registerAccount(UserDetailDTO userDetailDTO) {
//        List<ServiceInstance> instances = discoveryClient.getInstances("ACCOUNT-SERVER");
//        if (instances == null || instances.isEmpty()) {
//            throw new IllegalStateException("No Account-Server instances available");
//        }

        // URI 생성 부분 수정
//        ServiceInstance accountService = instances.get(0);
        URI uri = URI.create("http://10.10.0.154:11000" + "/api/account/add-details");

        // HTTP 헤더 및 본문 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // DTO를 HttpEntity에 포함
        HttpEntity<UserDetailDTO> httpEntity = new HttpEntity<>(userDetailDTO, headers);

        // REST 요청 보내기
        try {
            ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, Void.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Request to update new user account information sent successfully.");
            } else {
                throw new IllegalStateException("Failed to send request to update new user account information.");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send request to Account-Server", e);
        }
    }

    @Override
    public void deleteAccount(Long id) throws URISyntaxException {
        List<ServiceInstance> instances = discoveryClient.getInstances("ACCOUNT-SERVER");
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("No Account-Server instances available");
        }

        // 무작위 인스턴스 선택 (로드 밸런싱을 위해)
        ServiceInstance accountService = instances.get(new Random().nextInt(instances.size()));

        // URI 생성
        URI uri = UriComponentsBuilder.fromUri(new URI("http://10.10.0.154:11000"))
                .path("/api/account/withdraw/{id}")
                .buildAndExpand(id)
                .toUri();

        // HTTP 헤더 및 본문 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        // REST 요청 보내기
        try {
            ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, httpEntity, Void.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Request to update withdraw account information sent successfully.");
            } else {
                throw new IllegalStateException("Failed to send request to update withdraw account information.");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send request to Account-Server", e);
        }
    }

    @Override
    public String getAccountEmail(Long id) throws URISyntaxException {
        List<ServiceInstance> instances = discoveryClient.getInstances("ACCOUNT-SERVER");
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("No Account-Server instances available");
        }
        ServiceInstance accountService = instances.get(new Random().nextInt(instances.size()));

        // URI 생성
        URI uri = UriComponentsBuilder.fromUri(new URI("http://10.10.0.154:11000"))
                .path("/api/account/email/{id}")
                .buildAndExpand(id)
                .toUri();
        // HTTP 헤더 및 본문 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        // REST 요청 보내기
        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Request to get account email successfully.");
                return response.getBody();
            } else {
                throw new IllegalStateException("Failed to send request to get account email.");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send request to Account-Server", e);
        }
    }

    @Override
    public void releaseAccount(Long id) throws URISyntaxException {
        List<ServiceInstance> instances = discoveryClient.getInstances("ACCOUNT-SERVER");
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("No Account-Server instances available");
        }
        ServiceInstance accountService = instances.get(new Random().nextInt(instances.size()));

        // URI 생성
        URI uri = UriComponentsBuilder.fromUri(new URI("http://10.10.0.154:11000"))
                .path("/api/account/rollback-account/{id}")
                .buildAndExpand(id)
                .toUri();

        // HTTP 헤더 및 본문 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        // REST 요청 보내기
        try {
            ResponseEntity<StatusCodeDTO> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, StatusCodeDTO.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                StatusCodeDTO statusCodeDTO = response.getBody();
                System.out.println("Request to release account successfully. Code: " + statusCodeDTO.getCode() + ", Message: " + statusCodeDTO.getMsg());
            } else {
                throw new IllegalStateException("Failed to send request to release account information.");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send request to Account-Server");
        }
    }

}
