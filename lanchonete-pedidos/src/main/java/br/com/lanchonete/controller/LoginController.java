package br.com.lanchonete.controller;

import java.util.Optional;

import br.com.lanchonete.model.entity.Usuario;
import br.com.lanchonete.model.repository.UsuarioRepository;

public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        if (!opt.isPresent()) {
            return Optional.empty();
        }
        Usuario u = opt.get();
        if (!u.isAtivo() || !u.getSenha().equals(senha)) {
            return Optional.empty();
        }
        return Optional.of(u);
    }
}
