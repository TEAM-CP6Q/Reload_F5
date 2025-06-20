package com.f5.authserver.DTO.User;


import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdministratorDTO {
    private String adminName;
    private String adminCode;
}
