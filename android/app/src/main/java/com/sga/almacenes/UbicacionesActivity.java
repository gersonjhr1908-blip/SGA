package com.sga.almacenes;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sga.almacenes.data.local.Producto;
import com.sga.almacenes.data.local.SgaDatabase;

public class UbicacionesActivity extends AppCompatActivity {

    private EditText etCodigo;
    private EditText etNuevaUbicacion;
    private TextView tvProducto;
    private Button btnActualizar;

    private SgaDatabase database;
    private Producto productoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicaciones);

        database = SgaDatabase.obtenerInstancia(this);

        etCodigo = findViewById(R.id.etUbicacionCodigo);
        etNuevaUbicacion = findViewById(R.id.etNuevaUbicacion);
        tvProducto = findViewById(R.id.tvProductoUbicacion);
        btnActualizar =
                findViewById(R.id.btnActualizarUbicacion);

        Button btnBuscar =
                findViewById(R.id.btnBuscarUbicacion);
        Button btnEscanear =
                findViewById(R.id.btnEscanearUbicacion);
        Button btnVolver =
                findViewById(R.id.btnVolverUbicaciones);

        btnBuscar.setOnClickListener(v -> buscarProducto());
        btnActualizar.setOnClickListener(v -> actualizarUbicacion());

        btnEscanear.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Próximamente: escáner con ML Kit",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnVolver.setOnClickListener(v -> finish());
    }

    private void buscarProducto() {
        String codigo = etCodigo.getText().toString().trim();

        if (TextUtils.isEmpty(codigo)) {
            etCodigo.setError("Ingrese el código del producto");
            etCodigo.requestFocus();
            return;
        }

        SgaDatabase.databaseExecutor.execute(() -> {
            Producto encontrado =
                    database.productoDao().buscarPorCodigo(codigo);

            runOnUiThread(() -> {
                productoSeleccionado = encontrado;

                if (encontrado == null) {
                    tvProducto.setText("Producto no encontrado");
                    etNuevaUbicacion.setText("");
                    btnActualizar.setEnabled(false);

                    Toast.makeText(
                            this,
                            "No existe un producto con ese código",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    tvProducto.setText(
                            encontrado.getNombre() +
                                    "\nCódigo: " +
                                    encontrado.getCodigo() +
                                    "\nStock: " +
                                    encontrado.getCantidad() +
                                    " unidades" +
                                    "\nUbicación actual: " +
                                    encontrado.getUbicacion()
                    );

                    etNuevaUbicacion.setText(
                            encontrado.getUbicacion()
                    );

                    btnActualizar.setEnabled(true);
                }
            });
        });
    }

    private void actualizarUbicacion() {
        if (productoSeleccionado == null) {
            Toast.makeText(
                    this,
                    "Primero seleccione un producto",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String nuevaUbicacion =
                etNuevaUbicacion.getText().toString().trim();

        if (TextUtils.isEmpty(nuevaUbicacion)) {
            etNuevaUbicacion.setError(
                    "Ingrese la nueva ubicación"
            );
            etNuevaUbicacion.requestFocus();
            return;
        }

        if (!nuevaUbicacion.matches(
                "^[A-Z0-9]+-[A-Z0-9]+-[A-Z0-9]+$"
        )) {
            etNuevaUbicacion.setError(
                    "Use el formato ZONA-PASILLO-ESTANTE"
            );
            etNuevaUbicacion.requestFocus();
            return;
        }

        productoSeleccionado.setUbicacion(nuevaUbicacion);
        productoSeleccionado.setSincronizado(false);

        SgaDatabase.databaseExecutor.execute(() -> {
            try {
                database.productoDao().actualizar(
                        productoSeleccionado
                );

                runOnUiThread(() -> {
                    Toast.makeText(
                            this,
                            "Ubicación actualizada correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    tvProducto.setText(
                            productoSeleccionado.getNombre() +
                                    "\nCódigo: " +
                                    productoSeleccionado.getCodigo() +
                                    "\nStock: " +
                                    productoSeleccionado.getCantidad() +
                                    " unidades" +
                                    "\nUbicación actual: " +
                                    productoSeleccionado.getUbicacion()
                    );
                });

            } catch (Exception error) {
                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "No se pudo actualizar la ubicación",
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }
}