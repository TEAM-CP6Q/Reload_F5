package com.f5.authserver.DAO.Administrator;

import com.f5.authserver.DTO.User.AdministratorDTO;
import com.f5.authserver.Entity.AdministratorEntity;
import com.f5.authserver.Repository.AdministratorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AdministratorDAOImpl implements AdministratorDAO {
    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministratorDAOImpl(AdministratorRepository administratorRepository, PasswordEncoder passwordEncoder) {
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<AdministratorEntity> findByAdminName(String adminName) {
        return administratorRepository.findByAdminName(adminName);  // Optional로 엔티티 반환
    }

    @Override
    public Boolean existsByAdminCode(String adminCode) {
        return administratorRepository.existsByAdminCode(adminCode);
    }

    @Override
    public AdministratorDTO addAdministrator(AdministratorDTO administratorDTO) {
        AdministratorEntity administratorEntity = new AdministratorEntity();
        administratorEntity.setAdminName(administratorDTO.getAdminName());
        administratorEntity.setAdminCode(passwordEncoder.encode(administratorDTO.getAdminCode()));  // adminCode를 암호화하여 저장
        administratorRepository.save(administratorEntity);
        return administratorDTO;
    }
}
