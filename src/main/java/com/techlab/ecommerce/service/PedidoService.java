package com.techlab.ecommerce.service;

import com.techlab.ecommerce.dto.ItemPedidoDTO;
import com.techlab.ecommerce.dto.PedidoRequestDTO;
import com.techlab.ecommerce.exception.OperacionInvalidaException;
import com.techlab.ecommerce.exception.RecursoNoEncontradoException;
import com.techlab.ecommerce.exception.StockInsuficienteException;
import com.techlab.ecommerce.model.*;
import com.techlab.ecommerce.repository.PedidoRepository;
import com.techlab.ecommerce.repository.ProductoRepository;
import com.techlab.ecommerce.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pedido obtenerPorId(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el pedido con ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPedidosPorUsuario(Integer usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNoEncontradoException("No se encontró el usuario con ID: " + usuarioId);
        }
        return pedidoRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuarioId);
    }

    @Transactional
    public Pedido crearPedido(PedidoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el usuario con ID: " + request.getUsuarioId()));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        for (ItemPedidoDTO item : request.getItemsPedido()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "No se encontró el producto con ID: " + item.getProductoId()));

            if (producto.getStock() < item.getCantidad()) {
                throw new StockInsuficienteException(
                        String.format("Stock insuficiente para '%s'. Disponible: %d, solicitado: %d",
                                producto.getNombre(), producto.getStock(), item.getCantidad()));
            }

            // Descontar stock automáticamente
            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);

            LineaPedido linea = new LineaPedido();
            linea.setProducto(producto);
            linea.setCantidad(item.getCantidad());
            linea.setPrecioUnitario(producto.getPrecio());
            pedido.agregarLinea(linea);
        }

        pedido.calcularTotal();
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido actualizarEstado(Integer pedidoId, String nuevoEstadoStr) {
        Pedido pedido = obtenerPorId(pedidoId);

        EstadoPedido nuevoEstado;
        try {
            nuevoEstado = EstadoPedido.valueOf(nuevoEstadoStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OperacionInvalidaException(
                    "Estado inválido '" + nuevoEstadoStr + "'. Valores permitidos: PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO");
        }

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new OperacionInvalidaException("No se puede modificar un pedido que ya fue CANCELADO");
        }

        // Si el pedido se cancela, devolver el stock a cada producto
        if (nuevoEstado == EstadoPedido.CANCELADO) {
            for (LineaPedido linea : pedido.getLineas()) {
                Producto producto = linea.getProducto();
                producto.setStock(producto.getStock() + linea.getCantidad());
                productoRepository.save(producto);
            }
        }

        pedido.setEstado(nuevoEstado);
        return pedidoRepository.save(pedido);
    }
}
