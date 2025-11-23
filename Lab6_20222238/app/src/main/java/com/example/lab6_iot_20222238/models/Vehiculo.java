package com.example.lab6_iot_20222238.models;

public class Vehiculo {
    private String id;
    private String idVehiculo;
    private String placa;
    private String marcaModelo;
    private int anioFabricacion;
    private String fechaRevisionTecnica;
    private String userId;

    public Vehiculo() {
    }

    public Vehiculo(String idVehiculo, String placa, String marcaModelo, int anioFabricacion, String fechaRevisionTecnica, String userId) {
        this.idVehiculo = idVehiculo;
        this.placa = placa;
        this.marcaModelo = marcaModelo;
        this.anioFabricacion = anioFabricacion;
        this.fechaRevisionTecnica = fechaRevisionTecnica;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(String idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarcaModelo() {
        return marcaModelo;
    }

    public void setMarcaModelo(String marcaModelo) {
        this.marcaModelo = marcaModelo;
    }

    public int getAnioFabricacion() {
        return anioFabricacion;
    }

    public void setAnioFabricacion(int anioFabricacion) {
        this.anioFabricacion = anioFabricacion;
    }

    public String getFechaRevisionTecnica() {
        return fechaRevisionTecnica;
    }

    public void setFechaRevisionTecnica(String fechaRevisionTecnica) {
        this.fechaRevisionTecnica = fechaRevisionTecnica;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
