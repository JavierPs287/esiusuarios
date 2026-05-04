package edu.esi.ds.esiusuarios.dao;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.esi.ds.esiusuarios.model.UserSession;

@Repository
public interface UserSessionDAO extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByToken(String token);
    
    @Modifying
    @Transactional
    void deleteByUserId(Long userId);
    
    @Modifying
    @Transactional
    void deleteByTokenAndUserIdAndEmail(String token, Long userId, String email);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM UserSession u WHERE u.expiresAt < :now")
    void deleteByExpiresAtBefore(@Param("now") LocalDateTime now);
}
