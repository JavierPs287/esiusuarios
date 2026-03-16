package edu.esi.ds.esiusuarios.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.esi.ds.esiusuarios.auxiliares.Manager;
import edu.esi.ds.esiusuarios.model.User;

@Service
public class UserService {

    private List<User> users;

    public UserService() {
        this.users = List.of(new User("Pepe","pepe123", "1234"), 
                            new User("María","maria456", "5678"));

    }

    public String registrar(String email, String contraseña) {
        User newUser = new User(email, contraseña, String.valueOf(users.size() + 1));
        this.users.add(newUser);

        Manager.getInstance().getEmailService().sendEmail(email, 
            "asunto", "Bienvenido a esiusuarios,!",
            "texto", "Bienvenido al sistema, confirma tu usuario en  http://localhost:8080/users/confirmar?token=" + newUser.getToken()
        );

        return String.valueOf(users.size());
    }

    public String login(String nombre, String contraseña) {
        for (User user : users) {
            if (user.getNombre().equals(nombre) && user.getContraseña().equals(contraseña)) {
                return "Login successful";
            }
        }
        return null;
    }

    public String checkToken(String token) {
        for (User user : users) {
            if (user.getToken().equals(token)) {
                return user.getNombre();
            }
        }
        return null;
        
    }

}
