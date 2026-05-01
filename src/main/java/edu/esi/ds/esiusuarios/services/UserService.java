package edu.esi.ds.esiusuarios.services;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.mail.MessagingException;
import edu.esi.ds.esiusuarios.dao.UserDAO;
import edu.esi.ds.esiusuarios.dao.UserSessionDAO;
import edu.esi.ds.esiusuarios.http.dto.LoginResponse;
import edu.esi.ds.esiusuarios.model.User;
import edu.esi.ds.esiusuarios.model.UserSession;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserSessionDAO userSessionDAO;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private GmailEmailService gmailEmailService;

    public UserService() {
    }

    public String registrar(String nombre, String apellidos, String email, String contraseña) {
        validatorService.validateNombre(nombre);
        validatorService.validateApellidos(apellidos);
        validatorService.validateEmail(email);
        validatorService.validatePassword(contraseña);

        if (userDAO.findByEmail(email).isPresent()) {
            System.err.println("Intento de registro fallido: El email ya está registrado - " + email);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Error en el registro");
        }
        
        String encodedPassword = encoder.encode(contraseña);

        User newUser = new User(nombre, apellidos, email, encodedPassword);
        userDAO.save(newUser);

        System.out.println("Registro exitoso para el email " + email);

        try {
            gmailEmailService.sendWelcomeEmail(email, nombre);
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se ha podido enviar el email de bienvenida", e);
        }

        return String.valueOf(newUser.getId());
    }

    public LoginResponse login(String email, String contraseña) {
        Optional<User> optionalUser = userDAO.findByEmail(email);
        
        if (optionalUser.isEmpty()) {
            System.err.println("Intento de login fallido: Usuario no encontrado para el email " + email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas o ha ocurrido un error");
        }
        
        if (!encoder.matches(contraseña, optionalUser.get().getContraseña())) {
            System.err.println("Intento de login fallido: Contraseña incorrecta para el email " + email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas o ha ocurrido un error");
        }
        
        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();

        System.out.println("Intento de login exitoso para el email " + email);
        return new LoginResponse(token, user.getId(), user.getEmail());
    }

    public void saveSession(String token, Long userId, String email) {
        if (token == null || token.isBlank() || userId == null || email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de sesion incompletos");
        }

        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isEmpty() || !optionalUser.get().getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sesion no valida para el usuario");
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        UserSession session = new UserSession(token, userId, email, expiresAt);
        userSessionDAO.save(session);
    }

    @Transactional
    public void logout(String token, Long userId, String email) {
        if (token == null || token.isBlank() || userId == null || email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de logout incompletos");
        }

        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isEmpty() || !optionalUser.get().getEmail().equalsIgnoreCase(email.trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sesion no valida para el usuario");
        }

        userSessionDAO.deleteByTokenAndUserIdAndEmail(token, userId, email);
    }

    @Transactional
    public void cancelarCuenta(Long userId, String email) {
        if (userId == null || email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de cancelacion incompletos");
        }

        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isEmpty() || !optionalUser.get().getEmail().equalsIgnoreCase(email.trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sesion no valida para el usuario");
        }

        userSessionDAO.deleteByUserId(userId);
        userDAO.deleteById(userId);
    }

}
