package com.techlab.ecommerce.service;

import com.techlab.ecommerce.dto.ProductoRequestDTO;
import com.techlab.ecommerce.exception.OperacionInvalidaException;
import com.techlab.ecommerce.exception.RecursoNoEncontradoException;
import com.techlab.ecommerce.model.Categoria;
import com.techlab.ecommerce.model.Producto;
import com.techlab.ecommerce.repository.CategoriaRepository;
import com.techlab.ecommerce.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto obtenerPorId(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el producto con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(Integer categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            throw new RecursoNoEncontradoException("No se encontró la categoría con ID: " + categoriaId);
        }
        return productoRepository.findByCategoriaId(categoriaId);
    }

    @Transactional(readOnly = true)
    public List<Producto> obtenerProductosStockBajo(int limite) {
        if (limite < 0) {
            throw new OperacionInvalidaException("El límite de stock mínimo no puede ser negativo");
        }
        return productoRepository.findByStockLessThanEqual(limite);
    }

    @Transactional
    public Producto crearProducto(ProductoRequestDTO dto) {
        Producto producto = new Producto();
        mapearDtoAEntidad(dto, producto);
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto actualizarProducto(Integer id, ProductoRequestDTO dto) {
        Producto productoExistente = obtenerPorId(id);
        mapearDtoAEntidad(dto, productoExistente);
        return productoRepository.save(productoExistente);
    }

    @Transactional
    public Producto actualizarStock(Integer id, Integer nuevoStock) {
        if (nuevoStock == null || nuevoStock < 0) {
            throw new OperacionInvalidaException("El stock debe ser un número entero mayor o igual a 0");
        }
        Producto producto = obtenerPorId(id);
        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }

    @Transactional
    public void eliminarProducto(Integer id) {
        Producto producto = obtenerPorId(id);
        productoRepository.delete(producto);
    }

    private void mapearDtoAEntidad(ProductoRequestDTO dto, Producto producto) {
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setImagenUrl(dto.getImagenUrl());
        producto.setStock(dto.getStock());

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró la categoría con ID: " + dto.getCategoriaId()));
            producto.setCategoria(categoria);
        } else {
            producto.setCategoria(null);
        }
    }
}
