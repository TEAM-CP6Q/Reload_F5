package com.f5.authserver.DAO.Administrator;

import com.f5.authserver.DTO.User.AdministratorDTO;
import com.f5.authserver.Entity.AdministratorEntity;

import java.util.Optional;

public interface AdministratorDAO {
    Optional<AdministratorEntity> findByAdminName(String adminName);
    Boolean existsByAdminCode(String adminCode);
    AdministratorDTO addAdministrator(AdministratorDTO administratorDTO);
}
