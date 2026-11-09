package com.sga.almacenes.data.local;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "usuarios",
        indices = {
                @Index(value = {"nombreUsuario"}, unique = true)
        }
)
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String nombreCompleto;
    private String nombreUsuario;
    private String clave;
    private String rol;
    private boolean activo;
    private boolean sincronizado;

    public Usuario(
            String nombreCompleto,
            String nombreUsuario,
            String clave,
            String rol,
            boolean activo,
            boolean sincronizado
    ) {
        this.nombreCompleto = nombreCompleto;
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.rol = rol;
        this.activo = activo;
        this.sincronizado = sincronizado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public boolean isSincronizado() {
        return sincronizado;
    }

    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }
}