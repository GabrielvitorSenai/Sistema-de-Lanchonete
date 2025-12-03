package br.com.lanchonete.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.lanchonete.model.enums.StatusPedido;

public class Pedido {

    private Long id;
    private Cliente cliente;
    private LocalDateTime dataCriacao;
    private StatusPedido status;
    private BigDecimal valorTotal;
    private List<ItemPedido> itens;

    public Pedido() {
        this.dataCriacao = LocalDateTime.now();
        this.status = StatusPedido.NOVO;
        this.itens = new ArrayList<>();
        this.valorTotal = BigDecimal.ZERO;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }

    public void adicionarItem(ItemPedido item) {
        if (this.itens == null) {
            this.itens = new ArrayList<>();
        }
        this.itens.add(item);
    }
    private String numeroMesa;

public String getNumeroMesa() {
    return numeroMesa;
}

public void setNumeroMesa(String numeroMesa) {
    this.numeroMesa = numeroMesa;
}

}
