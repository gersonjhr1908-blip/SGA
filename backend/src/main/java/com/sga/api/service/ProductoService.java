package com.sga.api.service;

import com.sga.api.model.Producto;
import com.sga.api.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(
            ProductoRepository productoRepository
    ) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado"
                        )
                );
    }

    public Producto buscarPorCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Producto no encontrado"
                        )
                );
    }

    public List<Producto> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarTodos();
        }

        String busqueda = texto.trim();

        return productoRepository
                .findByNombreContainingIgnoreCaseOrCodigoContainingIgnoreCase(
                        busqueda,
                        busqueda
                );
    }

    @Transactional
    public Producto registrar(Producto producto) {
        validarProducto(producto);

        String codigo = producto.getCodigo().trim();

        if (productoRepository.existsByCodigo(codigo)) {
            throw new IllegalArgumentException(
                    "Ya existe un producto con ese código"
            );
        }

        producto.setId(null);
        producto.setCodigo(codigo);
        producto.setNombre(producto.getNombre().trim());

        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizar(
            Long id,
            Producto datosProducto
    ) {
        validarProducto(datosProducto);

        Producto productoActual = buscarPorId(id);
        String codigoNuevo = datosProducto.getCodigo().trim();

        productoRepository.findByCodigo(codigoNuevo)
                .filter(producto ->
                        !producto.getId().equals(id)
                )
                .ifPresent(producto -> {
                    throw new IllegalArgumentException(
                            "Ya existe otro producto con ese código"
                    );
                });

        productoActual.setCodigo(codigoNuevo);
        productoActual.setNombre(
                datosProducto.getNombre().trim()
        );
        productoActual.setDescripcion(
                datosProducto.getDescripcion()
        );
        productoActual.setCantidad(
                datosProducto.getCantidad()
        );
        productoActual.setUbicacion(
                datosProducto.getUbicacion()
        );
        productoActual.setLote(
                datosProducto.getLote()
        );
        productoActual.setFechaVencimiento(
                datosProducto.getFechaVencimiento()
        );
        productoActual.setCodigoBarras(
                datosProducto.getCodigoBarras()
        );

        return productoRepository.save(productoActual);
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarPorId(id);
        productoRepository.delete(producto);
    }

    private void validarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException(
                    "Los datos del producto son obligatorios"
            );
        }

        if (producto.getCodigo() == null ||
                producto.getCodigo().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El código es obligatorio"
            );
        }

        if (producto.getNombre() == null ||
                producto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio"
            );
        }

        if (producto.getCantidad() == null) {
            throw new IllegalArgumentException(
                    "La cantidad es obligatoria"
            );
        }

        if (producto.getCantidad() < 0) {
            throw new IllegalArgumentException(
                    "La cantidad no puede ser negativa"
            );
        }
    }
}