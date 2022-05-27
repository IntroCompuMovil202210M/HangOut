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

    public void setFoto(Byte[] foto) {
        this.foto = foto;
    }

}
