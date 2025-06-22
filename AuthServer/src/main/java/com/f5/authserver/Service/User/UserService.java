package com.f5.authserver.Service.User;

import com.f5.authserver.DTO.Auth.RegisterDTO;
import com.f5.authserver.DTO.User.UserDTO;
import com.f5.authserver.Entity.UserEntity;

public interface UserService {
    UserDTO registerUser(RegisterDTO registerDTO);
    UserDTO registerKakaoUser(RegisterDTO registerDTO);
    Long getIdByEmail(String email);
    UserEntity getLoggedInUserEntity(String email);
    void deleteAccount(UserDTO user);
    UserDTO getByEmail(String email);
    String getEmailById(Long id);
    UserDTO registerDeliver(UserDTO userDTO);
}
