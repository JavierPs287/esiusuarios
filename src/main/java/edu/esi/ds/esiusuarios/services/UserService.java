package edu.esi.ds.esiusuarios.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.esi.ds.esiusuarios.auxiliares.Manager;
import edu.esi.ds.esiusuarios.dao.UserDAO;
import edu.esi.ds.esiusuarios.model.User;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    @Autowired
    private ValidatorService validatorService;

    public UserService() {
    }

    public String registrar(String nombre, String apellidos, String email, String contraseña) {
        validatorService.validateNombre(nombre);
        validatorService.validateApellidos(apellidos);
        validatorService.validateEmail(email);
        validatorService.validatePassword(contraseña);

        if (userDAO.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está registrado");
        }
        
        String encodedPassword = encoder.encode(contraseña);

        User newUser = new User(nombre, apellidos, email, encodedPassword);
        userDAO.save(newUser);

        Manager.getInstance().getEmailService().sendEmail(email, 
            "asunto", "Bienvenido a esiusuarios,!",
            "texto", "Bienvenido al sistema."
        );

        return String.valueOf(newUser.getId());
    }

    public String login(String email, String contraseña) {
        Optional<User> optionalUser = userDAO.findByEmail(email);
        
        if (optionalUser.isPresent() && encoder.matches(contraseña, optionalUser.get().getContraseña())) {
            return "Login successful";
        }
        
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
    }

}
