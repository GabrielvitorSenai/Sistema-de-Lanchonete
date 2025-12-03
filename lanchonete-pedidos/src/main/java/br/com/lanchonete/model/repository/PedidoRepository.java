package br.com.lanchonete.model.repository;

import java.util.List;

import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.entity.ItemPedido;
import br.com.lanchonete.model.entity.Pedido;
import br.com.lanchonete.model.enums.StatusPedido;

public interface PedidoRepository extends CrudRepository<Pedido, Long> {

    List<Pedido> findByCliente(Cliente cliente);

    List<Pedido> findByStatus(StatusPedido status);

    List<Pedido> findPendentesCozinha();

    List<Pedido> findPendentesGarcom();

    public List<ItemPedido> listarItensDoPedido(Long pedidoId);
}
