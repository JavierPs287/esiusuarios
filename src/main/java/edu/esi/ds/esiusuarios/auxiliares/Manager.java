package edu.esi.ds.esiusuarios.auxiliares;

import edu.esi.ds.esiusuarios.services.EmailService;
import edu.esi.ds.esiusuarios.services.EmailServiceFalso;

public class Manager {

    private static Manager instance;
    private EmailServiceFalso emailService;

    private Manager() {
        this.emailService = new EmailServiceFalso();
    }

    public synchronized static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    public EmailService getEmailService() {
        return this.emailService;
    }

}
