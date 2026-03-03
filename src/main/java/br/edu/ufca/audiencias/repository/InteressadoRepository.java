package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.Advogado;
import br.edu.ufca.audiencias.models.Interessado;
import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.models.enums.TipoParte;
import br.edu.ufca.audiencias.models.enums.TipoPessoa;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InteressadoRepository {

    private final Connection conn;

    public InteressadoRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
    }

    public Interessado salvar(Interessado interessado) {
        String sql = """
            INSERT INTO interessados
                (nome, identificador, telefone, tipo_pessoa, tipo_parte, processo_id, advogado_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, interessado.getNome());
            ps.setString(2, interessado.getIdentificador());
            ps.setString(3, interessado.getTelefone());
            ps.setString(4, interessado.getTipoPessoa() != null
                    ? interessado.getTipoPessoa().name() : "FISICA");
            ps.setString(5, interessado.getTipoParte().name());
            ps.setLong(6, interessado.getProcesso().getId());
            if (interessado.getAdvogado() != null) ps.setLong(7, interessado.getAdvogado().getId());
            else ps.setNull(7, Types.INTEGER);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) interessado.setId(keys.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar interessado: " + e.getMessage(), e);
        }
        return interessado;
    }

    public void deletar(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM interessados WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar interessado: " + e.getMessage(), e);
        }
    }

    public Interessado buscarPorId(Long id) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM interessados WHERE id=?");
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            return mapear(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar interessado: " + e.getMessage(), e);
        }
    }

    public List<Interessado> listarPorProcesso(Long processoId) {
        List<Interessado> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM interessados WHERE processo_id=? ORDER BY nome")) {
            ps.setLong(1, processoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar interessados: " + e.getMessage(), e);
        }
        return lista;
    }

    private Interessado mapear(ResultSet rs) throws SQLException {
        Interessado i = new Interessado();
        i.setId(rs.getLong("id"));
        i.setNome(rs.getString("nome"));
        i.setIdentificador(rs.getString("identificador"));
        i.setTelefone(rs.getString("telefone"));
        i.setTipoPessoa(TipoPessoa.valueOf(rs.getString("tipo_pessoa")));
        i.setTipoParte(TipoParte.valueOf(rs.getString("tipo_parte")));
        Processo p = new Processo(); p.setId(rs.getLong("processo_id")); i.setProcesso(p);
        long advId = rs.getLong("advogado_id");
        Advogado a = new Advogado();
        a.setId(advId);
        i.setAdvogado(a);
        return i;
    }
}
