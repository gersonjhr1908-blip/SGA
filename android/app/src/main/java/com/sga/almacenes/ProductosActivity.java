package com.sga.almacenes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sga.almacenes.adapters.ProductoAdapter;
import com.sga.almacenes.data.local.Producto;
import com.sga.almacenes.data.local.SgaDatabase;
import com.sga.almacenes.data.remote.ApiProducto;
import com.sga.almacenes.data.remote.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductosActivity extends AppCompatActivity {

    private EditText etBuscarProducto;
    private TextView tvSinProductos;
    private ProductoAdapter productoAdapter;
    private SgaDatabase database;
    private String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        database = SgaDatabase.obtenerInstancia(this);

        rol = getIntent().getStringExtra("rol");

        if (rol == null) {
            rol = "OPERARIO";
        }

        etBuscarProducto =
                findViewById(R.id.etBuscarProducto);

        tvSinProductos =
                findViewById(R.id.tvSinProductos);

        Button btnBuscarProducto =
                findViewById(R.id.btnBuscarProducto);

        Button btnNuevoProducto =
                findViewById(R.id.btnNuevoProducto);

        Button btnVolverMenu =
                findViewById(R.id.btnVolverMenu);

        RecyclerView rvProductos =
                findViewById(R.id.rvProductos);

        if ("OPERARIO".equals(rol)) {
            btnNuevoProducto.setVisibility(View.GONE);
        }

        String rolActual = rol;

        productoAdapter = new ProductoAdapter(producto -> {
            Intent intent = new Intent(
                    this,
                    DetalleProductoActivity.class
            );

            intent.putExtra("producto_id", producto.getId());
            intent.putExtra("rol", rolActual);

            startActivity(intent);
        });

        rvProductos.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvProductos.setAdapter(productoAdapter);

        btnBuscarProducto.setOnClickListener(
                v -> buscarProducto()
        );

        btnNuevoProducto.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this,
                    RegistrarProductoActivity.class
            );

            startActivity(intent);
        });

        btnVolverMenu.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarProductos();
        descargarProductosServidor();
    }

    private void descargarProductosServidor() {

        Toast.makeText(
                this,
                "Conectando con Spring Boot...",
                Toast.LENGTH_SHORT
        ).show();

        RetrofitClient.productoApi()
                .listarProductos()
                .enqueue(new Callback<List<ApiProducto>>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<List<ApiProducto>> call,
                            @NonNull Response<List<ApiProducto>> response
                    ) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    ProductosActivity.this,
                                    "Error del servidor: " +
                                            response.code(),
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        List<ApiProducto> productos =
                                response.body();

                        if (productos == null) {
                            Toast.makeText(
                                    ProductosActivity.this,
                                    "El servidor envió una respuesta vacía",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        guardarProductosDescargados(productos);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<List<ApiProducto>> call,
                            @NonNull Throwable error
                    ) {
                        Log.e(
                                "SGA_API",
                                "Error al descargar productos",
                                error
                        );

                        Toast.makeText(
                                ProductosActivity.this,
                                "Error: " + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void guardarProductosDescargados(
            List<ApiProducto> productosApi
    ) {
        SgaDatabase.databaseExecutor.execute(() -> {
            try {
                for (ApiProducto apiProducto : productosApi) {

                    if (apiProducto.getCodigo() == null) {
                        continue;
                    }

                    Producto productoLocal =
                            database.productoDao().buscarPorCodigo(
                                    apiProducto.getCodigo()
                            );

                    if (productoLocal == null) {
                        Producto productoNuevo = new Producto(
                                apiProducto.getCodigo(),
                                apiProducto.getNombre(),
                                apiProducto.getDescripcion(),
                                obtenerCantidad(apiProducto),
                                apiProducto.getUbicacion(),
                                apiProducto.getLote(),
                                apiProducto.getFechaVencimiento(),
                                apiProducto.getCodigoBarras(),
                                true
                        );

                        database.productoDao().insertar(
                                productoNuevo
                        );

                    } else if (productoLocal.isSincronizado()) {
                        productoLocal.setNombre(
                                apiProducto.getNombre()
                        );

                        productoLocal.setDescripcion(
                                apiProducto.getDescripcion()
                        );

                        productoLocal.setCantidad(
                                obtenerCantidad(apiProducto)
                        );

                        productoLocal.setUbicacion(
                                apiProducto.getUbicacion()
                        );

                        productoLocal.setLote(
                                apiProducto.getLote()
                        );

                        productoLocal.setFechaVencimiento(
                                apiProducto.getFechaVencimiento()
                        );

                        productoLocal.setCodigoBarras(
                                apiProducto.getCodigoBarras()
                        );

                        productoLocal.setSincronizado(true);

                        database.productoDao().actualizar(
                                productoLocal
                        );
                    }
                }

                runOnUiThread(() -> {
                    cargarProductos();

                    Toast.makeText(
                            ProductosActivity.this,
                            "Productos sincronizados desde MySQL",
                            Toast.LENGTH_SHORT
                    ).show();
                });

            } catch (Exception error) {
                Log.e(
                        "SGA_ROOM",
                        "Error al guardar productos en Room",
                        error
                );

                runOnUiThread(() ->
                        Toast.makeText(
                                ProductosActivity.this,
                                "Error local: " +
                                        error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        });
    }

    private int obtenerCantidad(ApiProducto producto) {
        if (producto.getCantidad() == null) {
            return 0;
        }

        return producto.getCantidad();
    }

    private void cargarProductos() {
        SgaDatabase.databaseExecutor.execute(() -> {
            List<Producto> productos =
                    database.productoDao().listarTodos();

            mostrarProductos(productos);
        });
    }

    private void buscarProducto() {
        String texto =
                etBuscarProducto.getText().toString().trim();

        if (TextUtils.isEmpty(texto)) {
            cargarProductos();

            Toast.makeText(
                    this,
                    "Mostrando todos los productos",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SgaDatabase.databaseExecutor.execute(() -> {
            List<Producto> productos =
                    database.productoDao().buscar(texto);

            mostrarProductos(productos);
        });
    }

    private void mostrarProductos(
            List<Producto> productos
    ) {
        runOnUiThread(() -> {
            productoAdapter.actualizarLista(productos);

            if (productos.isEmpty()) {
                tvSinProductos.setVisibility(View.VISIBLE);
            } else {
                tvSinProductos.setVisibility(View.GONE);
            }
        });
    }
}