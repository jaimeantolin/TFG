package com.DB_Objects;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class User implements Serializable {
    @Exclude private String id;
    private String fullName, userEmail, isValidated, isAdmin, isPaciente, isCreador;

    public User(){

    }

    public User(String fullName, String email, String isValidated, String isAdmin, String isPaciente, String isCreador) {
        this.fullName = fullName;
        this.userEmail = email;
        this.isValidated = isValidated;
        this.isAdmin = isAdmin;
        this.isPaciente = isPaciente;
        this.isCreador = isCreador;
    }

    public String getfullName() {
        return fullName;
    }

    public void setfullName(String fullName) {
        this.fullName = fullName;
    }

    public String getuserEmail() {
        return userEmail;
    }

    public void setuserEmail(String email) {
        this.userEmail = email;
    }

    public String getIsValidated() {
        return isValidated;
    }

    public void setIsValidated(String isValidated) {
        this.isValidated = isValidated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getIsPaciente() {
        return isPaciente;
    }

    public void setIsPaciente(String isPaciente) {
        this.isPaciente = isPaciente;
    }

    public String getIsCreador() {
        return isCreador;
    }

    public void setIsCreador(String isCreador) {
        this.isCreador = isCreador;
    }
}
