package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.Documento;
import br.edu.ufca.audiencias.models.Processo;
import br.edu.ufca.audiencias.models.enums.TipoDocumento;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;
import br.edu.ufca.audiencias.padroes.estruturais.decorator.DocumentoAssinadoDecorator;
import br.edu.ufca.audiencias.padroes.estruturais.decorator.DocumentoProtocoladoDecorator;
import br.edu.ufca.audiencias.padroes.estruturais.decorator.DocumentoUrgenteDecorator;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DocumentoRepository {

    private final Connection conn;

    public DocumentoRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
    }

    public Documento salvar(Documento documento) {
        boolean assinado = documento instanceof DocumentoAssinadoDecorator;
        boolean protocolado = documento instanceof DocumentoProtocoladoDecorator;
        boolean urgente = documento instanceof DocumentoUrgenteDecorator;
        String numProtocolo = null;
        if (protocolado) numProtocolo = ((DocumentoProtocoladoDecorator) documento).getNumeroProtocolo();

        Documento base = desempacotar(documento);

        String sql = """
            INSERT INTO documentos (titulo, descricao,sequencial, caminho_arquivo, tipo, data_upload,
                                    is_assinado, is_protocolado, numero_protocolo, is_urgente, processo_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, base.getTitulo());
            ps.setString(2, base.getDescricao());
            ps.setInt(3, base.getSequencial());
            ps.setString(4, base.getCaminhoArquivo());
            ps.setString(5, base.getTipo() != null ? base.getTipo().name() : "OUTRO");
            ps.setString(6, (base.getDataUpload() != null ? base.getDataUpload() : LocalDate.now()).toString());
            ps.setInt(7, assinado ? 1 : 0);
            ps.setInt(8, protocolado ? 1 : 0);
            ps.setString(9, numProtocolo);
            ps.setInt(10, urgente ? 1 : 0);
            ps.setLong(11, base.getProcesso() != null ? base.getProcesso().getId() : 0);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) documento.setId(keys.getLong(1));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar documento: " + e.getMessage(), e);
        }
        return documento;
    }

    public void deletar(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM documentos WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar documento: " + e.getMessage(), e);
        }
    }

    public List<Documento> listarPorProcesso(Long processoId) {
        List<Documento> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM documentos WHERE processo_id=? ORDER BY data_upload DESC")) {
            ps.setLong(1, processoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar documentos: " + e.getMessage(), e);
        }
        return lista;
    }

    private Documento mapear(ResultSet rs) throws SQLException {
        Documento d = new Documento();
        d.setId(rs.getLong("id"));
        d.setTitulo(rs.getString("titulo"));
        d.setSequencial(rs.getInt("sequencial"));
        d.setDescricao(rs.getString("descricao"));
        d.setCaminhoArquivo(rs.getString("caminho_arquivo"));
        String tipo = rs.getString("tipo");
        if (tipo != null) d.setTipo(TipoDocumento.valueOf(tipo));
        String du = rs.getString("data_upload");
        if (du != null) d.setDataUpload(LocalDate.parse(du));
        Processo p = new Processo();
        p.setId(rs.getLong("processo_id"));
        d.setProcesso(p);

        // Reconstrói os decorators conforme flags salvas
        Documento resultado = d;
        if (rs.getInt("is_assinado") == 1)    resultado = new DocumentoAssinadoDecorator(resultado);
        if (rs.getInt("is_protocolado") == 1) {
            DocumentoProtocoladoDecorator dec = new DocumentoProtocoladoDecorator(resultado);
            dec.setNumeroProtocolo(rs.getString("numero_protocolo"));
            resultado = dec;
        }
        if (rs.getInt("is_urgente") == 1)     resultado = new DocumentoUrgenteDecorator(resultado);
        return resultado;
    }

    /** Desempacota recursivamente todos os decorators para obter o Documento base. */
    private Documento desempacotar(Documento doc) {
        if (doc instanceof br.edu.ufca.audiencias.padroes.estruturais.decorator.DocumentoDecorator dec) {
            return desempacotar(dec.getDocumentoDecorado());
        }
        return doc;
    }
}
