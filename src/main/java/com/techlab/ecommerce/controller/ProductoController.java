package com.techlab.ecommerce.controller;

import com.techlab.ecommerce.dto.ProductoRequestDTO;
import com.techlab.ecommerce.model.Producto;
import com.techlab.ecommerce.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer categoriaId) {
        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
        }
        if (categoriaId != null) {
            return ResponseEntity.ok(productoService.listarPorCategoria(categoriaId));
        }
        return ResponseEntity.ok(productoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProducto(@PathVariable Integer id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> alertasStockBajo(
            @RequestParam(defaultValue = "5") int limite) {
        return ResponseEntity.ok(productoService.obtenerProductosStockBajo(limite));
    }

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoRequestDTO dto) {
        Producto creado = productoService.crearProducto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoRequestDTO dto) {
        Producto actualizado = productoService.actualizarProducto(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Producto> actualizarStock(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body) {
        Integer nuevoStock = body.get("stock");
        return ResponseEntity.ok(productoService.actualizarStock(id, nuevoStock));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
