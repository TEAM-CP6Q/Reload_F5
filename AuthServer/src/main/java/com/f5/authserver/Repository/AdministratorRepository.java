package com.f5.authserver.Repository;

import com.f5.authserver.Entity.AdministratorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdministratorRepository extends JpaRepository<AdministratorEntity, Long> {
    Optional<AdministratorEntity> findByAdminName(String adminName);
    Optional<AdministratorEntity> findByAdminCode(String adminCode);
    Boolean existsByAdminCode(String adminCode);
}
