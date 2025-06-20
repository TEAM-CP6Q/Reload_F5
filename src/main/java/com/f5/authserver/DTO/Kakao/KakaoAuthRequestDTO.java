package com.f5.authserver.DTO.Kakao;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAuthRequestDTO {
    private String code;
}