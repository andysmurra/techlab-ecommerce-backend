package com.techlab.ecommerce.controller;

import com.techlab.ecommerce.model.Pedido;
import com.techlab.ecommerce.model.Usuario;
import com.techlab.ecommerce.service.PedidoService;
import com.techlab.ecommerce.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crearUsuario(usuario));
    }

    @GetMapping("/{id}/pedidos")
    public ResponseEntity<List<Pedido>> obtenerHistorialPedidos(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.listarPedidosPorUsuario(id));
    }
}
