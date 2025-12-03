package br.com.lanchonete.model.repository.impl;

import br.com.lanchonete.config.ConnectionFactory;
import br.com.lanchonete.model.entity.Cliente;
import br.com.lanchonete.model.repository.ClienteRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepositoryImpl implements ClienteRepository {

    @Override
    public Cliente save(Cliente c) {
        String sql = "INSERT INTO cliente (nome, email, data_nascimento, cpf, senha, ativo, pontos_fidelidade) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNome());
            ps.setString(2, c.getEmail());
            ps.setDate(3, c.getDataNascimento() != null ? Date.valueOf(c.getDataNascimento()) : null);
            ps.setString(4, c.getCpf());
            ps.setString(5, c.getSenha());
            ps.setBoolean(6, c.isAtivo());
            ps.setInt(7, c.getPontosFidelidade());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                c.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar cliente", e);
        }
        return c;
    }

    @Override
    public Cliente update(Cliente c) {
        String sql = "UPDATE cliente SET nome = ?, email = ?, data_nascimento = ?, cpf = ?, " +
                     "senha = ?, ativo = ?, pontos_fidelidade = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNome());
            ps.setString(2, c.getEmail());
            ps.setDate(3, c.getDataNascimento() != null ? Date.valueOf(c.getDataNascimento()) : null);
            ps.setString(4, c.getCpf());
            ps.setString(5, c.getSenha());
            ps.setBoolean(6, c.isAtivo());
            ps.setInt(7, c.getPontosFidelidade());
            ps.setLong(8, c.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente", e);
        }
        return c;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar cliente", e);
        }
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        String sql = "SELECT * FROM cliente WHERE id = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Cliente> findAll() {
        String sql = "SELECT * FROM cliente";
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes", e);
        }
        return lista;
    }

    @Override
    public Optional<Cliente> findByEmail(String email) {
        String sql = "SELECT * FROM cliente WHERE email = ?";
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente por email", e);
        }
        return Optional.empty();
    }

    private Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getLong("id"));
        c.setNome(rs.getString("nome"));
        c.setEmail(rs.getString("email"));
        Date d = rs.getDate("data_nascimento");
        c.setDataNascimento(d != null ? d.toLocalDate() : null);
        c.setCpf(rs.getString("cpf"));
        c.setSenha(rs.getString("senha"));
        c.setAtivo(rs.getBoolean("ativo"));
        c.setPontosFidelidade(rs.getInt("pontos_fidelidade"));
        return c;
    }
}
