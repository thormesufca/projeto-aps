package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.Cliente;
import br.edu.ufca.audiencias.models.Contrato;
import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.models.enums.FaseProcesso;
import br.edu.ufca.audiencias.models.enums.StatusProcesso;
import br.edu.ufca.audiencias.models.enums.TipoPessoa;
import br.edu.ufca.audiencias.models.enums.TipoProcesso;
import br.edu.ufca.audiencias.models.enums.TipoValorContrato;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContratoRepository {

    private final Connection conn;

    public ContratoRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
    }

    public Contrato salvar(Contrato contrato) {
        String sql = """
                INSERT INTO contratos
                    (data_contratacao, data_encerramento, tipo_valor, valor, descricao, observacoes, cliente_id, processo_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, contrato.getDataContratacao() != null
                    ? contrato.getDataContratacao().toString()
                    : null);
            ps.setString(2, contrato.getDataEncerramento() != null
                    ? contrato.getDataEncerramento().toString()
                    : null);
            ps.setString(3, contrato.getTipoValor() != null
                    ? contrato.getTipoValor().name()
                    : TipoValorContrato.FIXO.name());
            ps.setDouble(4, contrato.getValor() != null ? contrato.getValor().doubleValue() : 0);
            ps.setString(5, contrato.getDescricao());
            ps.setString(6, contrato.getObservacoes());
            ps.setLong(7, contrato.getCliente().getId());
            if (contrato.getProcesso() != null)
                ps.setLong(8, contrato.getProcesso().getId());
            else
                ps.setNull(8, Types.INTEGER);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next())
                contrato.setId(keys.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar contrato: " + e.getMessage(), e);
        }
        return contrato;
    }

    public List<Contrato> listarRecebidosPorMes(int mes, int ano) {
        List<Contrato> lista = new ArrayList<>();
        String sql = "SELECT c.*, p.*, cli.* FROM contratos c INNER JOIN processos p ON c.processo_id = p.id INNER JOIN clientes cli on cli.id = c.cliente_id WHERE strftime('%m', p.data_pagamento) = ? AND strftime('%Y', p.data_pagamento) = ? ORDER BY p.data_pagamento DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, String.format("%02d", mes));
            ps.setString(2, String.valueOf(ano));
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar contratos: " + e.getMessage(), e);
        }
        return lista;
    }

    public void atualizar(Contrato contrato) {
        String sql = """
                UPDATE contratos SET
                    data_contratacao=?, data_encerramento=?, tipo_valor=?, valor=?,
                    descricao=?, observacoes=?, cliente_id=?, processo_id=?
                WHERE id=?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contrato.getDataContratacao() != null
                    ? contrato.getDataContratacao().toString()
                    : null);
            ps.setString(2, contrato.getDataEncerramento() != null
                    ? contrato.getDataEncerramento().toString()
                    : null);
            ps.setString(3, contrato.getTipoValor() != null
                    ? contrato.getTipoValor().name()
                    : TipoValorContrato.FIXO.name());
            ps.setDouble(4, contrato.getValor() != null ? contrato.getValor().doubleValue() : 0);
            ps.setString(5, contrato.getDescricao());
            ps.setString(6, contrato.getObservacoes());
            ps.setLong(7, contrato.getCliente().getId());
            if (contrato.getProcesso() != null)
                ps.setLong(8, contrato.getProcesso().getId());
            else
                ps.setNull(8, Types.INTEGER);
            ps.setLong(9, contrato.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar contrato: " + e.getMessage(), e);
        }
    }

    public void deletar(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM contratos WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar contrato: " + e.getMessage(), e);
        }
    }

    public Optional<Contrato> buscarPorId(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM contratos WHERE id=?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar contrato: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Contrato> listarPorCliente(Long clienteId) {
        List<Contrato> lista = new ArrayList<>();
        String sql = "SELECT * FROM contratos WHERE cliente_id=? ORDER BY data_contratacao DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, clienteId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar contratos: " + e.getMessage(), e);
        }
        return lista;
    }

    public int contar() {
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM contratos")) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar contratos: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<Contrato> listarTodos() {
        List<Contrato> lista = new ArrayList<>();
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(
                        "SELECT c.*, p.*, cli.* FROM contratos c INNER JOIN processos p ON c.processo_id = p.id INNER JOIN clientes cli on cli.id = c.cliente_id ORDER BY data_contratacao DESC")) {
            while (rs.next())
                lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar contratos: " + e.getMessage(), e);
        }
        return lista;
    }

    private Contrato mapear(ResultSet rs) throws SQLException {
        Contrato c = new Contrato();
        c.setId(rs.getLong("id"));
        String dc = rs.getString("data_contratacao");
        if (dc != null)
            c.setDataContratacao(LocalDate.parse(dc));
        String de = rs.getString("data_encerramento");
        if (de != null)
            c.setDataEncerramento(LocalDate.parse(de));
        String tv = rs.getString("tipo_valor");
        c.setTipoValor(tv != null ? TipoValorContrato.valueOf(tv) : TipoValorContrato.FIXO);
        c.setValor(BigDecimal.valueOf(rs.getDouble("valor")));
        c.setDescricao(rs.getString("descricao"));
        c.setObservacoes(rs.getString("observacoes"));
        long clienteId = rs.getLong("cliente_id");
        if (!rs.wasNull()) {
            Cliente cli = new Cliente();
            cli.setId(clienteId);
            cli.setNome(rs.getString("nome"));
            cli.setEmail(rs.getString("email"));
            cli.setEndereco(rs.getString("endereco"));
            cli.setIdentificador(rs.getString("identificador"));
            cli.setTelefone(rs.getString("telefone"));
            cli.setTipoPessoa(TipoPessoa.valueOf(rs.getString("tipo_pessoa")));
            c.setCliente(cli);
        }
        long procId = rs.getLong("processo_id");
        if (!rs.wasNull()) {
            Processo p = new Processo();
            p.setId(procId);
            p.setNumero(rs.getString("numero"));
            p.setTipo(TipoProcesso.valueOf(rs.getString("tipo")));
            p.setStatus(StatusProcesso.valueOf(rs.getString("status")));
            p.setFase(FaseProcesso.valueOf(rs.getString("fase")));
            p.setDataAbertura(LocalDate.parse(rs.getString("data_abertura")));
            p.setOrgaoJulgador(rs.getString("orgao_julgador"));
            p.setDescricao(rs.getString("descricao"));
            p.setValorCausa(BigDecimal.valueOf(rs.getDouble("valor_causa")));
            p.setValorCondenacao(BigDecimal.valueOf(rs.getDouble("valor_condenacao")));
            p.setHonorariosSucumbenciais(BigDecimal.valueOf(rs.getDouble("honorarios_sucumbenciais")));
            p.setDataPagamento(rs.getString("data_pagamento") != null
                    ? LocalDate.parse(rs.getString("data_pagamento"))
                    : null);
            c.setProcesso(p);
        }
        return c;
    }
}
