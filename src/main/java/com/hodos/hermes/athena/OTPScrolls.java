package com.hodos.hermes.athena;

import com.hodos.hermes.dao.user.OTPDao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPScrolls extends JpaRepository<OTPDao,Long> {
    Optional<OTPDao> findByEmail(String email);
}
