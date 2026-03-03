package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.Audiencia;
import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.models.enums.StatusAudiencia;
import br.edu.ufca.audiencias.models.enums.TipoAudiencia;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AudienciaRepository {

    private static final String SEP = "|";
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Connection conn;

    public AudienciaRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
    }

    public Audiencia salvar(Audiencia audiencia) {
        String sql = """
            INSERT INTO audiencias (descricao, data_hora, local, tipo, status, resultado, observacoes, processo_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, audiencia.getDescricao());
            ps.setString(2, audiencia.getDataHora() != null ? audiencia.getDataHora().format(DT_FMT) : null);
            ps.setString(3, audiencia.getLocal());
            ps.setString(4, audiencia.getTipo().name());
            ps.setString(5, audiencia.getStatus().name());
            ps.setString(6, audiencia.getResultado());
            ps.setString(7, serializarObservacoes(audiencia.getObservacoes()));
            ps.setLong(8, audiencia.getProcesso().getId());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) audiencia.setId(keys.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar audiência: " + e.getMessage(), e);
        }
        return audiencia;
    }

    public void atualizar(Audiencia audiencia) {
        String sql = """
            UPDATE audiencias SET descricao=?, data_hora=?, local=?, tipo=?, status=?,
            resultado=?, observacoes=?, processo_id=? WHERE id=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, audiencia.getDescricao());
            ps.setString(2, audiencia.getDataHora() != null ? audiencia.getDataHora().format(DT_FMT) : null);
            ps.setString(3, audiencia.getLocal());
            ps.setString(4, audiencia.getTipo().name());
            ps.setString(5, audiencia.getStatus().name());
            ps.setString(6, audiencia.getResultado());
            ps.setString(7, serializarObservacoes(audiencia.getObservacoes()));
            ps.setLong(8, audiencia.getProcesso().getId());
            ps.setLong(9, audiencia.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar audiência: " + e.getMessage(), e);
        }
    }

    public void deletar(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM audiencias WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar audiência: " + e.getMessage(), e);
        }
    }

    public Optional<Audiencia> buscarPorId(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM audiencias WHERE id=?")) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar audiência: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Audiencia> listarPorProcesso(Long processoId) {
        List<Audiencia> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM audiencias WHERE processo_id=? ORDER BY data_hora")) {
            ps.setLong(1, processoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar audiências: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Audiencia> listarPorMes(int mes, int ano) {
        return listarPorMes(mes, ano, false);
    }

    public List<Audiencia> listarPorMes(int mes, int ano, boolean comProcesso) {
        List<Audiencia> lista = new ArrayList<>();
        String prefixo = String.format("%04d-%02d", ano, mes);
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM audiencias WHERE data_hora LIKE ? ORDER BY data_hora")) {
            ps.setString(1, prefixo + "%");
            ResultSet rs = ps.executeQuery();
            ProcessoRepository processoRepo = comProcesso ? new ProcessoRepository() : null;
            while (rs.next()) {
                Audiencia a = mapear(rs);
                if (comProcesso) {
                    processoRepo.buscarPorId(a.getProcesso().getId()).ifPresent(a::setProcesso);
                }
                lista.add(a);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar audiências por mês: " + e.getMessage(), e);
        }
        return lista;
    }

    public int contar() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM audiencias")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar audiências: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<Audiencia> listarTodas() {
        List<Audiencia> lista = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM audiencias ORDER BY data_hora")) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar audiências: " + e.getMessage(), e);
        }
        return lista;
    }

    private Audiencia mapear(ResultSet rs) throws SQLException {
        Audiencia a = new Audiencia();
        a.setId(rs.getLong("id"));
        a.setDescricao(rs.getString("descricao"));
        String dh = rs.getString("data_hora");
        if (dh != null) a.setDataHora(LocalDateTime.parse(dh, DT_FMT));
        a.setLocal(rs.getString("local"));
        a.setTipo(TipoAudiencia.valueOf(rs.getString("tipo")));
        a.setStatus(StatusAudiencia.valueOf(rs.getString("status")));
        a.setResultado(rs.getString("resultado"));
        a.setObservacoes(desserializarObservacoes(rs.getString("observacoes")));
        Processo p = new Processo();
        p.setId(rs.getLong("processo_id"));
        a.setProcesso(p);
        return a;
    }

    private String serializarObservacoes(List<String> obs) {
        if (obs == null || obs.isEmpty()) return null;
        return String.join(SEP, obs);
    }

    private List<String> desserializarObservacoes(String texto) {
        if (texto == null || texto.isBlank()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(texto.split("\\|")));
    }
}
