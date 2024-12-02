package com.hodos.hermes.athena;

import com.hodos.hermes.dao.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserScrolls extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
}
