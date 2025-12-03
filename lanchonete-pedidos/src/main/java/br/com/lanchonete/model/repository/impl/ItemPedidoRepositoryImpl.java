package br.com.lanchonete.model.repository.impl;

import br.com.lanchonete.config.ConnectionFactory;
import br.com.lanchonete.model.entity.ItemPedido;
import br.com.lanchonete.model.entity.Pedido;
import br.com.lanchonete.model.entity.Produto;
import br.com.lanchonete.model.repository.ItemPedidoRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemPedidoRepositoryImpl implements ItemPedidoRepository {

    @Override
    public ItemPedido save(ItemPedido i) {
        String sql = "INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, i.getPedido().getId());
            ps.setLong(2, i.getProduto().getId());
            ps.setInt(3, i.getQuantidade());
            ps.setBigDecimal(4, i.getPrecoUnitario());
            ps.setBigDecimal(5, i.getSubtotal());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                i.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar item de pedido", e);
        }
        return i;
    }

    @Override
    public ItemPedido update(ItemPedido i) {
        String sql = "UPDATE item_pedido SET pedido_id = ?, produto_id = ?, quantidade = ?, " +
                     "preco_unitario = ?, subtotal = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, i.getPedido().getId());
            ps.setLong(2, i.getProduto().getId());
            ps.setInt(3, i.getQuantidade());
            ps.setBigDecimal(4, i.getPrecoUnitario());
            ps.setBigDecimal(5, i.getSubtotal());
            ps.setLong(6, i.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item de pedido", e);
        }
        return i;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM item_pedido WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar item de pedido", e);
        }
    }

    @Override
    public Optional<ItemPedido> findById(Long id) {
        String sql = "SELECT * FROM item_pedido WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar item de pedido por id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ItemPedido> findAll() {
        String sql = "SELECT * FROM item_pedido";
        List<ItemPedido> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens de pedido", e);
        }
        return lista;
    }

    @Override
    public List<ItemPedido> findByPedido(Pedido pedido) {
        String sql = "SELECT * FROM item_pedido WHERE pedido_id = ?";
        List<ItemPedido> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, pedido.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens por pedido", e);
        }
        return lista;
    }

    private ItemPedido mapRow(ResultSet rs) throws SQLException {
        ItemPedido i = new ItemPedido();
        i.setId(rs.getLong("id"));

        Pedido p = new Pedido();
        p.setId(rs.getLong("pedido_id"));
        i.setPedido(p);

        Produto prod = new Produto();
        prod.setId(rs.getLong("produto_id"));
        i.setProduto(prod);

        i.setQuantidade(rs.getInt("quantidade"));
        i.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
        i.setSubtotal(rs.getBigDecimal("subtotal"));
        return i;
    }
}
