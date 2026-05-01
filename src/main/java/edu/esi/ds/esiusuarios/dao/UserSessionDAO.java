package edu.esi.ds.esiusuarios.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.esi.ds.esiusuarios.model.UserSession;

@Repository
public interface UserSessionDAO extends JpaRepository<UserSession, Long> {
    void deleteByUserId(Long userId);
}
