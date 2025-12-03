package br.com.lanchonete.model.repository;

import java.util.List;

import br.com.lanchonete.model.entity.ItemPedido;
import br.com.lanchonete.model.entity.Pedido;

public interface ItemPedidoRepository extends CrudRepository<ItemPedido, Long> {

    List<ItemPedido> findByPedido(Pedido pedido);
}
