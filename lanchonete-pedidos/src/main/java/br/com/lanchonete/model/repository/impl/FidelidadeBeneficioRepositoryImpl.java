package br.com.lanchonete.model.repository.impl;

import br.com.lanchonete.config.ConnectionFactory;
import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.entity.FidelidadeBeneficio;
import br.com.lanchonete.model.enums.TipoBeneficio;
import br.com.lanchonete.model.repository.FidelidadeBeneficioRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FidelidadeBeneficioRepositoryImpl implements FidelidadeBeneficioRepository {

    @Override
    public FidelidadeBeneficio save(FidelidadeBeneficio b) {
        String sql = "INSERT INTO fidelidade_beneficio (cliente_id, tipo, validade, utilizado) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, b.getCliente().getId());
            ps.setString(2, b.getTipo().name());
            ps.setDate(3, Date.valueOf(b.getValidade()));
            ps.setBoolean(4, b.isUtilizado());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                b.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar benefício de fidelidade", e);
        }
        return b;
    }

    @Override
    public FidelidadeBeneficio update(FidelidadeBeneficio b) {
        String sql = "UPDATE fidelidade_beneficio SET cliente_id = ?, tipo = ?, validade = ?, utilizado = ? " +
                     "WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, b.getCliente().getId());
            ps.setString(2, b.getTipo().name());
            ps.setDate(3, Date.valueOf(b.getValidade()));
            ps.setBoolean(4, b.isUtilizado());
            ps.setLong(5, b.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar benefício de fidelidade", e);
        }
        return b;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM fidelidade_beneficio WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar benefício de fidelidade", e);
        }
    }

    @Override
    public Optional<FidelidadeBeneficio> findById(Long id) {
        String sql = "SELECT * FROM fidelidade_beneficio WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar benefício por id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<FidelidadeBeneficio> findAll() {
        String sql = "SELECT * FROM fidelidade_beneficio";
        List<FidelidadeBeneficio> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar benefícios", e);
        }
        return lista;
    }

    @Override
    public List<FidelidadeBeneficio> findAtivosByCliente(Cliente cliente) {
        String sql = "SELECT * FROM fidelidade_beneficio " +
                     "WHERE cliente_id = ? AND utilizado = 0 AND validade >= CURDATE()";
        List<FidelidadeBeneficio> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cliente.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar benefícios ativos por cliente", e);
        }
        return lista;
    }

    @Override
    public FidelidadeBeneficio findPrimeiroAtivoByClienteAndTipo(Cliente cliente, TipoBeneficio tipo) {
        String sql = "SELECT * FROM fidelidade_beneficio " +
                     "WHERE cliente_id = ? AND tipo = ? AND utilizado = 0 AND validade >= CURDATE() " +
                     "ORDER BY validade ASC LIMIT 1";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, cliente.getId());
            ps.setString(2, tipo.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar benefício ativo por tipo", e);
        }
        return null;
    }

    private FidelidadeBeneficio mapRow(ResultSet rs) throws SQLException {
        FidelidadeBeneficio b = new FidelidadeBeneficio();
        b.setId(rs.getLong("id"));

        Cliente c = new Cliente();
        c.setId(rs.getLong("cliente_id"));
        b.setCliente(c);

        b.setTipo(TipoBeneficio.valueOf(rs.getString("tipo")));
        b.setValidade(rs.getDate("validade").toLocalDate());
        b.setUtilizado(rs.getBoolean("utilizado"));
        return b;
    }
}
