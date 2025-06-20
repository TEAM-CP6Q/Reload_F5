package com.f5.authserver.Config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class JwtKeyConfig {

    @Bean
    public SecretKey secretKey() {
        // 모든 클래스에서 동일한 SecretKey를 사용할 수 있도록 Bean으로 관리
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
}
