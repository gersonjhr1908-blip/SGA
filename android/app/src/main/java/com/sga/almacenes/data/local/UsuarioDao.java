package com.sga.almacenes.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertar(Usuario usuario);

    @Update
    void actualizar(Usuario usuario);

    @Query("SELECT * FROM usuarios ORDER BY nombreCompleto ASC")
    List<Usuario> listarTodos();

    @Query("SELECT * FROM usuarios " +
            "WHERE nombreUsuario = :nombreUsuario " +
            "AND clave = :clave " +
            "AND activo = 1 LIMIT 1")
    Usuario iniciarSesion(
            String nombreUsuario,
            String clave
    );

    @Query("SELECT * FROM usuarios " +
            "WHERE nombreUsuario = :nombreUsuario LIMIT 1")
    Usuario buscarPorNombreUsuario(String nombreUsuario);

    @Query("SELECT * FROM usuarios WHERE id = :usuarioId LIMIT 1")
    Usuario buscarPorId(int usuarioId);

    @Query("UPDATE usuarios SET activo = :activo, " +
            "sincronizado = 0 WHERE id = :usuarioId")
    void cambiarEstado(int usuarioId, boolean activo);

    @Query("SELECT COUNT(*) FROM usuarios")
    int contarUsuarios();

    @Query("SELECT * FROM usuarios WHERE sincronizado = 0")
    List<Usuario> listarPendientesSincronizacion();
}