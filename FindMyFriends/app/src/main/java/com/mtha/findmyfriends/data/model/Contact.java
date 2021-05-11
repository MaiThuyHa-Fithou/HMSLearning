package com.mtha.findmyfriends.data.model;

import com.huawei.hms.maps.model.LatLng;

public class Contact {

    private String fullName;
    private String phone;
    private String email;
    private double latitude, longitude;
    private String image;

    public Contact() {
    }

    public Contact(String fullName, String phoneNumb, String email) {
        this.fullName = fullName;
        this.phone = phoneNumb;
        this.email = email;

    }

    public Contact(String fullName, String phoneNumb, String email, double latitude, double longitude) {
        this.fullName = fullName;
        this.phone = phoneNumb;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Contact(String fullName, String phoneNumb, String email, String image,double latitude, double longitude) {
        this.fullName = fullName;
        this.phone = phoneNumb;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "fullName='" + fullName + '\'' +
                ", phoneNumb='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
