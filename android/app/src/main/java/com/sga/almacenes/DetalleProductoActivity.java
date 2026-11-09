package com.sga.almacenes;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sga.almacenes.data.local.Producto;
import com.sga.almacenes.data.local.SgaDatabase;

public class DetalleProductoActivity extends AppCompatActivity {

    private EditText etCodigo;
    private EditText etNombre;
    private EditText etDescripcion;
    private EditText etCantidad;
    private EditText etUbicacion;
    private EditText etLote;
    private EditText etVencimiento;
    private EditText etCodigoBarras;

    private SgaDatabase database;
    private Producto productoActual;
    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        database = SgaDatabase.obtenerInstancia(this);

        etCodigo = findViewById(R.id.etDetalleCodigo);
        etNombre = findViewById(R.id.etDetalleNombre);
        etDescripcion = findViewById(R.id.etDetalleDescripcion);
        etCantidad = findViewById(R.id.etDetalleCantidad);
        etUbicacion = findViewById(R.id.etDetalleUbicacion);
        etLote = findViewById(R.id.etDetalleLote);
        etVencimiento = findViewById(R.id.etDetalleVencimiento);
        etCodigoBarras = findViewById(R.id.etDetalleCodigoBarras);

        Button btnActualizar =
                findViewById(R.id.btnActualizarProducto);

        Button btnEliminar =
                findViewById(R.id.btnEliminarProducto);

        Button btnVolver =
                findViewById(R.id.btnVolverProductos);

        rol = getIntent().getStringExtra("rol");

        if (rol == null) {
            rol = "OPERARIO";
        }

        if ("OPERARIO".equals(rol)) {
            configurarModoConsulta(btnActualizar, btnEliminar);
        }

        int productoId =
                getIntent().getIntExtra("producto_id", -1);

        if (productoId == -1) {
            Toast.makeText(
                    this,
                    "No se recibió el producto",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        cargarProducto(productoId);

        btnActualizar.setOnClickListener(
                v -> actualizarProducto()
        );

        btnEliminar.setOnClickListener(
                v -> confirmarEliminacion()
        );

        btnVolver.setOnClickListener(v -> finish());
    }

    private void configurarModoConsulta(
            Button btnActualizar,
            Button btnEliminar
    ) {
        btnActualizar.setVisibility(View.GONE);
        btnEliminar.setVisibility(View.GONE);

        etCodigo.setEnabled(false);
        etNombre.setEnabled(false);
        etDescripcion.setEnabled(false);
        etCantidad.setEnabled(false);
        etUbicacion.setEnabled(false);
        etLote.setEnabled(false);
        etVencimiento.setEnabled(false);
        etCodigoBarras.setEnabled(false);
    }

    private void cargarProducto(int productoId) {
        SgaDatabase.databaseExecutor.execute(() -> {
            productoActual =
                    database.productoDao().buscarPorId(productoId);

            runOnUiThread(() -> {
                if (productoActual == null) {
                    Toast.makeText(
                            this,
                            "Producto no encontrado",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                    return;
                }

                etCodigo.setText(productoActual.getCodigo());
                etNombre.setText(productoActual.getNombre());
                etDescripcion.setText(
                        productoActual.getDescripcion()
                );

                etCantidad.setText(
                        String.valueOf(productoActual.getCantidad())
                );

                etUbicacion.setText(productoActual.getUbicacion());
                etLote.setText(productoActual.getLote());

                etVencimiento.setText(
                        productoActual.getFechaVencimiento()
                );

                etCodigoBarras.setText(
                        productoActual.getCodigoBarras()
                );
            });
        });
    }

    private void actualizarProducto() {
        if (!"ADMINISTRADOR".equals(rol)) {
            Toast.makeText(
                    this,
                    "No tienes permiso para modificar productos",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (productoActual == null) {
            return;
        }

        String nombre =
                etNombre.getText().toString().trim();

        String descripcion =
                etDescripcion.getText().toString().trim();

        String cantidadTexto =
                etCantidad.getText().toString().trim();

        String ubicacion =
                etUbicacion.getText().toString().trim();

        String lote =
                etLote.getText().toString().trim();

        String vencimiento =
                etVencimiento.getText().toString().trim();

        String codigoBarras =
                etCodigoBarras.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingrese el nombre");
            etNombre.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cantidadTexto)) {
            etCantidad.setError("Ingrese la cantidad");
            etCantidad.requestFocus();
            return;
        }

        int cantidad;

        try {
            cantidad = Integer.parseInt(cantidadTexto);
        } catch (NumberFormatException error) {
            etCantidad.setError("Ingrese una cantidad válida");
            etCantidad.requestFocus();
            return;
        }

        if (cantidad < 0) {
            etCantidad.setError(
                    "La cantidad no puede ser negativa"
            );
            etCantidad.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ubicacion)) {
            etUbicacion.setError("Ingrese la ubicación");
            etUbicacion.requestFocus();
            return;
        }

        productoActual.setNombre(nombre);
        productoActual.setDescripcion(descripcion);
        productoActual.setCantidad(cantidad);
        productoActual.setUbicacion(ubicacion);
        productoActual.setLote(lote);
        productoActual.setFechaVencimiento(vencimiento);
        productoActual.setCodigoBarras(codigoBarras);
        productoActual.setSincronizado(false);

        SgaDatabase.databaseExecutor.execute(() -> {
            database.productoDao().actualizar(productoActual);

            runOnUiThread(() -> {
                Toast.makeText(
                        this,
                        "Producto actualizado correctamente",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            });
        });
    }

    private void confirmarEliminacion() {
        if (!"ADMINISTRADOR".equals(rol)) {
            Toast.makeText(
                    this,
                    "No tienes permiso para eliminar productos",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (productoActual == null) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage(
                        "¿Seguro que deseas eliminar este producto?"
                )
                .setPositiveButton(
                        "Eliminar",
                        (dialog, which) -> eliminarProducto()
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarProducto() {
        SgaDatabase.databaseExecutor.execute(() -> {
            database.productoDao().eliminar(productoActual);

            runOnUiThread(() -> {
                Toast.makeText(
                        this,
                        "Producto eliminado",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            });
        });
    }
}