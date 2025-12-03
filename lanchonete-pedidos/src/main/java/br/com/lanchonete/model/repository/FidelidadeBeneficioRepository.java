package br.com.lanchonete.model.repository;

import java.util.List;

import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.entity.FidelidadeBeneficio;
import br.com.lanchonete.model.enums.TipoBeneficio;

public interface FidelidadeBeneficioRepository extends CrudRepository<FidelidadeBeneficio, Long> {

    List<FidelidadeBeneficio> findAtivosByCliente(Cliente cliente);

    FidelidadeBeneficio findPrimeiroAtivoByClienteAndTipo(Cliente cliente, TipoBeneficio tipo);
}
