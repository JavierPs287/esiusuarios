package edu.esi.ds.esiusuarios.http;


import java.util.Map;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.esi.ds.esiusuarios.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/registrar")
    public String registrar(@RequestBody Map<String, String> credentials) {
        JSONObject json = new JSONObject(credentials);
        String nombre = json.optString("nombre");
        String apellidos = json.optString("apellidos");
        String email = json.optString("email");
        String pwd1 = json.optString("pwd1");
        String pwd2 = json.optString("pwd2");

        if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || pwd1.isEmpty() || pwd2.isEmpty()) {
            System.err.println("Intento de registro fallido: Faltan credenciales.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan campos obligatorios");
        }

        if (!pwd1.equals(pwd2)) {
            System.err.println("Intento de registro fallido: Las contraseñas no coinciden para " + email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden");
        }

        String result = this.service.registrar(nombre, apellidos, email, pwd1);
        if (result == null) {
            System.err.println("Intento de registro fallido: Fallo desconocido para " + email);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el registro");
        }
        return result;
    }

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> credentials) {
        JSONObject json = new JSONObject(credentials);
        String email = json.optString("email");
        String pwd = json.optString("pwd");

        if (email.isEmpty() || pwd.isEmpty()) {
            System.err.println("Intento de login fallido: Faltan credenciales.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en el inicio de sesión");
        }

        String result = this.service.login(email, pwd);
        return result;
    }

}
