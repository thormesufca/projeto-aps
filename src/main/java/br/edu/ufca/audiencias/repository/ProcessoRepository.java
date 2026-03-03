package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.Advogado;
import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.models.enums.FaseProcesso;
import br.edu.ufca.audiencias.models.enums.StatusProcesso;
import br.edu.ufca.audiencias.models.enums.TipoProcesso;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProcessoRepository {

    private final Connection conn;

    public ProcessoRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
    }

    public Processo salvar(Processo processo) {
        String sql = """
                INSERT INTO processos
                    (numero, tipo, status, fase, data_abertura,
                     orgao_julgador, descricao,
                     valor_causa, valor_condenacao, favoravel, honorarios_sucumbenciais,data_pagamento,
                     advogado_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, processo.getNumero());
            ps.setString(2, processo.getTipo().name());
            ps.setString(3, processo.getStatus().name());
            ps.setString(4, processo.getFase().name());
            ps.setString(5, processo.getDataAbertura().toString());
            ps.setString(6, processo.getOrgaoJulgador());
            ps.setString(7, processo.getDescricao());
            setNullableReal(ps, 8, processo.getValorCausa());
            setNullableReal(ps, 9, processo.getValorCondenacao());
            setNullableBoolean(ps, 10, processo.getFavoravel());
            setNullableReal(ps, 11, processo.getHonorariosSucumbenciais());
            ps.setString(12, processo.getDataPagamento() != null ? processo.getDataPagamento().toString() : null);
            setNullableFK(ps, 13, processo.getAdvogadoResponsavel());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next())
                processo.setId(keys.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar processo: " + e.getMessage(), e);
        }
        return processo;
    }

    public void atualizar(Processo processo) {
        String sql = """
                UPDATE processos SET
                    numero=?, tipo=?, status=?, fase=?,
                    data_abertura=?, orgao_julgador=?, descricao=?,
                    valor_causa=?, valor_condenacao=?, favoravel=?, honorarios_sucumbenciais=?,
                    data_pagamento=?, advogado_id=?
                WHERE id=?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, processo.getNumero());
            ps.setString(2, processo.getTipo().name());
            ps.setString(3, processo.getStatus().name());
            ps.setString(4, processo.getFase().name());
            ps.setString(5, processo.getDataAbertura().toString());
            ps.setString(6, processo.getOrgaoJulgador());
            ps.setString(7, processo.getDescricao());
            setNullableReal(ps, 8, processo.getValorCausa());
            setNullableReal(ps, 9, processo.getValorCondenacao());
            setNullableBoolean(ps, 10, processo.getFavoravel());
            setNullableReal(ps, 11, processo.getHonorariosSucumbenciais());
            ps.setString(12, processo.getDataPagamento() != null ? processo.getDataPagamento().toString() : null);
            setNullableFK(ps, 13, processo.getAdvogadoResponsavel());
            ps.setLong(14, processo.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar processo: " + e.getMessage(), e);
        }
    }

    public void deletar(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM processos WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar processo: " + e.getMessage(), e);
        }
    }

    public Optional<Processo> buscarPorId(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM processos WHERE id=?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar processo: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public int contar() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM processos")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar processos: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<Processo> listarTodos() {
        List<Processo> lista = new ArrayList<>();
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT * FROM processos ORDER BY data_abertura DESC")) {
            while (rs.next())
                lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar processos: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Processo> buscarPorNumero(String numero) {
        List<Processo> lista = new ArrayList<>();
        String termoNormalizado = numero != null ? numero.replaceAll("\\D", "") : "";
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM processos WHERE numero LIKE ? ORDER BY numero")) {
            ps.setString(1, "%" + termoNormalizado + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar processos: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Processo> buscarPorNomeCliente(String nomeCliente) {
        List<Processo> lista = new ArrayList<>();
        String sql = """
                SELECT DISTINCT p.* FROM processos p
                INNER JOIN contratos ct ON ct.processo_id = p.id
                INNER JOIN clientes c ON ct.cliente_id = c.id
                WHERE c.nome LIKE ?
                ORDER BY p.numero
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + nomeCliente + "%";
            ps.setString(1, like);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar processos: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Processo> buscarPorStatus(StatusProcesso status) {
        List<Processo> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM processos WHERE status=? ORDER BY numero")) {
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar processos por status: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Processo> buscarPorClienteId(Long clienteId) {
        List<Processo> lista = new ArrayList<>();
        String sql = """
                SELECT DISTINCT p.* FROM processos p
                INNER JOIN contratos ct ON ct.processo_id = p.id
                WHERE ct.cliente_id = ?
                ORDER BY p.data_abertura DESC
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, clienteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar processos por cliente: " + e.getMessage(), e);
        }
        return lista;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setNullableFK(PreparedStatement ps, int idx, Object entity)
            throws SQLException {
        if (entity == null) {
            ps.setNull(idx, Types.INTEGER);
            return;
        }
        try {
            Long id = (Long) entity.getClass().getMethod("getId").invoke(entity);
            if (id != null)
                ps.setLong(idx, id);
            else
                ps.setNull(idx, Types.INTEGER);
        } catch (Exception e) {
            ps.setNull(idx, Types.INTEGER);
        }
    }

    private void setNullableReal(PreparedStatement ps, int idx, BigDecimal value)
            throws SQLException {
        if (value == null)
            ps.setNull(idx, Types.REAL);
        else
            ps.setDouble(idx, value.doubleValue());
    }

    private void setNullableBoolean(PreparedStatement ps, int idx, Boolean value)
            throws SQLException {
        if (value == null)
            ps.setNull(idx, Types.INTEGER);
        else
            ps.setInt(idx, value ? 1 : 0);
    }

    private Processo mapear(ResultSet rs) throws SQLException {
        Processo p = new Processo();
        p.setId(rs.getLong("id"));
        String numero = rs.getString("numero");
        p.setNumero(numero != null ? numero.replaceAll("\\D", "") : null);
        p.setTipo(TipoProcesso.valueOf(rs.getString("tipo")));
        p.setStatus(StatusProcesso.valueOf(rs.getString("status")));
        p.setFase(FaseProcesso.valueOf(rs.getString("fase")));
        String da = rs.getString("data_abertura");
        if (da != null)
            p.setDataAbertura(LocalDate.parse(da));
        p.setOrgaoJulgador(rs.getString("orgao_julgador"));
        p.setDescricao(rs.getString("descricao"));

        double vc = rs.getDouble("valor_causa");
        if (!rs.wasNull())
            p.setValorCausa(BigDecimal.valueOf(vc));
        double vcd = rs.getDouble("valor_condenacao");
        if (!rs.wasNull())
            p.setValorCondenacao(BigDecimal.valueOf(vcd));
        Object fav = rs.getObject("favoravel");
        if (fav != null)
            p.setFavoravel(((Number) fav).intValue() == 1);
        double hs = rs.getDouble("honorarios_sucumbenciais");
        if (!rs.wasNull())
            p.setHonorariosSucumbenciais(BigDecimal.valueOf(hs));
        String dp = rs.getString("data_pagamento");
        if (dp != null)
            p.setDataPagamento(LocalDate.parse(dp));

        long advId = rs.getLong("advogado_id");
        if (!rs.wasNull()) {
            Advogado a = new Advogado();
            a.setId(advId);
            p.setAdvogadoResponsavel(a);
        }
        return p;
    }
}
