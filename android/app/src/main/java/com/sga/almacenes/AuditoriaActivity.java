package com.sga.almacenes;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.sga.almacenes.data.local.Movimiento;
import com.sga.almacenes.data.local.Producto;
import com.sga.almacenes.data.local.SgaDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AuditoriaActivity extends AppCompatActivity {

    private EditText etCodigo;
    private EditText etConteoFisico;
    private EditText etMotivo;
    private TextView tvProducto;
    private TextView tvResultado;
    private Button btnCalcular;
    private Button btnConfirmar;

    private SgaDatabase database;
    private Producto productoSeleccionado;
    private int conteoCalculado;
    private int diferenciaCalculada;
    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditoria);

        database = SgaDatabase.obtenerInstancia(this);

        usuario = getIntent().getStringExtra("usuario");

        if (usuario == null) {
            usuario = "usuario_local";
        }

        etCodigo = findViewById(R.id.etAuditoriaCodigo);
        etConteoFisico = findViewById(R.id.etConteoFisico);
        etMotivo = findViewById(R.id.etMotivoAjuste);
        tvProducto = findViewById(R.id.tvProductoAuditoria);
        tvResultado = findViewById(R.id.tvResultadoAuditoria);
        btnCalcular = findViewById(R.id.btnCalcularDiferencia);
        btnConfirmar = findViewById(R.id.btnConfirmarAjuste);

        Button btnBuscar =
                findViewById(R.id.btnBuscarAuditoria);
        Button btnEscanear =
                findViewById(R.id.btnEscanearAuditoria);
        Button btnVolver =
                findViewById(R.id.btnVolverAuditoria);

        btnBuscar.setOnClickListener(v -> buscarProducto());
        btnCalcular.setOnClickListener(v -> calcularDiferencia());
        btnConfirmar.setOnClickListener(v -> confirmarAjuste());

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
            etCodigo.setError("Ingrese el código");
            etCodigo.requestFocus();
            return;
        }

        SgaDatabase.databaseExecutor.execute(() -> {
            Producto encontrado =
                    database.productoDao().buscarPorCodigo(codigo);

            runOnUiThread(() -> {
                productoSeleccionado = encontrado;
                btnConfirmar.setEnabled(false);

                if (encontrado == null) {
                    tvProducto.setText("Producto no encontrado");
                    btnCalcular.setEnabled(false);

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
                                    "\nStock del sistema: " +
                                    encontrado.getCantidad() +
                                    " unidades" +
                                    "\nUbicación: " +
                                    encontrado.getUbicacion()
                    );

                    tvResultado.setText(
                            "Ingrese el conteo físico"
                    );

                    btnCalcular.setEnabled(true);
                }
            });
        });
    }

    private void calcularDiferencia() {
        if (productoSeleccionado == null) {
            return;
        }

        String conteoTexto =
                etConteoFisico.getText().toString().trim();

        if (TextUtils.isEmpty(conteoTexto)) {
            etConteoFisico.setError("Ingrese el conteo físico");
            etConteoFisico.requestFocus();
            return;
        }

        conteoCalculado = Integer.parseInt(conteoTexto);

        if (conteoCalculado < 0) {
            etConteoFisico.setError(
                    "El conteo no puede ser negativo"
            );
            etConteoFisico.requestFocus();
            return;
        }

        int stockSistema = productoSeleccionado.getCantidad();
        diferenciaCalculada = conteoCalculado - stockSistema;

        if (diferenciaCalculada == 0) {
            tvResultado.setText(
                    "SIN DIFERENCIAS\nEl inventario es correcto"
            );
            tvResultado.setTextColor(0xFF2E7D32);
            btnConfirmar.setEnabled(false);
        } else {
            String signo =
                    diferenciaCalculada > 0 ? "+" : "";

            tvResultado.setText(
                    "DIFERENCIA: " +
                            signo + diferenciaCalculada +
                            "\nSistema: " + stockSistema +
                            " | Físico: " + conteoCalculado
            );

            tvResultado.setTextColor(0xFFC62828);
            btnConfirmar.setEnabled(true);
        }
    }

    private void confirmarAjuste() {
        String motivo = etMotivo.getText().toString().trim();

        if (TextUtils.isEmpty(motivo)) {
            etMotivo.setError(
                    "Explique el motivo de la diferencia"
            );
            etMotivo.requestFocus();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar ajuste")
                .setMessage(
                        "El stock cambiará de " +
                                productoSeleccionado.getCantidad() +
                                " a " + conteoCalculado +
                                ". ¿Deseas continuar?"
                )
                .setPositiveButton(
                        "Confirmar",
                        (dialog, which) -> guardarAjuste(motivo)
                )
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarAjuste(String motivo) {
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

                    productoActual.setCantidad(conteoCalculado);
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
                            "AJUSTE",
                            Math.abs(conteoCalculado - stockAnterior),
                            stockAnterior,
                            conteoCalculado,
                            usuario,
                            fechaHora,
                            false
                    );

                    database.movimientoDao().insertar(movimiento);
                });

                runOnUiThread(() -> {
                    Toast.makeText(
                            this,
                            "Ajuste registrado: " + motivo,
                            Toast.LENGTH_LONG
                    ).show();

                    finish();
                });

            } catch (Exception error) {
                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "No se pudo registrar el ajuste",
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }
}