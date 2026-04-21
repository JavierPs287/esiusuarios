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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        if (!pwd1.equals(pwd2)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Passwords do not match");
        }

        String result = this.service.registrar(nombre, apellidos, email, pwd1);
        if (result == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Registration failed");
        }
        return result;
    }

    @PostMapping("/login")
    public Integer login(@RequestBody Map<String, String> credentials) {
        JSONObject json = new JSONObject(credentials);
        String email = json.optString("email");
        String pwd = json.optString("pwd");

        if (email.isEmpty() || pwd.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return this.service.login(email, pwd);
    }

}
