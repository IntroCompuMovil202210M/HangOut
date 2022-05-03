package com.example.proyecto1;

import java.util.ArrayList;

public class MyUser {
    String name;
    String lastName;
    String mail;
    String userName;
    String phoneNumber;
    String password;
    String urlImage;
    ArrayList<Restaurant> favoritesRestaurants  = new ArrayList<>();

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

    public MyUser(String name, String lastName, String mail, String userName, String phoneNumber, String password, String urlImage, ArrayList<Restaurant> favoritesRestaurants) {
        this.name = name;
        this.lastName = lastName;
        this.mail = mail;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.urlImage = urlImage;
        this.favoritesRestaurants = favoritesRestaurants;
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

    public ArrayList<Restaurant> getFavoritesRestaurants() {
        return favoritesRestaurants;
    }

    @Override
    public String toString() {
        return "MyUser{" +
                "mail='" + mail + '\'' +
                ", favoritesRestaurants=" + favoritesRestaurants +
                '}';
    }
}
