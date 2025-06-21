package com.f5.accountserver.Service.Communication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class CommunicationServiceImpl implements CommunicationService {
    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    public CommunicationServiceImpl(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
    }

    @Override
    public String getEmail(String name) throws URISyntaxException {
        log.info("Getting email for name: {}", name);

        // 1. discoveryClient null 체크
        if (discoveryClient == null) {
            log.error("DiscoveryClient is null");
            throw new IllegalStateException("DiscoveryClient is not initialized");
        }

        List<ServiceInstance> instances = discoveryClient.getInstances("AUTH-SERVER");
        log.info("Found {} AUTH-SERVER instances", instances != null ? instances.size() : 0);

        if (instances == null || instances.isEmpty()) {
            log.error("No Auth-Server instances available");
            throw new IllegalStateException("No Auth-Server instances available");
        }

        // 랜덤하게 하나의 인스턴스를 선택
        ServiceInstance accountService = instances.get(new Random().nextInt(instances.size()));
        log.info("Selected instance: {}:{}", accountService.getHost(), accountService.getPort());

        // URI 생성 및 로깅
        URI uri = UriComponentsBuilder.fromUri(accountService.getUri())
                .path("/api/auth/email/" + name)
                .build()
                .toUri();

        log.info("Generated URI: {}", uri.toString());
        log.info("Postman URL과 비교해보세요!");

        // 2. restTemplate null 체크
        if (restTemplate == null) {
            log.error("RestTemplate is null");
            throw new IllegalStateException("RestTemplate is not initialized");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        try {
            log.info("Sending request to: {}", uri);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);

            log.info("Response status: {}", response.getStatusCode());
            log.info("Response body: {}", response.getBody());

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("Request failed with status: {}", response.getStatusCode());
                throw new IllegalStateException("Failed to get email information.");
            }
        } catch (Exception e) {
            log.error("Exception occurred while calling AUTH-SERVER", e);
            log.error("Exception type: {}", e.getClass().getSimpleName());
            log.error("Exception message: {}", e.getMessage());
            throw new IllegalStateException("Failed to send request to Auth-Server", e);
        }
    }


    @Override
    public Long searchInfo(String email) throws URISyntaxException {
        List<ServiceInstance> instances = discoveryClient.getInstances("AUTH-SERVER");
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("No Auth-Server instances available");
        }

        // Auth-Server 인스턴스 중 하나를 무작위로 선택
        ServiceInstance accountService = instances.get(new Random().nextInt(instances.size()));

        // URI 생성
        URI uri = UriComponentsBuilder.fromUri(new URI("http://10.10.0.154:10000"))
                .path("/api/auth/user-info/{email}")
                .buildAndExpand(email)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        try {
            // 응답을 Map<String, Object>로 받도록 제네릭 타입을 명확히 지정
            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                // 'id' 값을 Long으로 변환
                Number id = (Number) responseBody.get("id");
                return id != null ? id.longValue() : null;
            } else {
                throw new IllegalStateException("Failed to get user information.");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send request to Auth-Server", e);
        }
    }


}
