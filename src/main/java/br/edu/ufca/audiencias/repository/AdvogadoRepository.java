package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.Advogado;
import br.edu.ufca.audiencias.models.InscricaoOab;
import br.edu.ufca.audiencias.models.enums.TipoPessoa;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AdvogadoRepository {

    private static final String SEP = "|";

    private static final String SQL_SELECT = """
            SELECT a.id, a.nome, a.identificador, a.telefone, a.tipo_pessoa, a.especialidade, a.email,
                   GROUP_CONCAT(ao.estado || '#' || ao.numero, '|') AS inscricoes_oab
            FROM advogados a
            LEFT JOIN advogado_oabs ao ON ao.advogado_id = a.id
            """;

    private final Connection conn;

    public AdvogadoRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
    }

    public Advogado salvar(Advogado advogado) {
        String sql = """
            INSERT INTO advogados (nome, identificador, telefone, tipo_pessoa, especialidade, email)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, advogado.getNome());
            ps.setString(2, advogado.getIdentificador());
            ps.setString(3, advogado.getTelefone());
            ps.setString(4, advogado.getTipoPessoa() != null ? advogado.getTipoPessoa().name() : "FISICA");
            ps.setString(5, serializarEspecialidades(advogado.getEspecialidades()));
            ps.setString(6, advogado.getEmail());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) advogado.setId(keys.getLong(1));
            inserirOabs(advogado.getId(), advogado.getInscricoesOab());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar advogado: " + e.getMessage(), e);
        }
        return advogado;
    }

    public void atualizar(Advogado advogado) {
        String sql = """
            UPDATE advogados SET nome=?, identificador=?, telefone=?, tipo_pessoa=?,
            especialidade=?, email=? WHERE id=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, advogado.getNome());
            ps.setString(2, advogado.getIdentificador());
            ps.setString(3, advogado.getTelefone());
            ps.setString(4, advogado.getTipoPessoa() != null ? advogado.getTipoPessoa().name() : "FISICA");
            ps.setString(5, serializarEspecialidades(advogado.getEspecialidades()));
            ps.setString(6, advogado.getEmail());
            ps.setLong(7, advogado.getId());
            ps.executeUpdate();
            try (PreparedStatement del = conn.prepareStatement(
                    "DELETE FROM advogado_oabs WHERE advogado_id=?")) {
                del.setLong(1, advogado.getId());
                del.executeUpdate();
            }
            inserirOabs(advogado.getId(), advogado.getInscricoesOab());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar advogado: " + e.getMessage(), e);
        }
    }

    public void deletar(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM advogados WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar advogado: " + e.getMessage(), e);
        }
    }

    public Optional<Advogado> buscarPorId(Long id) {
        String sql = SQL_SELECT + " WHERE a.id=? GROUP BY a.id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar advogado: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public int contar() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM advogados")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar advogados: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<Advogado> listarTodos() {
        List<Advogado> lista = new ArrayList<>();
        String sql = SQL_SELECT + " GROUP BY a.id ORDER BY a.nome";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar advogados: " + e.getMessage(), e);
        }
        return lista;
    }

    private Advogado mapear(ResultSet rs) throws SQLException {
        Advogado a = new Advogado();
        a.setId(rs.getLong("id"));
        a.setNome(rs.getString("nome"));
        a.setIdentificador(rs.getString("identificador"));
        a.setTelefone(rs.getString("telefone"));
        a.setTipoPessoa(TipoPessoa.valueOf(rs.getString("tipo_pessoa")));
        List<String> especialidades = desserializarEspecialidades(rs.getString("especialidade"));
        for (String esp : especialidades) a.addEspecialidade(esp);
        a.setEmail(rs.getString("email"));
        String rawOabs = rs.getString("inscricoes_oab");
        if (rawOabs != null) {
            for (String parte : rawOabs.split("\\|")) {
                String[] campos = parte.split("#", 2);
                if (campos.length == 2) a.addInscricaoOab(new InscricaoOab(campos[0], campos[1]));
            }
        }
        return a;
    }

    private void inserirOabs(Long advId, List<InscricaoOab> oabs) throws SQLException {
        if (oabs == null || oabs.isEmpty()) return;
        String sql = "INSERT INTO advogado_oabs (advogado_id, estado, numero) VALUES (?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (InscricaoOab o : oabs) {
                ps.setLong(1, advId);
                ps.setString(2, o.getEstado());
                ps.setString(3, o.getNumero());
                ps.executeUpdate();
            }
        }
    }

    private String serializarEspecialidades(List<String> esp) {
        if (esp == null || esp.isEmpty()) return null;
        return String.join(SEP, esp);
    }

    private List<String> desserializarEspecialidades(String texto) {
        if (texto == null || texto.isBlank()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(texto.split("\\|")));
    }
}
