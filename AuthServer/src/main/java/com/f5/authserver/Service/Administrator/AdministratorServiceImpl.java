package com.f5.authserver.Service.Administrator;

import com.f5.authserver.DAO.Administrator.AdministratorDAO;
import com.f5.authserver.DTO.User.AdministratorDTO;
import com.f5.authserver.Entity.AdministratorEntity;
import com.f5.authserver.Repository.AdministratorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdministratorServiceImpl implements AdministratorService {
    private final AdministratorDAO administratorDAO;
    private final AdministratorRepository administratorRepository;

    @Override
    public AdministratorEntity getLoggedInAdministratorEntity(String adminName) {
        return administratorDAO.findByAdminName(adminName)
                .orElseThrow(() -> new IllegalArgumentException("해당 어드민을 찾을 수 없습니다."));
    }

    @Override
    public AdministratorDTO addAdministrator(AdministratorDTO administratorDTO) {
        if(administratorDAO.existsByAdminCode(administratorDTO.getAdminCode())) {
            throw new IllegalArgumentException("중복된 어드민 코드");
        }
        return administratorDAO.addAdministrator(administratorDTO);
    }
}
