package edu.esi.ds.esiusuarios.services;

import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.mail.MessagingException;
import edu.esi.ds.esiusuarios.dao.UserDAO;
import edu.esi.ds.esiusuarios.dao.UserSessionDAO;
import edu.esi.ds.esiusuarios.dto.CancelarCuentaRequest;
import edu.esi.ds.esiusuarios.dto.ExternalSessionResponse;
import edu.esi.ds.esiusuarios.dto.LoginResponse;
import edu.esi.ds.esiusuarios.dto.LogoutRequest;
import edu.esi.ds.esiusuarios.dto.SaveSessionRequest;
import edu.esi.ds.esiusuarios.model.User;
import edu.esi.ds.esiusuarios.model.UserSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserSessionDAO userSessionDAO;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private GmailEmailService gmailEmailService;

    public UserService() {
    }

    public String registrar(Map<String, String> credentials) {
        JSONObject json = new JSONObject(credentials);
        String nombre = json.optString("nombre");
        String apellidos = json.optString("apellidos");
        String email = json.optString("email");
        String pwd1 = json.optString("pwd1");
        String pwd2 = json.optString("pwd2");
  
        if (nombre.isEmpty() || apellidos.isEmpty() || email.isEmpty() || pwd1.isEmpty() || pwd2.isEmpty()) {
            logger.error("Intento de registro fallido: Faltan credenciales.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan campos obligatorios");
        }

        if (!pwd1.equals(pwd2)) {
            logger.error("Intento de registro fallido: Las contraseñas no coinciden para {}", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden");
        }

        validatorService.validateNombre(nombre);
        validatorService.validateApellidos(apellidos);
        validatorService.validateEmail(email);
        validatorService.validatePassword(pwd1);
        logger.info("Validación de datos exitosa para el email {}", email);

        if (userDAO.findByEmail(email).isPresent()) {
            logger.error("Intento de registro fallido: El email ya está registrado - {}", email);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Error en el registro");
        }
        
        String encodedPassword = encoder.encode(pwd1);

        User newUser = new User(nombre, apellidos, email, encodedPassword);
        userDAO.save(newUser);

        logger.info("Registro exitoso para el email {}", email);

        try {
            gmailEmailService.sendWelcomeEmail(email, nombre);
        } catch (MessagingException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se ha podido enviar el email de bienvenida", e);
        }

        String result = String.valueOf(newUser.getId());
        if (result == null) {
            logger.error("Intento de registro fallido: Fallo desconocido para {}", email);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error en el registro");
        }
        logger.info("Registro completado con éxito para el email {}. ID de usuario: {}", email, result);
        return result;
    }

    public LoginResponse login(Map<String, String> credentials) {

        JSONObject json = new JSONObject(credentials);
        String email = json.optString("email");
        String pwd = json.optString("pwd");

        if (email.isEmpty() || pwd.isEmpty()) {
            logger.error("Intento de login fallido: Faltan credenciales.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en el inicio de sesión");
        }

        Optional<User> optionalUser = userDAO.findByEmail(email);
        
        if (optionalUser.isEmpty()) {
            logger.error("Intento de login fallido: Usuario no encontrado para el email {}", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en el inicio de sesión");
        }
        
        if (!encoder.matches(pwd, optionalUser.get().getContraseña())) {
            logger.error("Intento de login fallido: Contraseña incorrecta para el email {}", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en el inicio de sesión");
        }
        
        User user = optionalUser.get();
        String token = UUID.randomUUID().toString();

        saveSession(new SaveSessionRequest(token, user.getId(), user.getEmail()));

        logger.info("Intento de login exitoso para el email {}", email);
        return new LoginResponse(token, user.getId(), user.getEmail());
    }

    public void saveSession(SaveSessionRequest request) {
        if (request == null) {
            logger.error("Intento de guardar sesión fallido: Request nulo.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body vacio");
        }
        String token = request.token();
        Long userId = request.userId();
        String email = request.email();

        if (token == null || token.isBlank() || userId == null || email == null || email.isBlank()) {
            logger.error("Intento de guardar sesión fallido: Datos incompletos.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la obtención del token");
        }

        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isEmpty() || !optionalUser.get().getEmail().equalsIgnoreCase(email)) {
            logger.error("Intento de guardar sesión fallido: Usuario no encontrado para el email {}", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la obtención del token");
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);
        UserSession session = new UserSession(token, userId, email, expiresAt);
        userSessionDAO.save(session);
        logger.info("Sesión guardada exitosamente para el email {}. Token: {}", email, token);
    }

    @Transactional
    public void logout(LogoutRequest request) {

        if (request == null || request.token() == null || request.token().isBlank() || request.userId() == null || request.email() == null || request.email().isBlank()) {
            logger.error("Intento de logout fallido: Datos de logout incompletos");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de logout incompletos");
        }

        String token = request.token();
        Long userId = request.userId();
        String email = request.email();

        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isEmpty() || !optionalUser.get().getEmail().equalsIgnoreCase(email.trim())) {
            logger.error("Intento de logout fallido: Usuario no encontrado para el email {}", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en el logout");
        }

        userSessionDAO.deleteByTokenAndUserIdAndEmail(token, userId, email);
        logger.info("Logout exitoso para el email {}.", email);
    }

    public String checkToken(String token) {

        if(token == null || token.isEmpty()) {
            logger.error("Intento de verificación de token fallido: Token no proporcionado.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se necesita token");
        }

        Optional<UserSession> sessionOpt = userSessionDAO.findByToken(token);
        if(sessionOpt.isEmpty() || sessionOpt.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.error("Intento de verificación de token fallido: Token no válido o caducado.");
            return null;
        }

        String username = sessionOpt.get().getEmail();
        if (username == null) {
            logger.error("Intento de verificación de token fallido: Email no válido.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no válido");
        }

        logger.info("Token válido para el email {}.", username);
        return username;
    }

    public ExternalSessionResponse getValidSession(String token) {

        if (token == null || token.isBlank()) {
            logger.error("Intento de obtención de sesión válida fallido: Token no proporcionado.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Se necesita token");
        }

        Optional<UserSession> sessionOpt = userSessionDAO.findByToken(token);
        if (sessionOpt.isEmpty() || sessionOpt.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.error("Intento de obtención de sesión válida fallido: Token no válido o caducado.");
            return null;
        }
        
        UserSession session = sessionOpt.get();
        if (session == null) {
            logger.error("Intento de obtención de sesión válida fallido: Sesión no encontrada.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no valido");
        }

        ExternalSessionResponse response = new ExternalSessionResponse(session.getUserId(), session.getEmail());

        logger.info("Sesión válida obtenida para el email {}.", session.getEmail());
        return response;
    }

    @Transactional
    public void cancelarCuenta(CancelarCuentaRequest request) {

        if (request == null || request.userId() == null || request.email() == null || request.email().isBlank()) {
            logger.error("Intento de cancelación de cuenta fallido: Datos de cancelación incompletos.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de cancelacion incompletos");
        }
        Long userId = request.userId();
        String email = request.email();

        Optional<User> optionalUser = userDAO.findById(userId);
        if (optionalUser.isEmpty() || !optionalUser.get().getEmail().equalsIgnoreCase(email.trim())) {
            logger.error("Intento de cancelación de cuenta fallido: Usuario no encontrado para el email {}", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la cancelación de cuenta");
        }

        logger.info("Cancelación/Borrado de cuenta iniciada para el email {}.", email);
        userSessionDAO.deleteByUserId(userId);
        userDAO.deleteById(userId);
        logger.info("Cuenta cancelada/borrada exitosamente para el email {}.", email);
    }

    @Transactional
    public void requestPasswordReset(Map<String, String> request) {

        String email = request.get("email");
        if (email == null || email.isBlank()) {
            logger.error("Intento de solicitud de restablecimiento de contraseña fallido: Email no proporcionado.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El email es obligatorio");
        }

        Optional<User> optionalUser = userDAO.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            
            // Si ya hay un token y no ha caducado, no envíamos otro correo
            if (user.getResetToken() != null && user.getResetTokenExpiry() != null 
                && user.getResetTokenExpiry().isAfter(LocalDateTime.now())) {
                logger.error("Intento de solicitud de restablecimiento de contraseña fallido: Ya se ha enviado un correo de recuperación válido para el email {}", email);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya se ha enviado un correo de recuperación que todavía es válido.");
            }

            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(10));
            userDAO.save(user);
            logger.info("Token de restablecimiento de contraseña generado para el email {}. Token: {}", email, resetToken);

            try {
                String resetLink = "http://localhost:4200/recuperar-password/" + resetToken;
                gmailEmailService.sendRecoveryEmail(user.getEmail(), user.getNombre(), resetLink);
            } catch (MessagingException e) {
                logger.error("Error al enviar el correo de recuperación: {}", e.getMessage());
            }
        } else {
            logger.error("Intento de solicitud de restablecimiento de contraseña fallido: Usuario no encontrado para el email {}", email);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
    }

    @Transactional
    public void resetPassword(String token, Map<String, String> request) {

        String pwd1 = request.get("pwd1");
        String pwd2 = request.get("pwd2");

        if (pwd1 == null || pwd1.isBlank() || pwd2 == null || pwd2.isBlank()) {
            logger.error("Intento de restablecimiento de contraseña fallido: Faltan campos obligatorios");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Faltan campos obligatorios");
        }

        if (!pwd1.equals(pwd2)) {
            logger.error("Intento de restablecimiento de contraseña fallido: Las contraseñas no coinciden para el token {}", token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden");
        }

        validatorService.validatePassword(pwd1);

        Optional<User> optionalUser = userDAO.findByResetToken(token);
        if (optionalUser.isEmpty() || optionalUser.get().getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            logger.error("Intento de restablecimiento de contraseña fallido: Token inválido o expirado para el token {}", token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token inválido o expirado");
        }

        logger.info("Restablecimiento de contraseña iniciado para el token {}.", token);
        User user = optionalUser.get();
        user.setContraseña(encoder.encode(pwd1));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userDAO.save(user);
        logger.info("Contraseña restablecida exitosamente para el email {}.", user.getEmail());
    }
    
    @Transactional
    public void deleteExpiredTokens() {
    	userSessionDAO.deleteByExpiresAtBefore(LocalDateTime.now());
    }
    
    @Transactional
    public void isTokenValid(Map<String, String> request) {

        String token = request.get("token");
        boolean isValid = true;
        if (token == null || token.isBlank()) {
            logger.error("Intento de validación de token fallido: Token no proporcionado.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token es obligatorio");
        }
        
        deleteExpiredTokens();
        
        Optional<UserSession> session = userSessionDAO.findByToken(token);
        if (session.isEmpty()) {
            logger.error("Intento de validación de token fallido: Token no encontrado.");
            isValid = false;
        }
        
        if (session.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            userSessionDAO.delete(session.get());
            logger.error("Intento de validación de token fallido: Token expirado.");
            isValid = false;
        }

        if (!isValid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o expirado");
        }
    }
}
