package com.f5.authserver.Repository;

import com.f5.authserver.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Boolean existsByEmail(String username);
    Optional<UserEntity> findByEmail(String email);
    UserEntity getByEmail(String email);

    @Query("SELECT u.email FROM UserEntity u WHERE u.id = :id")
    String getEmailById(@Param("id") Long id);
}