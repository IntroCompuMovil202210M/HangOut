package com.example.proyecto1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyUser {
    String name;
    String lastName;
    String mail;
    String userName;
    String phoneNumber;
    String password;
    String ID;
    private Byte[] foto;
    private String latitude;
    private String longitude;
    private boolean disponible;
    private String key;

    public MyUser() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    public MyUser(String name, String lastName, String mail, String userName, String phoneNumber, String password) {
        this.name = name;
        this.lastName = lastName;
        this.mail = mail;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
    public MyUser(String key, String name, String apellido, String id, String latitude, String longitude, boolean disponible){
        this.key = key;
        this.name = name;
        this.lastName = apellido;
        this.ID = id;
        this.latitude= latitude;
        this.longitude = longitude;
        this.disponible = disponible;
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
    public Byte[] getFoto() {
        return foto;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setFoto(Byte[] foto) {
        this.foto = foto;
    }

}
