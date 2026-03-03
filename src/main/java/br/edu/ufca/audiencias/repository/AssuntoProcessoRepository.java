package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.AssuntoCNJ;
import br.edu.ufca.audiencias.models.AssuntoProcesso;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssuntoProcessoRepository {

    private final Connection conn;

    public AssuntoProcessoRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
    }

    public AssuntoProcesso salvar(Long processoId, Long codItem, boolean principal) {
        String sql = "INSERT INTO processo_assuntos (processo_id, cod_item, principal) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, processoId);
            ps.setLong(2, codItem);
            ps.setInt(3, principal ? 1 : 0);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                AssuntoProcesso ap = new AssuntoProcesso();
                ap.setId(keys.getLong(1));
                AssuntoCNJ a = new AssuntoCNJ();
                a.setCodItem(codItem);
                ap.setAssunto(a);
                ap.setPrincipal(principal);
                return ap;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao vincular assunto ao processo: " + e.getMessage(), e);
        }
        return null;
    }

    public void deletar(Long id) {
        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM processo_assuntos WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover assunto do processo: " + e.getMessage(), e);
        }
    }

    public List<AssuntoProcesso> listarPorProcesso(Long processoId) {
        List<AssuntoProcesso> lista = new ArrayList<>();
        String sql = """
                SELECT pa.id, pa.principal, a.cod_item, a.cod_item_pai, a.nome
                FROM processo_assuntos pa
                INNER JOIN assuntos_cnj a ON a.cod_item = pa.cod_item
                WHERE pa.processo_id = ?
                ORDER BY pa.principal DESC, a.nome
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, processoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AssuntoCNJ cnj = new AssuntoCNJ();
                cnj.setCodItem(rs.getLong("cod_item"));
                long pai = rs.getLong("cod_item_pai");
                if (!rs.wasNull()) cnj.setCodItemPai(pai);
                cnj.setNome(rs.getString("nome"));

                AssuntoProcesso ap = new AssuntoProcesso();
                ap.setId(rs.getLong("id"));
                ap.setAssunto(cnj);
                ap.setPrincipal(rs.getInt("principal") == 1);
                lista.add(ap);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar assuntos do processo: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Define o assunto identificado por {@code assuntoProcessoId} como principal,
     * removendo a flag de todos os outros assuntos do mesmo processo.
     */
    public void definirPrincipal(Long assuntoProcessoId, Long processoId) {
        try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE processo_assuntos SET principal=0 WHERE processo_id=?")) {
                ps.setLong(1, processoId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE processo_assuntos SET principal=1 WHERE id=?")) {
                ps.setLong(1, assuntoProcessoId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao definir assunto principal: " + e.getMessage(), e);
        }
    }
}
