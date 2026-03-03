package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.Movimentacao;
import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.models.enums.TipoMovimentacao;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoRepository {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Connection conn;

    public MovimentacaoRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
    }

    public Movimentacao salvar(Movimentacao mov) {
        String sql = """
            INSERT INTO movimentacoes (descricao, data_hora, responsavel, tipo, processo_id)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, mov.getDescricao());
            ps.setString(2, (mov.getDataHora() != null ? mov.getDataHora() : LocalDateTime.now()).format(DT_FMT));
            ps.setString(3, mov.getResponsavel());
            ps.setString(4, mov.getTipo().name());
            ps.setLong(5, mov.getProcesso().getId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) mov.setId(keys.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar movimentação: " + e.getMessage(), e);
        }
        return mov;
    }

    public List<Movimentacao> listarPorProcesso(Long processoId) {
        List<Movimentacao> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM movimentacoes WHERE processo_id=? ORDER BY data_hora DESC")) {
            ps.setLong(1, processoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar movimentações: " + e.getMessage(), e);
        }
        return lista;
    }

    private Movimentacao mapear(ResultSet rs) throws SQLException {
        Movimentacao m = new Movimentacao();
        m.setId(rs.getLong("id"));
        m.setDescricao(rs.getString("descricao"));
        String dh = rs.getString("data_hora");
        if (dh != null) m.setDataHora(LocalDateTime.parse(dh, DT_FMT));
        m.setResponsavel(rs.getString("responsavel"));
        m.setTipo(TipoMovimentacao.valueOf(rs.getString("tipo")));
        Processo p = new Processo();
        p.setId(rs.getLong("processo_id"));
        m.setProcesso(p);
        return m;
    }
}
