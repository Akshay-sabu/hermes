package com.hodos.hermes.athena;

import com.hodos.hermes.dao.OTPDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPScrolls extends JpaRepository<OTPDao,Long> {
}
