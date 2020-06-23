package com.example.firebasee.model;

public class User {
    private String name,email,username,id;

    public User() {
    }

    public User(String name, String email, String username, String id) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
