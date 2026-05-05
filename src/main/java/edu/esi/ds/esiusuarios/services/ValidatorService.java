package edu.esi.ds.esiusuarios.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ValidatorService {

    private static final Logger logger = LoggerFactory.getLogger(ValidatorService.class);
    
    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            logger.error("La contraseña no puede estar vacía");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña no puede estar vacía");
        }
        if (password.length() < 8) {
            logger.error("La contraseña debe tener al menos 8 caracteres");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres");
        }
        if (password.length() > 256) {
            logger.error("La contraseña no puede superar los 256 caracteres");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña no puede superar los 256 caracteres");
        }
        if (!password.matches(".*\\d.*")) {
            logger.error("La contraseña debe contener al menos un numero");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe contener al menos un numero");
        }
        if (!password.matches(".*[A-Z].*")) {
            logger.error("La contraseña debe contener al menos una letra mayúscula");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe contener al menos una letra mayúscula");
        }
        if (!password.matches(".*[a-z].*")) {
            logger.error("La contraseña debe contener al menos una letra minúscula");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe contener al menos una letra minúscula");
        }
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            logger.error("La contraseña debe contener al menos un símbolo especial");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe contener al menos un símbolo especial");
        }
    }

    public void validateNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            logger.error("El nombre no puede estar vacío");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío");
        }
        if (nombre.length() > 32) {
            logger.error("El nombre no puede superar los 32 caracteres");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede superar los 32 caracteres");
        }
    }

    public void validateApellidos(String apellidos) {
        if (apellidos == null || apellidos.trim().isEmpty()) {
            logger.error("Los apellidos no pueden estar vacíos");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los apellidos no pueden estar vacíos");
        }
        if (apellidos.length() > 64) {
            logger.error("Los apellidos no pueden superar los 64 caracteres");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los apellidos no pueden superar los 64 caracteres");
        }
    }

    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            logger.error("El email no puede estar vacío");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email no puede estar vacío");
        }
        if (email.length() > 256) {
            logger.error("El email no puede superar los 256 caracteres");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email no puede superar los 256 caracteres");
        }
    }
}
