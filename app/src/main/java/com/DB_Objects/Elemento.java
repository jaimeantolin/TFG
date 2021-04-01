package com.DB_Objects;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Elemento implements Serializable {
    @Exclude private String id;
    private String nombre, desc, sensorID, foto;

    public Elemento(){

    }

    public Elemento(String nombre, String desc, String sensorID, String foto){
        this.nombre = nombre;
        this.desc = desc;
        this.sensorID = sensorID;
        this.foto = foto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}