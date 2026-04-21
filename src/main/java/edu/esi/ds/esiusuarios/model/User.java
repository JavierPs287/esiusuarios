package edu.esi.ds.esiusuarios.model;

public class User {

    private String nombre;
    private String apellidos;
    private String contraseña;
    private String email;

    public User(String nombre, String apellidos, String email, String contraseña) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.contraseña = contraseña;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos){
        this.apellidos = apellidos;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
