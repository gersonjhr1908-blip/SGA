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

public class DespachoActivity extends AppCompatActivity {

    private EditText etCodigo;
    private EditText etNumeroPedido;
    private EditText etCantidad;
    private TextView tvProducto;
    private Button btnConfirmar;

    private SgaDatabase database;
    private Producto productoSeleccionado;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despacho);

        database = SgaDatabase.obtenerInstancia(this);

        usuario = getIntent().getStringExtra("usuario");

        if (usuario == null) {
            usuario = "usuario_local";
        }

        etCodigo = findViewById(R.id.etDespachoCodigo);
        etNumeroPedido = findViewById(R.id.etNumeroPedido);
        etCantidad = findViewById(R.id.etCantidadDespacho);
        tvProducto = findViewById(R.id.tvProductoDespacho);
        btnConfirmar = findViewById(R.id.btnConfirmarDespacho);

        Button btnBuscar =
                findViewById(R.id.btnBuscarDespacho);

        Button btnEscanear =
                findViewById(R.id.btnEscanearDespacho);

        Button btnCancelar =
                findViewById(R.id.btnCancelarDespacho);

        btnBuscar.setOnClickListener(v -> buscarProducto());

        btnConfirmar.setOnClickListener(
                v -> confirmarDespacho()
        );

        btnEscanear.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Próximamente: escáner con ML Kit",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnCancelar.setOnClickListener(v -> finish());

        recibirDatosDelPicking();
    }

    private void recibirDatosDelPicking() {
        String pedidoRecibido =
                getIntent().getStringExtra("pedido");

        String codigoRecibido =
                getIntent().getStringExtra("codigo");

        int cantidadRecibida =
                getIntent().getIntExtra("cantidad", 0);

        if (pedidoRecibido != null && codigoRecibido != null) {
            etNumeroPedido.setText(pedidoRecibido);
            etCodigo.setText(codigoRecibido);

            if (cantidadRecibida > 0) {
                etCantidad.setText(
                        String.valueOf(cantidadRecibida)
                );
            }

            buscarProducto();
        }
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
                                    "\nCódigo: " +
                                    encontrado.getCodigo() +
                                    "\nStock disponible: " +
                                    encontrado.getCantidad() +
                                    " unidades" +
                                    "\nUbicación: " +
                                    encontrado.getUbicacion()
                    );

                    btnConfirmar.setEnabled(true);
                }
            });
        });
    }

    private void confirmarDespacho() {
        if (productoSeleccionado == null) {
            Toast.makeText(
                    this,
                    "Primero seleccione un producto",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String pedido =
                etNumeroPedido.getText().toString().trim();

        String cantidadTexto =
                etCantidad.getText().toString().trim();

        if (TextUtils.isEmpty(pedido)) {
            etNumeroPedido.setError(
                    "Ingrese el número de pedido"
            );
            etNumeroPedido.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cantidadTexto)) {
            etCantidad.setError("Ingrese la cantidad");
            etCantidad.requestFocus();
            return;
        }

        int cantidadDespacho =
                Integer.parseInt(cantidadTexto);

        if (cantidadDespacho <= 0) {
            etCantidad.setError(
                    "La cantidad debe ser mayor que cero"
            );
            etCantidad.requestFocus();
            return;
        }

        registrarSalida(cantidadDespacho, pedido);
    }

    private void registrarSalida(
            int cantidadDespacho,
            String pedido
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

                    int stockAnterior =
                            productoActual.getCantidad();

                    if (cantidadDespacho > stockAnterior) {
                        throw new IllegalArgumentException(
                                "STOCK_INSUFICIENTE"
                        );
                    }

                    int stockNuevo =
                            stockAnterior - cantidadDespacho;

                    productoActual.setCantidad(stockNuevo);
                    productoActual.setSincronizado(false);

                    database.productoDao().actualizar(
                            productoActual
                    );

                    String fechaHora = new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss",
                            Locale.getDefault()
                    ).format(new Date());

                    Movimiento movimiento = new Movimiento(
                            productoActual.getId(),
                            productoActual.getCodigo(),
                            productoActual.getNombre(),
                            "SALIDA",
                            cantidadDespacho,
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
                            "Pedido " + pedido +
                                    " despachado correctamente",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                });

            } catch (Exception error) {
                runOnUiThread(() -> {
                    String mensaje;

                    if ("STOCK_INSUFICIENTE".equals(
                            error.getMessage()
                    )) {
                        mensaje =
                                "Stock insuficiente para el despacho";
                    } else {
                        mensaje =
                                "No se pudo registrar el despacho";
                    }

                    Toast.makeText(
                            this,
                            mensaje,
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }
}