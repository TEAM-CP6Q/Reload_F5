package com.f5.authserver.DTO.Auth;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RegisterDTO {
    private String email;
    private String password;
    private String name;
    private String postalCode;
    private String roadNameAddress;
    private String detailedAddress;
    private String phoneNumber;
}
