package edu.esi.ds.esiusuarios.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.mail.MessagingException;
import edu.esi.ds.esiusuarios.dao.UserDAO;
import edu.esi.ds.esiusuarios.model.User;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

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

    public String login(String email, String contraseña) {
        Optional<User> optionalUser = userDAO.findByEmail(email);
        
        if (optionalUser.isEmpty()) {
            System.err.println("Intento de login fallido: Usuario no encontrado para el email " + email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas o ha ocurrido un error");
        }
        
        if (!encoder.matches(contraseña, optionalUser.get().getContraseña())) {
            System.err.println("Intento de login fallido: Contraseña incorrecta para el email " + email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas o ha ocurrido un error");
        }
        
        System.out.println("Intento de login exitoso para el email " + email);
        return "Login successful";
    }

}
