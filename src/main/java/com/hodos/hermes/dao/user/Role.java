package com.hodos.hermes.dao.user;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "name", unique = true)
    private String name; // This will store the role name, e.g., "USER", "ADMIN"
}
