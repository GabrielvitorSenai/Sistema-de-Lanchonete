package br.com.lanchonete.model.repository;

import java.util.List;

import br.com.lanchonete.model.entity.Produto;
import br.com.lanchonete.model.enums.CategoriaProduto;

public interface ProdutoRepository extends CrudRepository<Produto, Long> {

    List<Produto> findByCategoriaAtivos(CategoriaProduto categoria);
}
