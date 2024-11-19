package com.hodos.hermes.athena;

import com.hodos.hermes.dao.Traveller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravellerScrolls extends JpaRepository<Traveller,Long> {
}
