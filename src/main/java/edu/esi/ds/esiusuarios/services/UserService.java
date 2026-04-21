package edu.esi.ds.esiusuarios.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import edu.esi.ds.esiusuarios.auxiliares.Manager;
import edu.esi.ds.esiusuarios.model.User;

@Service
public class UserService {

    private List<User> users = new ArrayList<>();
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    @Autowired
    private ValidatorService validatorService;

    public UserService() {
    }

    public String registrar(String nombre, String apellidos, String email, String contraseña) {
        validatorService.validatePassword(contraseña);
        
        String encodedPassword = encoder.encode(contraseña);

        User newUser = new User(nombre, apellidos, email, encodedPassword);
        this.users.add(newUser);

        Manager.getInstance().getEmailService().sendEmail(email, 
            "asunto", "Bienvenido a esiusuarios,!",
            "texto", "Bienvenido al sistema."
        );

        return String.valueOf(users.size());
    }

    public String login(String email, String contraseña) {
        for (User user : users) {
            if (user.getEmail().equals(email) && encoder.matches(contraseña, user.getContraseña())) {
                return "Login successful";
            }
        }
        return "Login failed";
    }

}
