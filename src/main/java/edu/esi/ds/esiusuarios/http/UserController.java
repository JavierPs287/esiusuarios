package edu.esi.ds.esiusuarios.http;


import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.esi.ds.esiusuarios.dto.CancelarCuentaRequest;
import edu.esi.ds.esiusuarios.dto.LoginResponse;
import edu.esi.ds.esiusuarios.dto.LogoutRequest;
import edu.esi.ds.esiusuarios.dto.SaveSessionRequest;
import edu.esi.ds.esiusuarios.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/registrar")
    public String registrar(@RequestBody Map<String, String> credentials) {
        return this.service.registrar(credentials);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody Map<String, String> credentials) {
        return this.service.login(credentials);
    }

    @PostMapping("/savesession")
    public String saveSession(@RequestBody SaveSessionRequest request) {
        this.service.saveSession(request);
        return "Session saved";
    }

    @DeleteMapping("/cancelar-cuenta")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarCuenta(@RequestBody CancelarCuentaRequest request) {
        this.service.cancelarCuenta(request);
    }

    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody LogoutRequest request) {
        this.service.logout(request);
    }

    @PostMapping("/recuperar-password")
    public void requestPasswordReset(@RequestBody Map<String, String> request) {
        this.service.requestPasswordReset(request);
    }

    @PostMapping("/reset-password/{token}")
    public void resetPassword(@PathVariable String token, @RequestBody Map<String, String> request) {
        this.service.resetPassword(token, request);
    }
    
    @PostMapping("/validate-token")
    public void validateToken(@RequestBody Map<String, String> request) {
        this.service.isTokenValid(request);
    }

}
