package edu.esi.ds.esiusuarios.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ValidatorService {

    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña no puede estar vacía");
        }
        if (password.length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe tener al menos 8 caracteres");
        }
        if (password.length() > 256) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña no puede superar los 256 caracteres");
        }
        if (!password.matches(".*\\d.*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe contener al menos un numero");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe contener al menos una letra mayúscula");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe contener al menos una letra minúscula");
        }
        if (!password.matches(".*[^a-zA-Z0-9].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña debe contener al menos un símbolo especial");
        }
    }

    public void validateNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede estar vacío");
        }
        if (nombre.length() > 32) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre no puede superar los 32 caracteres");
        }
    }

    public void validateApellidos(String apellidos) {
        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los apellidos no pueden estar vacíos");
        }
        if (apellidos.length() > 64) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los apellidos no pueden superar los 64 caracteres");
        }
    }

    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email no puede estar vacío");
        }
        if (email.length() > 256) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email no puede superar los 256 caracteres");
        }
    }
}
