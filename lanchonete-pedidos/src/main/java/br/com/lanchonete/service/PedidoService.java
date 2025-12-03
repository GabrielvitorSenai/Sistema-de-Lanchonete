package br.com.lanchonete.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.entity.ItemPedido;
import br.com.lanchonete.model.entity.Pedido;
import br.com.lanchonete.model.entity.Produto;
import br.com.lanchonete.model.enums.StatusPedido;
import br.com.lanchonete.model.repository.PedidoRepository;
import br.com.lanchonete.model.repository.ProdutoRepository;

public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    
    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    /**
     * Versão nova: cria pedido já recebendo o número da mesa.
     */
    public Pedido criarPedido(Cliente cliente, List<ItemPedido> itens, String numeroMesa) {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setNumeroMesa(numeroMesa);                    // 🔹 mesa do cliente
        pedido.setDataCriacao(LocalDateTime.now());          // 🔹 data de criação

        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedido item : itens) {
            Produto produto = item.getProduto();

            // calcula subtotal do item
            item.setSubtotal(produto.getPreco()
                    .multiply(BigDecimal.valueOf(item.getQuantidade())));

            // controla estoque
            int novoEstoque = produto.getEstoque() - item.getQuantidade();
            if (novoEstoque < 0) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto: " + produto.getNome());
            }
            produto.setEstoque(novoEstoque);
            produtoRepository.update(produto);

            total = total.add(item.getSubtotal());
            pedido.adicionarItem(item);
        }

        pedido.setValorTotal(total);
        pedido.setStatus(StatusPedido.AGUARDANDO_PRODUCAO);

        return pedidoRepository.save(pedido);
    }

    /**
     * Versão antiga mantida por compatibilidade.
     * Se alguém chamar sem mesa, grava null em numero_mesa.
     */
    public Pedido criarPedido(Cliente cliente, List<ItemPedido> itens) {
        return criarPedido(cliente, itens, null);
    }

    public void atualizarStatus(Pedido pedido, StatusPedido novoStatus) {
        pedido.setStatus(novoStatus);
        pedidoRepository.update(pedido);
    }

    public List<ItemPedido> listarItensDoPedido(Long pedidoId) {
        return pedidoRepository.listarItensDoPedido(pedidoId);
    }
}
