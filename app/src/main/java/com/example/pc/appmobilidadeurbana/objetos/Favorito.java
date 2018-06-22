package com.example.pc.appmobilidadeurbana.objetos;

import java.io.Serializable;

public class Favorito implements Serializable {

    private int id;
    private Double latitude;
    private Double longitude;
    private String nome;
    private int id_user;


    public Favorito() {
    }

    public Favorito(int id,Double latitude,Double longitude,String nome,int id_user ) {
        this.id=id;
        this.latitude=latitude;
        this.longitude=longitude;
        this.nome=nome;
        this.id_user=id_user;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }
}
