package com.sga.almacenes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sga.almacenes.adapters.UsuarioAdapter;
import com.sga.almacenes.data.local.SgaDatabase;
import com.sga.almacenes.data.local.Usuario;

import java.util.List;

public class UsuariosActivity extends AppCompatActivity {

    private UsuarioAdapter usuarioAdapter;
    private TextView tvSinUsuarios;
    private SgaDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        database = SgaDatabase.obtenerInstancia(this);

        RecyclerView rvUsuarios =
                findViewById(R.id.rvUsuarios);

        tvSinUsuarios =
                findViewById(R.id.tvSinUsuarios);

        Button btnNuevoUsuario =
                findViewById(R.id.btnNuevoUsuario);

        Button btnVolver =
                findViewById(R.id.btnVolverUsuarios);

        usuarioAdapter = new UsuarioAdapter(
                this::confirmarCambioEstado
        );

        rvUsuarios.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvUsuarios.setAdapter(usuarioAdapter);

        btnNuevoUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this,
                    RegistrarUsuarioActivity.class
            );

            startActivity(intent);
        });

        btnVolver.setOnClickListener(v -> finish());

        cargarUsuarios();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        SgaDatabase.databaseExecutor.execute(() -> {
            List<Usuario> usuarios =
                    database.usuarioDao().listarTodos();

            runOnUiThread(() -> {
                usuarioAdapter.actualizarLista(usuarios);

                if (usuarios.isEmpty()) {
                    tvSinUsuarios.setVisibility(View.VISIBLE);
                } else {
                    tvSinUsuarios.setVisibility(View.GONE);
                }
            });
        });
    }

    private void confirmarCambioEstado(Usuario usuario) {
        if ("admin".equals(usuario.getNombreUsuario())
                && usuario.isActivo()) {

            Toast.makeText(
                    this,
                    "El administrador principal no puede desactivarse",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String accion =
                usuario.isActivo() ? "desactivar" : "activar";

        new AlertDialog.Builder(this)
                .setTitle("Cambiar estado")
                .setMessage(
                        "¿Deseas " + accion + " al usuario " +
                                usuario.getNombreUsuario() + "?"
                )
                .setPositiveButton(
                        "Confirmar",
                        (dialog, which) -> cambiarEstado(usuario)
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void cambiarEstado(Usuario usuario) {
        boolean nuevoEstado = !usuario.isActivo();

        SgaDatabase.databaseExecutor.execute(() -> {
            database.usuarioDao().cambiarEstado(
                    usuario.getId(),
                    nuevoEstado
            );

            runOnUiThread(() -> {
                String mensaje =
                        nuevoEstado
                                ? "Usuario activado"
                                : "Usuario desactivado";

                Toast.makeText(
                        this,
                        mensaje,
                        Toast.LENGTH_SHORT
                ).show();

                cargarUsuarios();
            });
        });
    }
}