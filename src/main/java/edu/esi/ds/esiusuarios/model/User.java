package edu.esi.ds.esiusuarios.model;

public class User {

    private String nombre;
    private String contraseña;
    private String token;

    public User(String nombre, String contraseña, String token) {
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.token = token;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    

}
