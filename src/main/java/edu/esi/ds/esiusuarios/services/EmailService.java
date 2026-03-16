package edu.esi.ds.esiusuarios.services;

import org.springframework.stereotype.Service;

@Service
public abstract class EmailService {

    public abstract void sendEmail(String destinatario, Object... params);

}
