package com.sga.almacenes;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sga.almacenes.data.local.Producto;
import com.sga.almacenes.data.local.SgaDatabase;

public class RegistrarProductoActivity extends AppCompatActivity {

    private EditText etCodigo;
    private EditText etNombreProducto;
    private EditText etDescripcion;
    private EditText etCantidad;
    private EditText etUbicacion;
    private EditText etLote;
    private EditText etVencimiento;
    private EditText etCodigoBarras;

    private SgaDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_producto);

        database = SgaDatabase.obtenerInstancia(this);

        etCodigo = findViewById(R.id.etCodigo);
        etNombreProducto = findViewById(R.id.etNombreProducto);
        etDescripcion = findViewById(R.id.etDescripcion);
        etCantidad = findViewById(R.id.etCantidad);
        etUbicacion = findViewById(R.id.etUbicacion);
        etLote = findViewById(R.id.etLote);
        etVencimiento = findViewById(R.id.etVencimiento);
        etCodigoBarras = findViewById(R.id.etCodigoBarras);

        Button btnGuardarProducto =
                findViewById(R.id.btnGuardarProducto);
        Button btnEscanearCodigo =
                findViewById(R.id.btnEscanearCodigo);
        Button btnCancelarRegistro =
                findViewById(R.id.btnCancelarRegistro);

        btnGuardarProducto.setOnClickListener(v -> validarYGuardar());

        btnEscanearCodigo.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "El escáner se implementará con CameraX y ML Kit",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnCancelarRegistro.setOnClickListener(v -> finish());
    }

    private void validarYGuardar() {
        String codigo = etCodigo.getText().toString().trim();
        String nombre = etNombreProducto.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String cantidadTexto = etCantidad.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String lote = etLote.getText().toString().trim();
        String vencimiento = etVencimiento.getText().toString().trim();
        String codigoBarras = etCodigoBarras.getText().toString().trim();

        if (TextUtils.isEmpty(codigo)) {
            etCodigo.setError("Ingrese el código");
            etCodigo.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nombre)) {
            etNombreProducto.setError("Ingrese el nombre");
            etNombreProducto.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cantidadTexto)) {
            etCantidad.setError("Ingrese la cantidad");
            etCantidad.requestFocus();
            return;
        }

        int cantidad = Integer.parseInt(cantidadTexto);

        if (cantidad <= 0) {
            etCantidad.setError("La cantidad debe ser mayor que cero");
            etCantidad.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ubicacion)) {
            etUbicacion.setError("Ingrese la ubicación");
            etUbicacion.requestFocus();
            return;
        }

        Producto producto = new Producto(
                codigo,
                nombre,
                descripcion,
                cantidad,
                ubicacion,
                lote,
                vencimiento,
                codigoBarras,
                false
        );

        guardarProducto(producto);
    }

    private void guardarProducto(Producto producto) {
        SgaDatabase.databaseExecutor.execute(() -> {
            try {
                database.productoDao().insertar(producto);

                runOnUiThread(() -> {
                    Toast.makeText(
                            this,
                            "Producto guardado correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                });

            } catch (Exception error) {
                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "No se pudo guardar. Verifique que el código no esté repetido",
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }
}