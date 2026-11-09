package com.sga.almacenes.data.remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL =
            "http://127.0.0.1:8081/";

    private static Retrofit retrofit;

    private RetrofitClient() {
    }

    public static Retrofit obtenerInstancia() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(
                            GsonConverterFactory.create()
                    )
                    .build();
        }

        return retrofit;
    }

    public static ProductoApiService productoApi() {
        return obtenerInstancia()
                .create(ProductoApiService.class);
    }
}
