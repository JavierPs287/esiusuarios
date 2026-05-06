package edu.esi.ds.esiusuarios.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "login_attempt_state")
public class LoginAttemptState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address", length = 45, nullable = false, unique = true)
    private String ipAddress;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(name = "ban_level", nullable = false)
    private int banLevel;

    @Column(name = "banned_until")
    private LocalDateTime bannedUntil;

    @Column(name = "last_failure_at")
    private LocalDateTime lastFailureAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public LoginAttemptState() {
    }

    public LoginAttemptState(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public int getBanLevel() {
        return banLevel;
    }

    public void setBanLevel(int banLevel) {
        this.banLevel = banLevel;
    }

    public LocalDateTime getBannedUntil() {
        return bannedUntil;
    }

    public void setBannedUntil(LocalDateTime bannedUntil) {
        this.bannedUntil = bannedUntil;
    }

    public LocalDateTime getLastFailureAt() {
        return lastFailureAt;
    }

    public void setLastFailureAt(LocalDateTime lastFailureAt) {
        this.lastFailureAt = lastFailureAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}