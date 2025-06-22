package com.f5.authserver.DTO.User;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDetailDTO {
    private Long id;
    private String name;
    private String postalCode;
    private String roadNameAddress;
    private String detailedAddress;
    private String phoneNumber;
}
