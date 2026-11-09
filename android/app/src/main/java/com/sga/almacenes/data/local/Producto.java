package com.sga.almacenes.data.local;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "productos",
        indices = {
                @Index(value = {"codigo"}, unique = true)
        }
)
public class Producto {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String codigo;
    private String nombre;
    private String descripcion;
    private int cantidad;
    private String ubicacion;
    private String lote;
    private String fechaVencimiento;
    private String codigoBarras;
    private boolean sincronizado;

    public Producto(
            String codigo,
            String nombre,
            String descripcion,
            int cantidad,
            String ubicacion,
            String lote,
            String fechaVencimiento,
            String codigoBarras,
            boolean sincronizado
    ) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.ubicacion = ubicacion;
        this.lote = lote;
        this.fechaVencimiento = fechaVencimiento;
        this.codigoBarras = codigoBarras;
        this.sincronizado = sincronizado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }
}