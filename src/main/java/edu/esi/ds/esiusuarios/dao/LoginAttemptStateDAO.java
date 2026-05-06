package edu.esi.ds.esiusuarios.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.esi.ds.esiusuarios.model.LoginAttemptState;

@Repository
public interface LoginAttemptStateDAO extends JpaRepository<LoginAttemptState, Long> {
    Optional<LoginAttemptState> findByIpAddress(String ipAddress);
}