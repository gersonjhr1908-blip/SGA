package com.sga.almacenes.data.remote;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductoApiService {

    @GET("api/productos")
    Call<List<ApiProducto>> listarProductos();

    @GET("api/productos")
    Call<List<ApiProducto>> buscarProductos(
            @Query("buscar") String texto
    );

    @GET("api/productos/{id}")
    Call<ApiProducto> buscarPorId(
            @Path("id") Long id
    );

    @GET("api/productos/codigo/{codigo}")
    Call<ApiProducto> buscarPorCodigo(
            @Path("codigo") String codigo
    );

    @POST("api/productos")
    Call<ApiProducto> registrar(
            @Body ApiProducto producto
    );

    @PUT("api/productos/{id}")
    Call<ApiProducto> actualizar(
            @Path("id") Long id,
            @Body ApiProducto producto
    );

    @DELETE("api/productos/{id}")
    Call<Void> eliminar(
            @Path("id") Long id
    );
}