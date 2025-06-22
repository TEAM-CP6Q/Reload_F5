package com.f5.authserver.DAO.User;

import com.f5.authserver.DTO.Auth.RegisterDTO;
import com.f5.authserver.DTO.Kakao.IntegrationDTO;
import com.f5.authserver.DTO.Kakao.UserKakaoDTO;
import com.f5.authserver.DTO.User.UserDTO;
import com.f5.authserver.DTO.User.UserDetailDTO;
import com.f5.authserver.Entity.UserEntity;
import com.f5.authserver.Repository.UserRepository;
import com.f5.authserver.Service.Communication.AccountCommunicationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class UserDAOImpl implements UserDAO {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountCommunicationService accountCommunicationService;

    private UserDTO entityToDTO(UserEntity userEntity) {
        return UserDTO.builder()
                .email(userEntity.getEmail())
                .password(passwordEncoder.encode(userEntity.getPassword()))
                .build();
    }

    @Override
    public UserDTO save(RegisterDTO registerDTO) {
        try {
            UserEntity userEntity = UserEntity.builder()
                    .email(registerDTO.getEmail())
                    .password(passwordEncoder.encode(registerDTO.getPassword()))
                    .kakao(false)
                    .role("User")
                    .build();
            userRepository.save(userEntity);
            UserDetailDTO userDetailDTO = UserDetailDTO.builder()
                    .id(userEntity.getId())
                    .name(registerDTO.getName())
                    .postalCode(registerDTO.getPostalCode())
                    .roadNameAddress(registerDTO.getRoadNameAddress())
                    .detailedAddress(registerDTO.getDetailedAddress())
                    .phoneNumber(registerDTO.getPhoneNumber())
                    .build();
            accountCommunicationService.registerAccount(userDetailDTO);
            return entityToDTO(userEntity);
        } catch (Exception e) {
            throw new IllegalStateException("잘못된 형식의 요청.", e);
        }
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDTO getByEmail(String email) {
        try {
            UserEntity user = userRepository.getByEmail(email);
            return UserDTO.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .build();
        } catch (Exception e){
            throw new IllegalStateException("해당 사용자 이름을 찾을 수 없습니다.", e);
        }
    }

    @Override
    public UserKakaoDTO getKakaoByEmail(String email) {
        try{
            UserEntity user = userRepository.getByEmail(email);
            return UserKakaoDTO.builder()
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .kakao(user.getKakao())
                    .build();
        } catch (Exception e){
            throw new IllegalStateException("해당 사용자를 찾을 수 없습니다.");
        }
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);  // Optional로 엔티티 반환
    }


    @Override
    public void deleteAccount(UserDTO userDTO) {
        try {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                UserEntity user = userRepository.getByEmail(userDTO.getEmail());
                accountCommunicationService.deleteAccount(user.getId());
                userRepository.delete(user); // 실제 삭제 로직 확인 필요
            } else {
                throw new IllegalArgumentException("해당 이메일이 없음");
            }
        } catch (Exception e) {
            throw new IllegalStateException("삭제에 실패 하였습니다.", e);
        }
    }

    @Override
    public Long getId(String email){
        return userRepository.getByEmail(email).getId();
    }

    @Override
    public void integrationInfo(IntegrationDTO integrationDTO) {
        try {
            UserEntity userEntity = userRepository.getByEmail(integrationDTO.getEmail());
            userEntity.setEmail(integrationDTO.getEmail());
            userEntity.setPassword(passwordEncoder.encode(integrationDTO.getUserId()));
            userEntity.setKakao(true);
            userRepository.save(userEntity);
        } catch (Exception e) {
            throw new IllegalStateException("통합에 실패 하였습니다.");
        }
    }

    @Override
    public UserDTO kakaoSave(RegisterDTO registerDTO) throws IllegalArgumentException{
        UserEntity userEntity = UserEntity.builder()
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .kakao(true)
                .role("User")
                .build();
        userRepository.save(userEntity);
        UserDetailDTO userDetailDTO = UserDetailDTO.builder()
                .id(userEntity.getId())
                .name(registerDTO.getName())
                .postalCode(registerDTO.getPostalCode())
                .roadNameAddress(registerDTO.getRoadNameAddress())
                .detailedAddress(registerDTO.getDetailedAddress())
                .phoneNumber(registerDTO.getPhoneNumber())
                .build();
        accountCommunicationService.registerAccount(userDetailDTO);
        return entityToDTO(userEntity);
    }

    @Override
    public String getEmail(Long id) throws IllegalArgumentException {
        return userRepository.getEmailById(id);
    }

    @Override
    public UserDTO saveDeliver(UserDTO userDTO) {
        try{
            UserEntity userEntity = UserEntity.builder()
                    .email(userDTO.getEmail())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .kakao(false)
                    .role("Deliver")
                    .build();
            userRepository.save(userEntity);
            return entityToDTO(userEntity);
        } catch (Exception e) {
            throw new IllegalArgumentException("배송기사 저장 실패");
        }
    }
}
