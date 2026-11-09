package com.sga.almacenes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sga.almacenes.data.local.Producto;
import com.sga.almacenes.data.local.SgaDatabase;

public class PickingActivity extends AppCompatActivity {

    private EditText etPedido;
    private EditText etCodigo;
    private EditText etCantidad;
    private TextView tvProducto;
    private TextView tvUbicacion;
    private Button btnConfirmar;

    private SgaDatabase database;
    private Producto productoSeleccionado;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picking);

        database = SgaDatabase.obtenerInstancia(this);

        usuario = getIntent().getStringExtra("usuario");

        if (usuario == null) {
            usuario = "usuario_local";
        }

        etPedido = findViewById(R.id.etPickingPedido);
        etCodigo = findViewById(R.id.etPickingCodigo);
        etCantidad = findViewById(R.id.etCantidadPicking);
        tvProducto = findViewById(R.id.tvProductoPicking);
        tvUbicacion = findViewById(R.id.tvIndicacionUbicacion);
        btnConfirmar = findViewById(R.id.btnConfirmarPicking);

        Button btnBuscar =
                findViewById(R.id.btnBuscarPicking);
        Button btnEscanear =
                findViewById(R.id.btnEscanearPicking);
        Button btnCancelar =
                findViewById(R.id.btnCancelarPicking);

        btnBuscar.setOnClickListener(v -> buscarProducto());
        btnConfirmar.setOnClickListener(v -> confirmarPicking());

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
            etCodigo.setError("Ingrese el código");
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
                    tvUbicacion.setText(
                            "La ubicación aparecerá aquí"
                    );
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
                                    " unidades"
                    );

                    tvUbicacion.setText(
                            "DIRÍGETE A: " +
                                    encontrado.getUbicacion()
                    );

                    btnConfirmar.setEnabled(true);
                }
            });
        });
    }

    private void confirmarPicking() {
        if (productoSeleccionado == null) {
            Toast.makeText(
                    this,
                    "Primero seleccione un producto",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String pedido = etPedido.getText().toString().trim();
        String cantidadTexto =
                etCantidad.getText().toString().trim();

        if (TextUtils.isEmpty(pedido)) {
            etPedido.setError("Ingrese el número de pedido");
            etPedido.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(cantidadTexto)) {
            etCantidad.setError("Ingrese la cantidad solicitada");
            etCantidad.requestFocus();
            return;
        }

        int cantidad = Integer.parseInt(cantidadTexto);

        if (cantidad <= 0) {
            etCantidad.setError(
                    "La cantidad debe ser mayor que cero"
            );
            etCantidad.requestFocus();
            return;
        }

        if (cantidad > productoSeleccionado.getCantidad()) {
            etCantidad.setError("Stock insuficiente");
            etCantidad.requestFocus();

            Toast.makeText(
                    this,
                    "No hay suficiente stock para preparar el pedido",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        Toast.makeText(
                this,
                "Producto recogido. Continúe con el despacho",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(this, DespachoActivity.class);
        intent.putExtra("usuario", usuario);
        intent.putExtra("pedido", pedido);
        intent.putExtra("codigo", productoSeleccionado.getCodigo());
        intent.putExtra("cantidad", cantidad);
        startActivity(intent);

        finish();
    }
}