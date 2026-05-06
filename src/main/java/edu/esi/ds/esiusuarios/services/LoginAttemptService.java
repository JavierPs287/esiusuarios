package edu.esi.ds.esiusuarios.services;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import edu.esi.ds.esiusuarios.dao.LoginAttemptStateDAO;
import edu.esi.ds.esiusuarios.model.LoginAttemptState;

@Service
public class LoginAttemptService {

    private static final int ATTEMPTS_PER_BAN = 3;
    private static final long INITIAL_BAN_SECONDS = 15L;
    private static final long MAX_BAN_SECONDS = 15L * 60L;

    @Autowired
    private LoginAttemptStateDAO loginAttemptStateDAO;

    public void ensureLoginAllowed(String ipAddress) {
        LoginAttemptState state = loginAttemptStateDAO.findByIpAddress(ipAddress).orElse(null);
        if (state == null || state.getBannedUntil() == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (state.getBannedUntil().isAfter(now)) {
            long remainingSeconds = Duration.between(now, state.getBannedUntil()).toSeconds();
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Demasiados intentos desde esta IP. Prueba de nuevo en " + Math.max(1L, remainingSeconds) + " segundos.");
        }
    }

    @Transactional
    public void registerSuccessfulLogin(String ipAddress) {
        loginAttemptStateDAO.findByIpAddress(ipAddress).ifPresent(loginAttemptStateDAO::delete);
    }

    @Transactional
    public void registerFailedLogin(String ipAddress) {
        LocalDateTime now = LocalDateTime.now();
        LoginAttemptState state = loginAttemptStateDAO.findByIpAddress(ipAddress)
                .orElseGet(() -> new LoginAttemptState(ipAddress));

        if (state.getBannedUntil() != null && !state.getBannedUntil().isAfter(now)) {
            state.setBannedUntil(null);
        }

        state.setFailedAttempts(state.getFailedAttempts() + 1);
        state.setLastFailureAt(now);

        if (state.getFailedAttempts() >= ATTEMPTS_PER_BAN && state.getFailedAttempts() % ATTEMPTS_PER_BAN == 0) {
            state.setBanLevel(state.getBanLevel() + 1);
            state.setFailedAttempts(0);
            state.setBannedUntil(now.plusSeconds(calculateBanSeconds(state.getBanLevel())));
        }

        state.setUpdatedAt(now);
        loginAttemptStateDAO.save(state);
    }

    private long calculateBanSeconds(int banLevel) {
        long banSeconds = INITIAL_BAN_SECONDS * (1L << Math.max(0, banLevel - 1));
        return Math.min(banSeconds, MAX_BAN_SECONDS);
    }
}