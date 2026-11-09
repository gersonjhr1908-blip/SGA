package com.sga.almacenes;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sga.almacenes.data.local.Movimiento;
import com.sga.almacenes.data.local.Producto;
import com.sga.almacenes.data.local.SgaDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecepcionActivity extends AppCompatActivity {

    private EditText etCodigo;
    private EditText etCantidad;
    private EditText etLote;
    private EditText etVencimiento;
    private TextView tvProducto;
    private Button btnConfirmar;

    private SgaDatabase database;
    private Producto productoSeleccionado;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepcion);

        database = SgaDatabase.obtenerInstancia(this);

        usuario = getIntent().getStringExtra("usuario");

        if (usuario == null) {
            usuario = "usuario_local";
        }

        etCodigo = findViewById(R.id.etRecepcionCodigo);
        etCantidad = findViewById(R.id.etCantidadRecibida);
        etLote = findViewById(R.id.etRecepcionLote);
        etVencimiento = findViewById(R.id.etRecepcionVencimiento);
        tvProducto = findViewById(R.id.tvProductoRecepcion);
        btnConfirmar = findViewById(R.id.btnConfirmarRecepcion);

        Button btnBuscar = findViewById(R.id.btnBuscarRecepcion);
        Button btnEscanear = findViewById(R.id.btnEscanearRecepcion);
        Button btnCancelar = findViewById(R.id.btnCancelarRecepcion);

        btnBuscar.setOnClickListener(v -> buscarProducto());
        btnConfirmar.setOnClickListener(v -> confirmarRecepcion());

        btnEscanear.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Próximamente: escáner con ML Kit",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnCancelar.setOnClickListener(v -> finish());
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
                    btnConfirmar.setEnabled(false);

                    Toast.makeText(
                            this,
                            "No existe un producto con ese código",
                            Toast.LENGTH_SHORT
                    ).show();
                } else {
                    tvProducto.setText(
                            encontrado.getNombre() +
                                    "\nCódigo: " + encontrado.getCodigo() +
                                    "\nStock actual: " +
                                    encontrado.getCantidad() +
                                    " unidades" +
                                    "\nUbicación: " +
                                    encontrado.getUbicacion()
                    );

                    etLote.setText(encontrado.getLote());
                    etVencimiento.setText(
                            encontrado.getFechaVencimiento()
                    );

                    btnConfirmar.setEnabled(true);
                }
            });
        });
    }

    private void confirmarRecepcion() {
        if (productoSeleccionado == null) {
            Toast.makeText(
                    this,
                    "Primero seleccione un producto",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String cantidadTexto =
                etCantidad.getText().toString().trim();
        String lote = etLote.getText().toString().trim();
        String vencimiento =
                etVencimiento.getText().toString().trim();

        if (TextUtils.isEmpty(cantidadTexto)) {
            etCantidad.setError("Ingrese la cantidad recibida");
            etCantidad.requestFocus();
            return;
        }

        int cantidadRecibida = Integer.parseInt(cantidadTexto);

        if (cantidadRecibida <= 0) {
            etCantidad.setError(
                    "La cantidad debe ser mayor que cero"
            );
            etCantidad.requestFocus();
            return;
        }

        registrarEntrada(cantidadRecibida, lote, vencimiento);
    }

    private void registrarEntrada(
            int cantidadRecibida,
            String lote,
            String vencimiento
    ) {
        SgaDatabase.databaseExecutor.execute(() -> {
            try {
                database.runInTransaction(() -> {
                    Producto productoActual =
                            database.productoDao().buscarPorId(
                                    productoSeleccionado.getId()
                            );

                    if (productoActual == null) {
                        throw new IllegalStateException(
                                "El producto ya no existe"
                        );
                    }

                    int stockAnterior = productoActual.getCantidad();
                    int stockNuevo =
                            stockAnterior + cantidadRecibida;

                    productoActual.setCantidad(stockNuevo);
                    productoActual.setLote(lote);
                    productoActual.setFechaVencimiento(vencimiento);
                    productoActual.setSincronizado(false);

                    database.productoDao().actualizar(productoActual);

                    String fechaHora = new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss",
                            Locale.getDefault()
                    ).format(new Date());

                    Movimiento movimiento = new Movimiento(
                            productoActual.getId(),
                            productoActual.getCodigo(),
                            productoActual.getNombre(),
                            "ENTRADA",
                            cantidadRecibida,
                            stockAnterior,
                            stockNuevo,
                            usuario,
                            fechaHora,
                            false
                    );

                    database.movimientoDao().insertar(movimiento);
                });

                runOnUiThread(() -> {
                    Toast.makeText(
                            this,
                            "Recepción registrada correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                });

            } catch (Exception error) {
                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "No se pudo registrar la recepción",
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }
}