package com.siyararslan.socialmediaapp;

public class User {
    private String email;
    private String password;
    private String profilePicture;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public User(String email, String password, String profilePicture) {
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
    }

    public User(String email,  String profilePicture) {
        this.email = email;

        this.profilePicture = profilePicture;
    }
}
