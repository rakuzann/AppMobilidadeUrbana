package com.example.pc.appmobilidadeurbana.objetos;

import java.io.Serializable;

public class Paragem implements Serializable {

    private int id;
    private String nome;
    private double latitude;
    private double longitude;
    private int id_rota;
    private double horario;

    public Paragem() {
    }

    public Paragem(int id,String nome, double latitude, double longitude,int id_rota, double horario) {
        this.id = id;
        this.nome = nome;
        this.latitude = latitude;
        this.longitude = longitude;
        this.id_rota = id_rota;
        this.horario = horario;
    }

    public int getId_rota() {
        return id_rota;
    }

    public void setId_rota(int id_rota) {
        this.id_rota = id_rota;
    }

    public double getHorario() {
        return horario;
    }

    public void setHorario(double horario) {
        this.horario = horario;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
