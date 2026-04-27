package edu.esi.ds.esiusuarios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "UserSessions")
public class UserSession {

    @Id
    @Column(name = "SessionId", length = 255)
    private String sessionId;

    @Column(name = "UserId", nullable = false)
    private Integer userId;

    @Column(name = "ExpirationTime", nullable = false)
    private LocalDateTime expirationTime;

    public UserSession() {
    }

    public UserSession(String sessionId, Integer userId, LocalDateTime expirationTime) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.expirationTime = expirationTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}
