package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.MembroEscritorio;
import br.edu.ufca.audiencias.models.enums.TipoVinculoAdvogado;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EscritorioRepository {

    private final Connection conn;
    private final AdvogadoRepository advogadoRepo;

    public EscritorioRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
        this.advogadoRepo = new AdvogadoRepository();
    }

    // -------------------------------------------------------------------------
    // Membros (vínculo advogado–escritório)
    // -------------------------------------------------------------------------

    public MembroEscritorio adicionarMembro(MembroEscritorio membro) {
        String sql = """
            INSERT INTO escritorio_advogados
                (advogado_id, tipo_vinculo, percentual_sociedade, salario_mensal,
                 data_ingresso, data_desligamento)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, membro.getAdvogado().getId());
            ps.setString(2, membro.getTipoVinculo().name());
            setBigDecimal(ps, 3, membro.getPercentualSociedade());
            setBigDecimal(ps, 4, membro.getSalarioMensal());
            ps.setString(5, membro.getDataIngresso() != null ? membro.getDataIngresso().toString() : null);
            ps.setString(6, membro.getDataDesligamento() != null ? membro.getDataDesligamento().toString() : null);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) membro.setId(keys.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar membro ao escritório: " + e.getMessage(), e);
        }
        return membro;
    }

    public void atualizarMembro(MembroEscritorio membro) {
        String sql = """
            UPDATE escritorio_advogados SET tipo_vinculo=?, percentual_sociedade=?,
            salario_mensal=?, data_ingresso=?, data_desligamento=?
            WHERE id=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, membro.getTipoVinculo().name());
            setBigDecimal(ps, 2, membro.getPercentualSociedade());
            setBigDecimal(ps, 3, membro.getSalarioMensal());
            ps.setString(4, membro.getDataIngresso() != null ? membro.getDataIngresso().toString() : null);
            ps.setString(5, membro.getDataDesligamento() != null ? membro.getDataDesligamento().toString() : null);
            ps.setLong(6, membro.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar membro: " + e.getMessage(), e);
        }
    }

    public void removerMembro(Long membroId) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM escritorio_advogados WHERE id=?")) {
            ps.setLong(1, membroId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover membro: " + e.getMessage(), e);
        }
    }

    public List<MembroEscritorio> listarMembros() {
        List<MembroEscritorio> membros = new ArrayList<>();
        String sql = """
            SELECT id, advogado_id, tipo_vinculo, percentual_sociedade,
                   salario_mensal, data_ingresso, data_desligamento
            FROM escritorio_advogados
            ORDER BY data_ingresso
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                MembroEscritorio m = new MembroEscritorio();
                m.setId(rs.getLong("id"));
                m.setTipoVinculo(TipoVinculoAdvogado.valueOf(rs.getString("tipo_vinculo")));
                double perc = rs.getDouble("percentual_sociedade");
                if (!rs.wasNull()) m.setPercentualSociedade(BigDecimal.valueOf(perc));
                double sal = rs.getDouble("salario_mensal");
                if (!rs.wasNull()) m.setSalarioMensal(BigDecimal.valueOf(sal));
                String di = rs.getString("data_ingresso");
                if (di != null) m.setDataIngresso(LocalDate.parse(di));
                String dd = rs.getString("data_desligamento");
                if (dd != null) m.setDataDesligamento(LocalDate.parse(dd));
                advogadoRepo.buscarPorId(rs.getLong("advogado_id"))
                        .ifPresent(m::setAdvogado);
                membros.add(m);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar membros do escritório: " + e.getMessage(), e);
        }
        return membros;
    }

    /**
     * Soma os percentuais dos sócios ativos. Se {@code excluirMembroId} for não-nulo,
     * aquele membro é ignorado (para validação ao atualizar um sócio existente).
     */
    public BigDecimal somarPercentuaisSocios(Long excluirMembroId) {
        String sql = """
            SELECT COALESCE(SUM(percentual_sociedade), 0)
            FROM escritorio_advogados
            WHERE tipo_vinculo = 'SOCIO'
              AND (data_desligamento IS NULL OR data_desligamento > date('now'))
            """ + (excluirMembroId != null ? " AND id <> ?" : "");
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (excluirMembroId != null) ps.setLong(1, excluirMembroId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return BigDecimal.valueOf(rs.getDouble(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao somar percentuais de sócios: " + e.getMessage(), e);
        }
        return BigDecimal.ZERO;
    }

    private void setBigDecimal(PreparedStatement ps, int idx, BigDecimal value) throws SQLException {
        if (value != null) ps.setDouble(idx, value.doubleValue());
        else ps.setNull(idx, Types.REAL);
    }
}
