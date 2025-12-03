package br.com.lanchonete.model.repository.impl;

import br.com.lanchonete.config.ConnectionFactory;
import br.com.lanchonete.model.entity.Produto;
import br.com.lanchonete.model.enums.CategoriaProduto;
import br.com.lanchonete.model.repository.ProdutoRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoRepositoryImpl implements ProdutoRepository {

    @Override
    public Produto save(Produto produto) {
        String sql = "INSERT INTO produto (nome, descricao, categoria, preco, estoque, ativo) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
    
        System.out.println("DEBUG ProdutoRepository.save()");
        System.out.println("  Produto recebido:");
        System.out.println("    nome      = " + produto.getNome());
        System.out.println("    descricao = " + produto.getDescricao());
        System.out.println("    categoria = " + produto.getCategoria());
        System.out.println("    preco     = " + produto.getPreco());
        System.out.println("    estoque   = " + produto.getEstoque());
        System.out.println("    ativo     = " + produto.isAtivo());
    
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setString(3, produto.getCategoria().name());
            stmt.setBigDecimal(4, produto.getPreco());
            stmt.setInt(5, produto.getEstoque());
            stmt.setBoolean(6, produto.isAtivo());
    
            System.out.println("DEBUG: Executando INSERT...");
            int affected = stmt.executeUpdate();
            System.out.println("DEBUG: Linhas afetadas = " + affected);
    
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    long id = rs.getLong(1);
                    produto.setId(id);
                    System.out.println("DEBUG: ID gerado = " + id);
                }
            }
    
            return produto;
    
        } catch (Exception e) {
            System.out.println("ERRO REAL AO SALVAR PRODUTO:");
            e.printStackTrace();  // <--- agora vamos ver o erro de verdade!
            return null;
        }
    }
    
    @Override
    public Produto update(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, descricao = ?, categoria = ?, " +
                     "preco = ?, estoque = ?, ativo = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getDescricao());
            ps.setString(3, produto.getCategoria().name());
            ps.setBigDecimal(4, produto.getPreco());
            ps.setInt(5, produto.getEstoque());
            ps.setBoolean(6, produto.isAtivo());
            ps.setLong(7, produto.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto", e);
        }
        return produto;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar produto", e);
        }
    }

    @Override
    public Optional<Produto> findById(Long id) {
        String sql = "SELECT * FROM produto WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto por id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Produto> findAll() {
        String sql = "SELECT * FROM produto";
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos", e);
        }
        return lista;
    }

    @Override
    public List<Produto> findByCategoriaAtivos(CategoriaProduto categoria) {
        String sql = "SELECT * FROM produto WHERE categoria = ? AND ativo = 1";
        List<Produto> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categoria.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por categoria", e);
        }
        return lista;
    }

    private Produto mapRow(ResultSet rs) throws SQLException {
        Produto p = new Produto();
        p.setId(rs.getLong("id"));
        p.setNome(rs.getString("nome"));
        p.setDescricao(rs.getString("descricao"));
        p.setCategoria(CategoriaProduto.valueOf(rs.getString("categoria")));
        p.setPreco(rs.getBigDecimal("preco"));
        p.setEstoque(rs.getInt("estoque"));
        p.setAtivo(rs.getBoolean("ativo"));
        return p;
        
    }
    public Produto findByNome(String nome) {
    String sql = "SELECT * FROM produto WHERE nome = ?";
    try (Connection conn = ConnectionFactory.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, nome);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Produto p = mapRow(rs);
            return p;
        }

    } catch (SQLException e) {
        throw new RuntimeException("Erro ao buscar produto por nome", e);
    }
    return null;
}

}
