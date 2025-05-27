package com.example.cooperadora_escuela.network.auth;

import com.example.cooperadora_escuela.models.User;

public class LoginResponse {
    //representa la respuesta que te devuelve el backend despues del login
    private String refresh;
    private String access;
    private User user;

    public String getRefresh() {
        return refresh;
    }

    public String getAccess() {
        return access;
    }

    public User getUser() {
        return user;
    }
}
