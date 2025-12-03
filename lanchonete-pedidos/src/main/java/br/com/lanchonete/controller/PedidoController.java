package br.com.lanchonete.controller;

import java.util.List;

import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.entity.ItemPedido;
import br.com.lanchonete.model.entity.Pedido;
import br.com.lanchonete.model.enums.StatusPedido;
import br.com.lanchonete.model.repository.PedidoRepository;
import br.com.lanchonete.service.FidelidadeService;
import br.com.lanchonete.service.PedidoService;

public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final FidelidadeService fidelidadeService;

    public PedidoController(PedidoService pedidoService,
                            PedidoRepository pedidoRepository,
                            FidelidadeService fidelidadeService) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
        this.fidelidadeService = fidelidadeService;
    }

    /**
     * Versão nova: cria pedido já recebendo o número da mesa.
     */
    public Pedido criarPedido(Cliente cliente, List<ItemPedido> itens, String numeroMesa) {
        return pedidoService.criarPedido(cliente, itens, numeroMesa);
    }

    /**
     * Versão antiga mantida por compatibilidade.
     */
    public Pedido criarPedido(Cliente cliente, List<ItemPedido> itens) {
        return pedidoService.criarPedido(cliente, itens, null);
    }

    public void atualizarStatus(Pedido pedido, StatusPedido novoStatus) {
        pedidoService.atualizarStatus(pedido, novoStatus);

        if (novoStatus == StatusPedido.CONCLUIDO) {
            fidelidadeService.adicionarPontoPorVisita(pedido.getCliente());
        }
    }

    public List<Pedido> listarPedidosDoCliente(Cliente cliente) {
        return pedidoRepository.findByCliente(cliente);
    }

    public List<Pedido> listarPendentesCozinha() {
        return pedidoRepository.findPendentesCozinha();
    }

    public List<Pedido> listarPendentesGarcom() {
        return pedidoRepository.findPendentesGarcom();
    }

    public List<ItemPedido> listarItensDoPedido(Long pedidoId) {
        return pedidoService.listarItensDoPedido(pedidoId);
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }
}
