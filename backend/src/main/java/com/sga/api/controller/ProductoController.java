package com.sga.api.controller;

import com.sga.api.model.Producto;
import com.sga.api.service.ProductoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(
            ProductoService productoService
    ) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listar(
            @RequestParam(
                    name = "buscar",
                    required = false
            ) String buscar
    ) {
        List<Producto> productos;

        if (buscar == null || buscar.trim().isEmpty()) {
            productos = productoService.listarTodos();
        } else {
            productos = productoService.buscar(buscar);
        }

        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(
            @PathVariable Long id
    ) {
        Producto producto =
                productoService.buscarPorId(id);

        return ResponseEntity.ok(producto);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Producto> buscarPorCodigo(
            @PathVariable String codigo
    ) {
        Producto producto =
                productoService.buscarPorCodigo(codigo);

        return ResponseEntity.ok(producto);
    }

    @PostMapping
    public ResponseEntity<Producto> registrar(
            @RequestBody Producto producto
    ) {
        Producto productoGuardado =
                productoService.registrar(producto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productoGuardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(
            @PathVariable Long id,
            @RequestBody Producto producto
    ) {
        Producto productoActualizado =
                productoService.actualizar(id, producto);

        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id
    ) {
        productoService.eliminar(id);

        return ResponseEntity.noContent().build();
    }
}