package com.sga.almacenes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sga.almacenes.adapters.MovimientoAdapter;
import com.sga.almacenes.data.local.Movimiento;
import com.sga.almacenes.data.local.SgaDatabase;

import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private MovimientoAdapter movimientoAdapter;
    private TextView tvSinMovimientos;
    private SgaDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        database = SgaDatabase.obtenerInstancia(this);

        RecyclerView rvMovimientos =
                findViewById(R.id.rvMovimientos);

        tvSinMovimientos =
                findViewById(R.id.tvSinMovimientos);

        Button btnTodos =
                findViewById(R.id.btnTodosMovimientos);

        Button btnEntradas =
                findViewById(R.id.btnEntradas);

        Button btnSalidas =
                findViewById(R.id.btnSalidas);

        Button btnAjustes =
                findViewById(R.id.btnAjustes);

        Button btnVolver =
                findViewById(R.id.btnVolverHistorial);

        movimientoAdapter = new MovimientoAdapter();

        rvMovimientos.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvMovimientos.setAdapter(movimientoAdapter);

        btnTodos.setOnClickListener(v -> cargarTodos());

        btnEntradas.setOnClickListener(
                v -> cargarPorTipo("ENTRADA")
        );

        btnSalidas.setOnClickListener(
                v -> cargarPorTipo("SALIDA")
        );

        btnAjustes.setOnClickListener(
                v -> cargarPorTipo("AJUSTE")
        );

        btnVolver.setOnClickListener(v -> finish());

        cargarTodos();
    }

    private void cargarTodos() {
        SgaDatabase.databaseExecutor.execute(() -> {
            List<Movimiento> movimientos =
                    database.movimientoDao().listarTodos();

            mostrarMovimientos(movimientos);
        });
    }

    private void cargarPorTipo(String tipo) {
        SgaDatabase.databaseExecutor.execute(() -> {
            List<Movimiento> movimientos =
                    database.movimientoDao().listarPorTipo(tipo);

            mostrarMovimientos(movimientos);
        });
    }

    private void mostrarMovimientos(
            List<Movimiento> movimientos
    ) {
        runOnUiThread(() -> {
            movimientoAdapter.actualizarLista(movimientos);

            if (movimientos.isEmpty()) {
                tvSinMovimientos.setVisibility(View.VISIBLE);
            } else {
                tvSinMovimientos.setVisibility(View.GONE);
            }
        });
    }
}