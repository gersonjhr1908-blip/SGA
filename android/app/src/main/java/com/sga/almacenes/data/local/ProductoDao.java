package com.sga.almacenes.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertar(Producto producto);

    @Update
    void actualizar(Producto producto);

    @Delete
    void eliminar(Producto producto);

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    List<Producto> listarTodos();

    @Query("SELECT * FROM productos " +
            "WHERE codigo LIKE '%' || :texto || '%' " +
            "OR nombre LIKE '%' || :texto || '%' " +
            "ORDER BY nombre ASC")
    List<Producto> buscar(String texto);

    @Query("SELECT * FROM productos WHERE codigo = :codigo LIMIT 1")
    Producto buscarPorCodigo(String codigo);

    @Query("SELECT * FROM productos WHERE id = :productoId LIMIT 1")
    Producto buscarPorId(int productoId);

    @Query("SELECT * FROM productos WHERE sincronizado = 0")
    List<Producto> listarPendientesSincronizacion();

    @Query("UPDATE productos SET sincronizado = 1 WHERE id = :productoId")
    void marcarComoSincronizado(int productoId);

    @Query("UPDATE productos SET cantidad = :nuevoStock, " +
            "sincronizado = 0 WHERE id = :productoId")
    void actualizarStock(int productoId, int nuevoStock);
}