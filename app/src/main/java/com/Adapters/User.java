package com.Adapters;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class User implements Serializable {
    @Exclude private String id;
    private String FullName, UserEmail, isValidated;

    public User(){

    }

    public User(String fullName, String email, String isValidated) {
        this.FullName = fullName;
        this.UserEmail = email;
        this.isValidated = isValidated;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        this.FullName = fullName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setEmail(String email) {
        this.UserEmail = email;
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
}
