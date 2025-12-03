package br.com.lanchonete.model.repository.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import br.com.lanchonete.config.ConnectionFactory;
import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.entity.ItemPedido;
import br.com.lanchonete.model.entity.Pedido;
import br.com.lanchonete.model.enums.StatusPedido;
import br.com.lanchonete.model.repository.PedidoRepository;

public class PedidoRepositoryImpl implements PedidoRepository {

    private final ItemPedidoRepositoryImpl itemRepo = new ItemPedidoRepositoryImpl();

    @Override
    public Pedido save(Pedido pedido) {
        String sql = "INSERT INTO pedido (cliente_id, data_criacao, status, valor_total, numero_mesa) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, pedido.getCliente().getId());
            ps.setTimestamp(2, Timestamp.valueOf(pedido.getDataCriacao()));
            ps.setString(3, pedido.getStatus().name());
            ps.setBigDecimal(4, pedido.getValorTotal());
            ps.setString(5, pedido.getNumeroMesa()); // NOVO: mesa

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                pedido.setId(rs.getLong(1));
            }

            // salva itens vinculados ao pedido
            for (ItemPedido item : pedido.getItens()) {
                item.setPedido(pedido);
                itemRepo.save(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar pedido", e);
        }
        return pedido;
    }

    @Override
    public Pedido update(Pedido pedido) {
        String sql = "UPDATE pedido SET cliente_id = ?, data_criacao = ?, status = ?, valor_total = ?, numero_mesa = ? " +
                     "WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, pedido.getCliente().getId());
            ps.setTimestamp(2, Timestamp.valueOf(pedido.getDataCriacao()));
            ps.setString(3, pedido.getStatus().name());
            ps.setBigDecimal(4, pedido.getValorTotal());
            ps.setString(5, pedido.getNumeroMesa()); // NOVO: mesa
            ps.setLong(6, pedido.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pedido", e);
        }
        return pedido;
    }

    @Override
    public void delete(Long id) {
        String sqlItens = "DELETE FROM item_pedido WHERE pedido_id = ?";
        String sqlPedido = "DELETE FROM pedido WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlItens)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlPedido)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar pedido", e);
        }
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        String sql = "SELECT * FROM pedido WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Pedido p = mapRow(rs);
                p.setItens(itemRepo.findByPedido(p));
                return Optional.of(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedido por id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Pedido> findAll() {
        String sql = "SELECT * FROM pedido";
        List<Pedido> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedido p = mapRow(rs);
                p.setItens(itemRepo.findByPedido(p));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos", e);
        }
        return lista;
    }

    @Override
    public List<Pedido> findByCliente(Cliente cliente) {
        String sql = "SELECT * FROM pedido WHERE cliente_id = ? ORDER BY data_criacao DESC";
        List<Pedido> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cliente.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pedido p = mapRow(rs);
                p.setItens(itemRepo.findByPedido(p));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedidos por cliente", e);
        }
        return lista;
    }

    @Override
    public List<Pedido> findByStatus(StatusPedido status) {
        String sql = "SELECT * FROM pedido WHERE status = ?";
        List<Pedido> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Pedido p = mapRow(rs);
                p.setItens(itemRepo.findByPedido(p));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedidos por status", e);
        }
        return lista;
    }

    @Override
    public List<Pedido> findPendentesCozinha() {
        String sql = "SELECT * FROM pedido WHERE status IN ('AGUARDANDO_PRODUCAO', 'EM_PRODUCAO') ORDER BY data_criacao ASC";
        List<Pedido> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedido p = mapRow(rs);
                p.setItens(itemRepo.findByPedido(p));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedidos pendentes para cozinha", e);
        }
        return lista;
    }

    @Override
    public List<Pedido> findPendentesGarcom() {
        String sql = "SELECT * FROM pedido WHERE status IN ('PRONTO', 'A_CAMINHO') ORDER BY data_criacao ASC";
        List<Pedido> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pedido p = mapRow(rs);
                p.setItens(itemRepo.findByPedido(p));
                lista.add(p);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedidos pendentes para garçom", e);
        }
        return lista;
    }

    private Pedido mapRow(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getLong("id"));

        Cliente c = new Cliente();
        c.setId(rs.getLong("cliente_id"));
        p.setCliente(c);

        Timestamp ts = rs.getTimestamp("data_criacao");
        p.setDataCriacao(ts != null ? ts.toLocalDateTime() : LocalDateTime.now());

        p.setStatus(StatusPedido.valueOf(rs.getString("status")));
        p.setValorTotal(rs.getBigDecimal("valor_total"));
        // NOVO: numero da mesa
        try {
            p.setNumeroMesa(rs.getString("numero_mesa"));
        } catch (SQLException ex) {
            // se a coluna ainda não existir no banco, evita quebrar
            // mas ideal é garantir que o ALTER TABLE foi executado
        }
        return p;
    }

    @Override
    public List<ItemPedido> listarItensDoPedido(Long pedidoId) {
        String sql = "SELECT ip.id, ip.pedido_id, ip.produto_id, ip.quantidade, " +
                     "       ip.preco_unitario, ip.subtotal, " +
                     "       p.nome, p.descricao, p.categoria, p.preco, p.estoque, p.ativo " +
                     "  FROM item_pedido ip " +
                     "  JOIN produto p ON p.id = ip.produto_id " +
                     " WHERE ip.pedido_id = ?";

        List<ItemPedido> itens = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, pedidoId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Monta o Produto completo
                br.com.lanchonete.model.entity.Produto prod =
                        new br.com.lanchonete.model.entity.Produto();
                prod.setId(rs.getLong("produto_id"));
                prod.setNome(rs.getString("nome"));
                prod.setDescricao(rs.getString("descricao"));
                prod.setCategoria(
                        br.com.lanchonete.model.enums.CategoriaProduto.valueOf(
                                rs.getString("categoria")
                        )
                );
                prod.setPreco(rs.getBigDecimal("preco"));
                prod.setEstoque(rs.getInt("estoque"));
                prod.setAtivo(rs.getBoolean("ativo"));

                // Monta o ItemPedido
                ItemPedido item = new ItemPedido();
                item.setId(rs.getLong("id"));

                br.com.lanchonete.model.entity.Pedido ped = new br.com.lanchonete.model.entity.Pedido();
                ped.setId(pedidoId);
                item.setPedido(ped);

                item.setProduto(prod);
                item.setQuantidade(rs.getInt("quantidade"));
                item.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
                item.setSubtotal(rs.getBigDecimal("subtotal"));

                itens.add(item);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens do pedido " + pedidoId, e);
        }

        return itens;
    }
}
