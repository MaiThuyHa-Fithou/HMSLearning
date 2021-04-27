package com.mtha.findmyfriends.data.model;


/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String email;
    private String password;
    private double latitude, longitude;
    private String img;
    private String username;

    public LoggedInUser() {
    }

    public LoggedInUser(String email, String password, double latitude, double longitude, String img, String username) {
        this.email = email;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.img = img;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getImg() {
        return img;
    }

    public String getUsername() {
        return username;
    }
}