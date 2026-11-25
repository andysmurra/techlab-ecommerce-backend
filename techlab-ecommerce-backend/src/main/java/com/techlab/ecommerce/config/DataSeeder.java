package com.techlab.ecommerce.config;

import com.techlab.ecommerce.model.Categoria;
import com.techlab.ecommerce.model.Producto;
import com.techlab.ecommerce.model.Usuario;
import com.techlab.ecommerce.repository.CategoriaRepository;
import com.techlab.ecommerce.repository.ProductoRepository;
import com.techlab.ecommerce.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() == 0) {
            Categoria catCafeteria = categoriaRepository.save(new Categoria(null, "Cafetería", "Granos seleccionados y cápsulas"));
            Categoria catTecnologia = categoriaRepository.save(new Categoria(null, "Tecnología", "Periféricos y accesorios de oficina"));
            Categoria catAccesorios = categoriaRepository.save(new Categoria(null, "Accesorios", "Tazas térmicas, filtros y complementos"));

            productoRepository.saveAll(List.of(
                    new Producto(null, "Café Colombiano Premium 500g", "Café tostado molido de origen colombiano, notas achocolatadas", 14500.0, "https://images.unsplash.com/photo-1559056199-641a0ac8b55e", 50, catCafeteria),
                    new Producto(null, "Café Espresso Italiano 1kg", "Blend intenso ideal para máquinas espresso", 26800.0, "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd", 30, catCafeteria),
                    new Producto(null, "Teclado Mecánico Inalámbrico", "Switches táctiles silenciosos, retroiluminación blanca", 65000.0, "https://images.unsplash.com/photo-1587829741301-dc798b83add3", 15, catTecnologia),
                    new Producto(null, "Taza Térmica Acero Inoxidable", "Mantiene temperatura por 6 horas, capacidad 350ml", 18900.0, "https://images.unsplash.com/photo-1517256064527-09c73fc73e38", 4, catAccesorios)
            ));
        }

        if (usuarioRepository.count() == 0) {
            usuarioRepository.saveAll(List.of(
                    new Usuario(null, "Andrés Gómez", "andres@techlab.com", "ADMIN"),
                    new Usuario(null, "Lucía Fernández", "lucia@techlab.com", "CLIENTE")
            ));
        }
    }
}
