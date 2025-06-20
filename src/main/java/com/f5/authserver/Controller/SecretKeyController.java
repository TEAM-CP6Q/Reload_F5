package com.f5.authserver.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
public class SecretKeyController {

    private final SecretKey secretKey;

    public SecretKeyController(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @GetMapping("/key")
    public ResponseEntity<String> getSecretKey() {
        return ResponseEntity.ok(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }
}
