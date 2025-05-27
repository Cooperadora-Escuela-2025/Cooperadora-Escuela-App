package com.example.cooperadora_escuela.network.auth;

public class LoginRequest {
    //Los datos que se va a enviar al backend cuando el usuario quiera iniciar sesion
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
