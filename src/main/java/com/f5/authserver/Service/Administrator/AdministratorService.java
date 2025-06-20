package com.f5.authserver.Service.Administrator;

import com.f5.authserver.DTO.User.AdministratorDTO;
import com.f5.authserver.Entity.AdministratorEntity;

public interface AdministratorService {
    AdministratorEntity getLoggedInAdministratorEntity(String adminName);
    AdministratorDTO addAdministrator(AdministratorDTO administratorDTO);
}
