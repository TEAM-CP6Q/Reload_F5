package com.f5.authserver.DTO.Kakao;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAuthResponseDTO {
    private String accessToken;
}

