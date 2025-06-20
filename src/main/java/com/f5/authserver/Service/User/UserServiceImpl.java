package com.f5.authserver.Service.User;

import com.f5.authserver.DAO.User.UserDAO;
import com.f5.authserver.DTO.Auth.RegisterDTO;
import com.f5.authserver.DTO.User.UserDTO;
import com.f5.authserver.Entity.UserEntity;
import com.f5.authserver.Service.Communication.AccountCommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    @Override
    public UserDTO registerUser(RegisterDTO registerDTO) {
        if(userDAO.existsByEmail(registerDTO.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        return userDAO.save(registerDTO);
    }

    @Override
    public UserDTO registerKakaoUser(RegisterDTO registerDTO){
        if(userDAO.existsByEmail(registerDTO.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 계정입니다. 통합을 진행해주세요");
        }
        return userDAO.kakaoSave(registerDTO);
    }

    @Override
    public UserDTO getByEmail(String email) {
        return userDAO.getByEmail(email);
    }

    @Override
    public Long getIdByEmail(String email) {
        return userDAO.getId(email);
    }

    @Override
    public String getEmailById(Long id){
        return userDAO.getEmail(id);
    }

    @Override
    public UserEntity getLoggedInUserEntity(String email) {
        return userDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 찾을 수 없습니다."));
    }

    @Override
    public void deleteAccount(UserDTO user) {
        try{
            userDAO.deleteAccount(user);
        } catch (Exception e){
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public UserDTO registerDeliver(UserDTO userDTO) {
        try{
            return userDAO.saveDeliver(userDTO);
        } catch (Exception e){
            throw new IllegalArgumentException(e);
        }
    }
}
