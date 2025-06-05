package com.example.cooperadora_escuela.network.profile;

public class ProfileRequest {
    private String first_name;
    private String last_name;

    private String email;
    private String dni;
    private String shift;
    private String grade_year;
    private String telephone;

    public ProfileRequest(String first_name, String last_name,String email ,String dni, String shift, String grade_year, String telephone) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email=email;
        this.dni = dni;
        this.shift = shift;
        this.grade_year = grade_year;
        this.telephone = telephone;
    }
}
