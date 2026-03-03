package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.Interessado;
import br.edu.ufca.audiencias.models.Testemunha;
import br.edu.ufca.audiencias.models.enums.TipoParte;
import br.edu.ufca.audiencias.models.enums.TipoPessoa;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestemunhaRepository {

    private final Connection conn;

    public TestemunhaRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
    }

    /** Persiste uma nova testemunha (sem FK de processo). */
    public Testemunha salvar(Testemunha testemunha) {
        String sql = """
                INSERT INTO testemunhas (nome, identificador, telefone, tipo_pessoa, depoimento)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, testemunha.getNome());
            ps.setString(2, testemunha.getIdentificador());
            ps.setString(3, testemunha.getTelefone());
            ps.setString(4, testemunha.getTipoPessoa() != null
                    ? testemunha.getTipoPessoa().name() : "FISICA");
            ps.setString(5, testemunha.getDepoimento());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) testemunha.setId(keys.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar testemunha: " + e.getMessage(), e);
        }
        return testemunha;
    }

    /** Cria o vínculo entre uma testemunha e um interessado. */
    public void vincularTestemunha(Long testemunhaId, Long interessadoId, Long processoId) {
        String sql = "INSERT INTO testemunha_interessado (testemunha_id, interessado_id, processo_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, testemunhaId);
            ps.setLong(2, interessadoId);
            ps.setLong(3, processoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao vincular testemunha: " + e.getMessage(), e);
        }
    }

    /** Remove apenas o vínculo (linha em testemunha_interessado); preserva o registro da testemunha. */
    public void deletarVinculo(Long linkId) {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM testemunha_interessado WHERE id=?")) {
            ps.setLong(1, linkId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover vínculo de testemunha: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todas as combinações (testemunha, interessado) de um processo.
     * Cada linha do resultado representa um vínculo distinto.
     */
    public List<Testemunha> listarPorProcesso(Long processoId) {
        String sql = """
                SELECT ti.id      AS link_id,
                       t.id       AS t_id,
                       t.nome     AS t_nome,
                       t.identificador,
                       t.telefone,
                       t.tipo_pessoa,
                       t.depoimento,
                       i.id       AS int_id,
                       i.nome     AS int_nome,
                       i.tipo_parte
                FROM testemunha_interessado ti
                JOIN testemunhas  t ON t.id = ti.testemunha_id
                JOIN interessados i ON i.id = ti.interessado_id
                WHERE i.processo_id = ?
                ORDER BY t.nome
                """;
        List<Testemunha> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, processoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapearComVinculo(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar testemunhas: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Busca testemunhas pelo nome (parcial, LIKE). */
    public List<Testemunha> buscarPorNome(String nome) {
        List<Testemunha> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM testemunhas WHERE nome LIKE ? ORDER BY nome")) {
            ps.setString(1, "%" + nome + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar testemunhas por nome: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Busca testemunha pelo identificador exato (CPF/CNPJ). */
    public Optional<Testemunha> buscarPorIdentificador(String identificador) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM testemunhas WHERE identificador = ?")) {
            ps.setString(1, identificador);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar testemunha: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    // ── Mapeamento ────────────────────────────────────────────────────────────

    private Testemunha mapear(ResultSet rs) throws SQLException {
        Testemunha t = new Testemunha();
        t.setId(rs.getLong("id"));
        t.setNome(rs.getString("nome"));
        t.setIdentificador(rs.getString("identificador"));
        t.setTelefone(rs.getString("telefone"));
        t.setTipoPessoa(TipoPessoa.valueOf(rs.getString("tipo_pessoa")));
        t.setDepoimento(rs.getString("depoimento"));
        return t;
    }

    private Testemunha mapearComVinculo(ResultSet rs) throws SQLException {
        Testemunha t = new Testemunha();
        t.setLinkId(rs.getLong("link_id"));
        t.setId(rs.getLong("t_id"));
        t.setNome(rs.getString("t_nome"));
        t.setIdentificador(rs.getString("identificador"));
        t.setTelefone(rs.getString("telefone"));
        t.setTipoPessoa(TipoPessoa.valueOf(rs.getString("tipo_pessoa")));
        t.setDepoimento(rs.getString("depoimento"));

        Interessado i = new Interessado();
        i.setId(rs.getLong("int_id"));
        i.setNome(rs.getString("int_nome"));
        i.setTipoParte(TipoParte.valueOf(rs.getString("tipo_parte")));
        t.setInteressado(i);

        return t;
    }
}
