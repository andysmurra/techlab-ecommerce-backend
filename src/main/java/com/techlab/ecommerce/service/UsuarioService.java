package com.techlab.ecommerce.service;

import com.techlab.ecommerce.exception.OperacionInvalidaException;
import com.techlab.ecommerce.exception.RecursoNoEncontradoException;
import com.techlab.ecommerce.model.Usuario;
import com.techlab.ecommerce.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario con ID: " + id));
    }

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new OperacionInvalidaException("Ya existe un usuario registrado con el email: " + usuario.getEmail());
        }
        return usuarioRepository.save(usuario);
    }
}
