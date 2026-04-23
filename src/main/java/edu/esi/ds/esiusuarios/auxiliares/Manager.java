package edu.esi.ds.esiusuarios.auxiliares;


import edu.esi.ds.esiusuarios.services.GmailEmailService;

public class Manager {

    private static Manager instance;
    private GmailEmailService emailService;

    private Manager() {
        this.emailService = new GmailEmailService("esiusuarios@gmail.com", "your-app-password");
    }

    public synchronized static Manager getInstance() {
        if (instance == null) {
            instance = new Manager();
        }
        return instance;
    }

    public GmailEmailService getEmailService() {
        return this.emailService;
    }

}
