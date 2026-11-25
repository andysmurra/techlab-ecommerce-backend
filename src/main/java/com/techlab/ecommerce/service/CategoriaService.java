package com.techlab.ecommerce.service;

import com.techlab.ecommerce.exception.OperacionInvalidaException;
import com.techlab.ecommerce.exception.RecursoNoEncontradoException;
import com.techlab.ecommerce.model.Categoria;
import com.techlab.ecommerce.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria obtenerPorId(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la categoría con ID: " + id));
    }

    @Transactional
    public Categoria crearCategoria(Categoria categoria) {
        categoriaRepository.findByNombreIgnoreCase(categoria.getNombre()).ifPresent(c -> {
            throw new OperacionInvalidaException("Ya existe una categoría con el nombre: " + categoria.getNombre());
        });
        return categoriaRepository.save(categoria);
    }

    @Transactional
    public Categoria actualizarCategoria(Integer id, Categoria datos) {
        Categoria existente = obtenerPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        return categoriaRepository.save(existente);
    }

    @Transactional
    public void eliminarCategoria(Integer id) {
        Categoria existente = obtenerPorId(id);
        categoriaRepository.delete(existente);
    }
}
