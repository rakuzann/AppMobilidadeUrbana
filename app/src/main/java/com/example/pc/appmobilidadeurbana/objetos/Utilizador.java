package com.example.pc.appmobilidadeurbana.objetos;

import java.io.Serializable;

public class Utilizador implements Serializable {

    private int id;
    private String nome;
    private String username;
    private String password;
    private String email;

    public Utilizador() {
    }

    public Utilizador(int id,String nome, String username, String password, String email) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
