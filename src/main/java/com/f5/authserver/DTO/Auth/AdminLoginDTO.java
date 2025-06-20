package com.f5.authserver.DTO.Auth;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminLoginDTO {
    private String adminCode;
}
