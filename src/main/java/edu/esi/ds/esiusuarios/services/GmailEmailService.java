package edu.esi.ds.esiusuarios.services;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class GmailEmailService {

    private final String username;
    private final String appPassword;

    public GmailEmailService(
        @Value("${mail.gmail.username}") String username,
        @Value("${mail.gmail.app-password}") String appPassword
    ) {
        this.username = username;
        this.appPassword = appPassword;
    }

    

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, appPassword);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject, "UTF-8");
        message.setContent(htmlContent, "text/html; charset=UTF-8");

        Transport.send(message);
    }

    public void sendWelcomeEmail(String to, String nombre) throws MessagingException {
        String subject = "Bienvenido a ESIUsuarios";
        String nombreApp = "ESI-ENTRADAS";
        String urlLogin = "http://localhost:4200/login";

        String htmlContent = """
                <html>
                    <body style="margin:0; padding:0; background-color:#d8cfbd; font-family: Arial, sans-serif;">

                        <div style="background-color:#1f2a3a; padding:15px; text-align:center;">
                            <h1 style="color:#ffffff; margin:0; font-size:20px;">
                                %s
                            </h1>
                        </div>


                        <div style="padding:40px 20px; text-align:center;">

                            <h2 style="color:#000; font-size:28px; margin-bottom:10px;">
                                Bienvenido, %s
                            </h2>

                            <p style="color:#333; font-size:16px;">
                                Tu cuenta se ha creado correctamente.
                            </p>

                            <p style="color:#333; font-size:16px; margin-bottom:30px;">
                                Ya puedes iniciar sesión y empezar a usar la plataforma.
                            </p>

                            <a href="%s"
                               style="
                                    display:inline-block;
                                    padding:14px 28px;
                                    font-size:16px;
                                    font-weight:bold;
                                    color:#000;
                                    text-decoration:none;
                                    border-radius:30px;
                                    background: linear-gradient(90deg, #ff6a2b, #f7a53b);
                                    box-shadow: 0px 4px 10px rgba(0,0,0,0.2);
                               ">
                               Iniciar sesión
                            </a>

                        </div>

                        <div style="text-align:center; padding:20px; font-size:12px; color:#555;">
                            © %s - Todos los derechos reservados
                        </div>

                    </body>
                </html>
                """.formatted(nombreApp, nombre, urlLogin, nombreApp);

        sendHtmlEmail(to, subject, htmlContent);
    }
}
