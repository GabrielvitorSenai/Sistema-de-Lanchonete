package br.com.lanchonete.controller;

import java.math.BigDecimal;
import java.util.List;

import br.com.lanchonete.model.entity.Produto;
import br.com.lanchonete.model.enums.CategoriaProduto;
import br.com.lanchonete.model.repository.ProdutoRepository;

public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto salvarProduto(String nome, String descricao,
                                 CategoriaProduto categoria, BigDecimal preco,
                                 int estoque, boolean ativo) {

        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setCategoria(categoria);
        produto.setPreco(preco);
        produto.setEstoque(estoque);
        produto.setAtivo(ativo);

        return produtoRepository.save(produto);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }
}
