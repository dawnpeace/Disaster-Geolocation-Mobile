package com.example.disastergeolocation.ErrorModel;

public class RegisterForm {
    private String[] email;
    private String[] password;
    private String[] phone;
    private String[] gender;
    private String[] photo;
    private String[] name;
    private String[] identity_number;
    private String[] address;

    public RegisterForm(String[] email, String[] password, String[] phone, String[] gender, String[] photo, String[] name, String[] identity_number, String[] address) {
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.gender = gender;
        this.photo = photo;
        this.name = name;
        this.identity_number = identity_number;
        this.address = address;
    }

    public String[] getEmail() {
        return email;
    }

    public String[] getPassword() {
        return password;
    }

    public String[] getPhone() {
        return phone;
    }

    public String[] getGender() {
        return gender;
    }

    public String[] getPhoto() {
        return photo;
    }

    public String[] getName() {
        return name;
    }

    public String[] getIdentity_number() {
        return identity_number;
    }

    public String[] getAddress() {
        return address;
    }
}
