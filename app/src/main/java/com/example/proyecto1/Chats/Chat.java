package com.example.proyecto1.Chats;

import com.example.proyecto1.MyUser;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private String primerUsuario;
    private String segundoUsuario;
    private ArrayList<Mensaje> mensajes;
    private String key;

    public Chat(String primerUsuario, String segundoUsuario,String key) {
        this.primerUsuario = primerUsuario;
        this.segundoUsuario = segundoUsuario;
        this.mensajes = new ArrayList<>();
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPrimerUsuario() {
        return primerUsuario;
    }

    public void setPrimerUsuario(String primerUsuario) {
        this.primerUsuario = primerUsuario;
    }

    public String getSegundoUsuario() {
        return segundoUsuario;
    }

    public void setSegundoUsuario(String segundoUsuario) {
        this.segundoUsuario = segundoUsuario;
    }


    public void setMensajes(ArrayList<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    public ArrayList<Mensaje> getMensajes() {
        return mensajes;
    }

    public Chat() {
    }
}
