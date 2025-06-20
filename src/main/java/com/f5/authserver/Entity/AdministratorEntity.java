package com.f5.authserver.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "administrator")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdministratorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long Id;

    @Column(unique = true, nullable = false)
    private String adminName;

    @Column(unique = true, nullable = false)
    private String adminCode;
}
