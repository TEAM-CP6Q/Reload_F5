package com.f5.authserver.DTO.Kakao;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserKakaoDTO {
    private String email;
    private String password;
    private Boolean kakao;
    private String userId;
}
