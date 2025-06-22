//package com.f5.authserver.Config;
//
//import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
//import org.springframework.security.oauth2.core.AuthorizationGrantType;
//
//@EnableWebSecurity
//@KeycloakConfiguration
//public class KeycloakConfig {
//    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
//    private String clientId;
//
//    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
//    private String clientSecret;
//
//    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
//    private String baseUrl;
//
//    @Value("${spring.security.oauth2.client.registration.keycloak.redirect-uri}")
//    private String redirectUrl;
//
//
//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        ClientRegistration keycloakRegistration = ClientRegistration.withRegistrationId("keycloak")
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .authorizationUri(baseUrl +"/auth")
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .redirectUri(redirectUrl)
//                .tokenUri( baseUrl + "/token")
//                .userInfoUri( baseUrl + "/userinfo")
//                .jwkSetUri(baseUrl + "/certs")
//                .userNameAttributeName("preferred_username")
//                .build();
//
//        return new InMemoryClientRegistrationRepository(keycloakRegistration);
//    }
//
//}
