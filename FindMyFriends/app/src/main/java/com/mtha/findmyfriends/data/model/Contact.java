package com.mtha.findmyfriends.data.model;

public class Contact {
    private String fullName;
    private String phoneNumb;
    private String email;
    private String address;

    public Contact() {
    }

    public Contact(String fullName, String phoneNumb, String email, String address) {
        this.fullName = fullName;
        this.phoneNumb = phoneNumb;
        this.email = email;
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumb() {
        return phoneNumb;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "fullName='" + fullName + '\'' +
                ", phoneNumb='" + phoneNumb + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
