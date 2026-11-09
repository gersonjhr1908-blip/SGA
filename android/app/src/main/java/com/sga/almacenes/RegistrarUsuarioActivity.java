package com.sga.almacenes;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sga.almacenes.data.local.SgaDatabase;
import com.sga.almacenes.data.local.Usuario;

import java.util.Locale;

public class RegistrarUsuarioActivity extends AppCompatActivity {

    private EditText etNombreCompleto;
    private EditText etNombreUsuario;
    private EditText etClave;
    private EditText etConfirmarClave;
    private Spinner spinnerRol;

    private SgaDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        database = SgaDatabase.obtenerInstancia(this);

        etNombreCompleto =
                findViewById(R.id.etUsuarioNombreCompleto);

        etNombreUsuario =
                findViewById(R.id.etNuevoNombreUsuario);

        etClave =
                findViewById(R.id.etNuevaClave);

        etConfirmarClave =
                findViewById(R.id.etConfirmarClave);

        spinnerRol =
                findViewById(R.id.spinnerRolUsuario);

        Button btnGuardar =
                findViewById(R.id.btnGuardarUsuario);

        Button btnCancelar =
                findViewById(R.id.btnCancelarUsuario);

        configurarRoles();

        btnGuardar.setOnClickListener(v -> validarYGuardar());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void configurarRoles() {
        String[] roles = {
                "OPERARIO",
                "ADMINISTRADOR"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        roles
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerRol.setAdapter(adapter);
    }

    private void validarYGuardar() {
        String nombreCompleto =
                etNombreCompleto.getText().toString().trim();

        String nombreUsuario =
                etNombreUsuario.getText().toString()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        String clave =
                etClave.getText().toString().trim();

        String confirmarClave =
                etConfirmarClave.getText().toString().trim();

        String rol =
                spinnerRol.getSelectedItem().toString();

        if (TextUtils.isEmpty(nombreCompleto)) {
            etNombreCompleto.setError(
                    "Ingrese el nombre completo"
            );
            etNombreCompleto.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nombreUsuario)) {
            etNombreUsuario.setError(
                    "Ingrese un nombre de usuario"
            );
            etNombreUsuario.requestFocus();
            return;
        }

        if (!nombreUsuario.matches("^[a-z0-9._]+$")) {
            etNombreUsuario.setError(
                    "Use solo letras, números, punto o guion bajo"
            );
            etNombreUsuario.requestFocus();
            return;
        }

        if (clave.length() < 6) {
            etClave.setError(
                    "La contraseña debe tener mínimo 6 caracteres"
            );
            etClave.requestFocus();
            return;
        }

        if (!clave.equals(confirmarClave)) {
            etConfirmarClave.setError(
                    "Las contraseñas no coinciden"
            );
            etConfirmarClave.requestFocus();
            return;
        }

        Usuario nuevoUsuario = new Usuario(
                nombreCompleto,
                nombreUsuario,
                clave,
                rol,
                true,
                false
        );

        guardarUsuario(nuevoUsuario);
    }

    private void guardarUsuario(Usuario usuario) {
        SgaDatabase.databaseExecutor.execute(() -> {
            try {
                Usuario existente =
                        database.usuarioDao()
                                .buscarPorNombreUsuario(
                                        usuario.getNombreUsuario()
                                );

                if (existente != null) {
                    runOnUiThread(() -> {
                        etNombreUsuario.setError(
                                "Este nombre de usuario ya existe"
                        );
                        etNombreUsuario.requestFocus();
                    });
                    return;
                }

                database.usuarioDao().insertar(usuario);

                runOnUiThread(() -> {
                    Toast.makeText(
                            this,
                            "Usuario registrado correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                });

            } catch (Exception error) {
                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "No se pudo registrar el usuario",
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }
}