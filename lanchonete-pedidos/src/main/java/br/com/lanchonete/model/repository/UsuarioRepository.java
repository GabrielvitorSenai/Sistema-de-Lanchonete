package br.com.lanchonete.model.repository;

import java.util.Optional;

import br.com.lanchonete.model.entity.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
}
