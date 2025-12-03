package br.com.lanchonete.model.entity;

import java.math.BigDecimal;

import br.com.lanchonete.model.enums.CategoriaProduto;

public class Produto {

    private Long id;
    private String nome;
    private String descricao;
    private CategoriaProduto categoria;
    private BigDecimal preco;
    private int estoque;
    private boolean ativo;

    public Produto() {
        this.ativo = true;
    }

    public Produto(Long id, String nome, String descricao,
                   CategoriaProduto categoria, BigDecimal preco,
                   int estoque, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.categoria = categoria;
        this.preco = preco;
        this.estoque = estoque;
        this.ativo = ativo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public CategoriaProduto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProduto categoria) { this.categoria = categoria; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public int getEstoque() { return estoque; }
    public void setEstoque(int estoque) { this.estoque = estoque; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
