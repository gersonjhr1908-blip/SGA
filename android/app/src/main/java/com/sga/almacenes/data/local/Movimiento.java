package com.sga.almacenes.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movimientos")
public class Movimiento {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int productoId;
    private String codigoProducto;
    private String nombreProducto;
    private String tipo;
    private int cantidad;
    private int stockAnterior;
    private int stockNuevo;
    private String usuario;
    private String fechaHora;
    private boolean sincronizado;

    public Movimiento(
            int productoId,
            String codigoProducto,
            String nombreProducto,
            String tipo,
            int cantidad,
            int stockAnterior,
            int stockNuevo,
            String usuario,
            String fechaHora,
            boolean sincronizado
    ) {
        this.productoId = productoId;
        this.codigoProducto = codigoProducto;
        this.nombreProducto = nombreProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.stockAnterior = stockAnterior;
        this.stockNuevo = stockNuevo;
        this.usuario = usuario;
        this.fechaHora = fechaHora;
        this.sincronizado = sincronizado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductoId() {
        return productoId;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getTipo() {
        return tipo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getStockAnterior() {
        return stockAnterior;
    }

    public int getStockNuevo() {
        return stockNuevo;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }
}