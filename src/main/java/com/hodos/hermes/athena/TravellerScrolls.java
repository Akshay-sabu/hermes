package com.hodos.hermes.athena;

import com.hodos.hermes.dao.user.Traveller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TravellerScrolls extends JpaRepository<Traveller,Long> {
    Optional<Traveller> findByEmail(String email);
}
