package com.f5.authserver.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DormantEntity {
    @jakarta.persistence.Id
    @Column(unique = true, nullable = false)
    private Long Id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private LocalDate dormantDate;
}