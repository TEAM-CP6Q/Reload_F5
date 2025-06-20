package com.f5.authserver.DTO.Kakao;

import lombok.*;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLoginDTO {
    private String email;
    private String userId;
}
