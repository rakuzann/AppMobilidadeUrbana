package com.example.pc.appmobilidadeurbana.objetos;

import java.io.Serializable;

public class Paragem implements Serializable {

    private int id;
    private String nome;
    private Double latitude;
    private Double longitude;
    private int id_rota;
    private double horario;

    public Paragem() {
    }

    public Paragem(int id,String nome, Double latitude, Double longitude,int id_rota, double horario) {
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
}
