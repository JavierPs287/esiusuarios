package edu.esi.ds.esiusuarios.dao;

import java.time.LocalDateTime;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.esi.ds.esiusuarios.model.UserSession;

@Repository
public interface UserSessionDAO extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByToken(String token);
    void deleteByUserId(Long userId);
    void deleteByTokenAndUserIdAndEmail(String token, Long userId, String email);
    void deleteByExpiresAtBefore(LocalDateTime now);
}
