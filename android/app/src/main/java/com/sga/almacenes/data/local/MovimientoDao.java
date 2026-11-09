package com.sga.almacenes.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MovimientoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertar(Movimiento movimiento);

    @Query("SELECT * FROM movimientos ORDER BY id DESC")
    List<Movimiento> listarTodos();

    @Query("SELECT * FROM movimientos " +
            "WHERE productoId = :productoId ORDER BY id DESC")
    List<Movimiento> listarPorProducto(int productoId);

    @Query("SELECT * FROM movimientos " +
            "WHERE tipo = :tipo ORDER BY id DESC")
    List<Movimiento> listarPorTipo(String tipo);

    @Query("SELECT * FROM movimientos " +
            "WHERE sincronizado = 0 ORDER BY id ASC")
    List<Movimiento> listarPendientesSincronizacion();

    @Query("UPDATE movimientos SET sincronizado = 1 " +
            "WHERE id = :movimientoId")
    void marcarComoSincronizado(int movimientoId);
}