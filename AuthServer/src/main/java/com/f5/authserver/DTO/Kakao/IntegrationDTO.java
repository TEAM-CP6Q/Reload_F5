package com.f5.authserver.DTO.Kakao;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class IntegrationDTO {
    private String email;
    private String userId;
}
