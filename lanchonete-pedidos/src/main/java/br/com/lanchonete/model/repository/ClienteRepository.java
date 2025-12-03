package br.com.lanchonete.model.repository;

import java.util.Optional;

import br.com.lanchonete.model.entity.Cliente;

public interface ClienteRepository extends CrudRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);
}
