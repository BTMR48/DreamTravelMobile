package com.tbmr.dreamtravel.models;

// User.java
public class User {
    private String id;
    private String nic;
    private int role;
    private String password;
    private String email;
    private boolean isActive;

    public User(String id, String nic, int role, String password, String email, boolean isActive) {
        this.id = id;
        this.nic = nic;
        this.role = role;
        this.password = password;
        this.email = email;
        this.isActive = isActive;
    }

}
