package com.sga.almacenes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sga.almacenes.data.local.SgaDatabase;
import com.sga.almacenes.data.local.Usuario;

public class MainActivity extends AppCompatActivity {

    private EditText etUsuario;
    private EditText etContrasena;
    private Button btnIngresar;
    private SgaDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = SgaDatabase.obtenerInstancia(this);

        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        btnIngresar = findViewById(R.id.btnIngresar);

        btnIngresar.setOnClickListener(v -> validarLogin());
    }

    private void validarLogin() {
        String nombreUsuario =
                etUsuario.getText().toString().trim();

        String clave =
                etContrasena.getText().toString().trim();

        if (TextUtils.isEmpty(nombreUsuario)) {
            etUsuario.setError("Ingrese su usuario");
            etUsuario.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(clave)) {
            etContrasena.setError("Ingrese su contraseña");
            etContrasena.requestFocus();
            return;
        }

        btnIngresar.setEnabled(false);
        btnIngresar.setText("VALIDANDO...");

        SgaDatabase.databaseExecutor.execute(() -> {
            Usuario usuarioEncontrado =
                    database.usuarioDao().iniciarSesion(
                            nombreUsuario,
                            clave
                    );

            runOnUiThread(() -> {
                btnIngresar.setEnabled(true);
                btnIngresar.setText("INGRESAR");

                if (usuarioEncontrado == null) {
                    Toast.makeText(
                            this,
                            "Usuario, contraseña incorrecta o cuenta inactiva",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                abrirMenu(usuarioEncontrado);
            });
        });
    }

    private void abrirMenu(Usuario usuario) {
        Toast.makeText(
                this,
                "Bienvenido, " + usuario.getNombreCompleto(),
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(this, MenuActivity.class);

        intent.putExtra(
                "usuario",
                usuario.getNombreUsuario()
        );

        intent.putExtra(
                "nombre_completo",
                usuario.getNombreCompleto()
        );

        intent.putExtra(
                "rol",
                usuario.getRol()
        );

        startActivity(intent);

        etContrasena.setText("");
    }
}