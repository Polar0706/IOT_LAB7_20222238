package com.example.lab6_iot_20222238.models;

public class RegistroCombustible {
    private String id;
    private String idRegistro;
    private String vehiculoId;
    private String fecha;
    private double litrosCargados;
    private int kilometrajeActual;
    private double precioTotal;
    private String tipoCombustible;
    private String userId;

    public RegistroCombustible() {
    }

    public RegistroCombustible(String idRegistro, String vehiculoId, String fecha, double litrosCargados, int kilometrajeActual, double precioTotal, String tipoCombustible, String userId) {
        this.idRegistro = idRegistro;
        this.vehiculoId = vehiculoId;
        this.fecha = fecha;
        this.litrosCargados = litrosCargados;
        this.kilometrajeActual = kilometrajeActual;
        this.precioTotal = precioTotal;
        this.tipoCombustible = tipoCombustible;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(String idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getVehiculoId() {
        return vehiculoId;
    }

    public void setVehiculoId(String vehiculoId) {
        this.vehiculoId = vehiculoId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getLitrosCargados() {
        return litrosCargados;
    }

    public void setLitrosCargados(double litrosCargados) {
        this.litrosCargados = litrosCargados;
    }

    public int getKilometrajeActual() {
        return kilometrajeActual;
    }

    public void setKilometrajeActual(int kilometrajeActual) {
        this.kilometrajeActual = kilometrajeActual;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(String tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
