package edu.esi.ds.esiusuarios.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.esi.ds.esiusuarios.auxiliares.Manager;
import edu.esi.ds.esiusuarios.model.User;

@Service
public class UserService {

    private List<User> users;

    public UserService() {
        this.users = new ArrayList<>();
    }

    public String registrar(String nombre, String apellidos, String email, String contraseña) {
        User newUser = new User(nombre, apellidos, email, contraseña);
        this.users.add(newUser);

        Manager.getInstance().getEmailService().sendEmail(email, 
            "asunto", "Bienvenido a esiusuarios,!",
            "texto", "Bienvenido al sistema."
        );

        return String.valueOf(users.size());
    }

    public String login(String email, String contraseña) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getContraseña().equals(contraseña)) {
                return "Login successful";
            }
        }
        return null;
    }

}
