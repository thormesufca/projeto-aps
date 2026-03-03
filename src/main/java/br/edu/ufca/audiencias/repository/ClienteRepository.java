package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.Cliente;
import br.edu.ufca.audiencias.models.enums.TipoPessoa;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository {

    private final Connection conn;

    public ClienteRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
    }

    public Cliente salvar(Cliente cliente) {
        String sql = """
            INSERT INTO clientes (nome, identificador, telefone, tipo_pessoa, email, endereco)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getIdentificador());
            ps.setString(3, cliente.getTelefone());
            ps.setString(4, cliente.getTipoPessoa().name());
            ps.setString(5, cliente.getEmail());
            ps.setString(6, cliente.getEndereco());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) cliente.setId(keys.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar cliente: " + e.getMessage(), e);
        }
        return cliente;
    }

    public void atualizar(Cliente cliente) {
        String sql = """
            UPDATE clientes SET nome=?, identificador=?, telefone=?, tipo_pessoa=?, email=?, endereco=?
            WHERE id=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getIdentificador());
            ps.setString(3, cliente.getTelefone());
            ps.setString(4, cliente.getTipoPessoa().name());
            ps.setString(5, cliente.getEmail());
            ps.setString(6, cliente.getEndereco());
            ps.setLong(7, cliente.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    public void deletar(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM clientes WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar cliente: " + e.getMessage(), e);
        }
    }

    public Optional<Cliente> buscarPorId(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM clientes WHERE id=?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar cliente: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM clientes ORDER BY nome")) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar clientes: " + e.getMessage(), e);
        }
        return lista;
    }

    public int contar() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM clientes")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar clientes: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<Cliente> buscarPorNome(String nome) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM clientes WHERE nome LIKE ? ORDER BY nome";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar clientes por nome: " + e.getMessage(), e);
        }
        return lista;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getLong("id"));
        c.setNome(rs.getString("nome"));
        c.setIdentificador(rs.getString("identificador"));
        c.setTelefone(rs.getString("telefone"));
        c.setTipoPessoa(TipoPessoa.valueOf(rs.getString("tipo_pessoa")));
        c.setEmail(rs.getString("email"));
        c.setEndereco(rs.getString("endereco"));
        return c;
    }
}
