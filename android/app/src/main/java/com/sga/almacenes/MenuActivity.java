package com.sga.almacenes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView tvBienvenida =
                findViewById(R.id.tvBienvenida);

        TextView tvRol =
                findViewById(R.id.tvRol);

        Button btnRecepcion =
                findViewById(R.id.btnRecepcion);

        Button btnUbicaciones =
                findViewById(R.id.btnUbicaciones);

        Button btnPicking =
                findViewById(R.id.btnPicking);

        Button btnDespacho =
                findViewById(R.id.btnDespacho);

        Button btnInventario =
                findViewById(R.id.btnInventario);

        Button btnAuditoria =
                findViewById(R.id.btnAuditoria);

        Button btnHistorial =
                findViewById(R.id.btnHistorial);

        Button btnAdministrarUsuarios =
                findViewById(R.id.btnAdministrarUsuarios);

        Button btnCerrarSesion =
                findViewById(R.id.btnCerrarSesion);

        String usuario =
                getIntent().getStringExtra("usuario");

        String rol =
                getIntent().getStringExtra("rol");

        if (usuario == null) {
            usuario = "Usuario";
        }

        if (rol == null) {
            rol = "OPERARIO";
        }

        tvBienvenida.setText("Bienvenido, " + usuario);
        tvRol.setText("Rol: " + rol);

        if ("OPERARIO".equals(rol)) {
            btnAdministrarUsuarios.setVisibility(View.GONE);
        }

        String usuarioActual = usuario;
        String rolActual = rol;

        btnRecepcion.setOnClickListener(v -> {
            Intent intent =
                    new Intent(this, RecepcionActivity.class);

            intent.putExtra("usuario", usuarioActual);
            startActivity(intent);
        });

        btnUbicaciones.setOnClickListener(v -> {
            Intent intent =
                    new Intent(this, UbicacionesActivity.class);

            startActivity(intent);
        });

        btnPicking.setOnClickListener(v -> {
            Intent intent =
                    new Intent(this, PickingActivity.class);

            intent.putExtra("usuario", usuarioActual);
            startActivity(intent);
        });

        btnDespacho.setOnClickListener(v -> {
            Intent intent =
                    new Intent(this, DespachoActivity.class);

            intent.putExtra("usuario", usuarioActual);
            startActivity(intent);
        });

        btnInventario.setOnClickListener(v -> {
            Intent intent =
                    new Intent(this, ProductosActivity.class);

            intent.putExtra("rol", rolActual);
            startActivity(intent);
        });

        btnAuditoria.setOnClickListener(v -> {
            Intent intent =
                    new Intent(this, AuditoriaActivity.class);

            intent.putExtra("usuario", usuarioActual);
            startActivity(intent);
        });

        btnHistorial.setOnClickListener(v -> {
            Intent intent =
                    new Intent(this, HistorialActivity.class);

            startActivity(intent);
        });

        btnAdministrarUsuarios.setOnClickListener(v -> {
            Intent intent =
                    new Intent(this, UsuariosActivity.class);

            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(v -> finish());
    }
}