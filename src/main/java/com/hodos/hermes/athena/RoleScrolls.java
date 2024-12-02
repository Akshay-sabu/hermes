package com.hodos.hermes.athena;

import com.hodos.hermes.dao.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleScrolls extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String name);
}
