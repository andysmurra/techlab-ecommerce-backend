package com.techlab.ecommerce.repository;

import com.techlab.ecommerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByCategoriaId(Integer categoriaId);
    List<Producto> findByStockLessThanEqual(Integer stockMinimo);
}
