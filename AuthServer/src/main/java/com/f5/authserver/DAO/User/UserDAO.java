package com.f5.authserver.DAO.User;

import com.f5.authserver.DTO.Kakao.IntegrationDTO;
import com.f5.authserver.DTO.Auth.RegisterDTO;
import com.f5.authserver.DTO.User.UserDTO;
import com.f5.authserver.DTO.Kakao.UserKakaoDTO;
import com.f5.authserver.Entity.UserEntity;

import java.util.Optional;

public interface UserDAO {
    UserDTO save(RegisterDTO registerDTO);
    Boolean existsByEmail(String email);
    UserDTO getByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
    void deleteAccount(UserDTO userDTO);
    Long getId(String username);
    UserKakaoDTO getKakaoByEmail(String email);
    void integrationInfo(IntegrationDTO integrationDTO);
    UserDTO kakaoSave(RegisterDTO registerDTO) throws IllegalArgumentException;
    String getEmail(Long id);
    UserDTO saveDeliver(UserDTO userDTO);
}
