package com.example.chatapp.Model;

public class User {

    private String username;
    private String email;
    private String id;
    private String imageURL;

    public User(String username, String email, String id, String imageURL) {
        this.username = username;
        this.email = email;
        this.id = id;
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return imageURL;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
