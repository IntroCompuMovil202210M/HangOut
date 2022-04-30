package com.example.proyecto1;

public class MyUser {
    String name;
    String lastName;
    String mail;
    String userName;
    String phoneNumber;
    String password;
    String urlImage;

    public MyUser() {
    }

    public MyUser(String name, String lastName, String mail, String userName, String phoneNumber, String password, String urlImage) {
        this.name = name;
        this.lastName = lastName;
        this.mail = mail;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.urlImage = urlImage;
    }

    public String getName() {
        return name;
    }
    public String getLastName() {
        return lastName;
    }
    public String getMail() {
        return mail;
    }
    public String getUserName() {
        return userName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getPassword() {
        return password;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
