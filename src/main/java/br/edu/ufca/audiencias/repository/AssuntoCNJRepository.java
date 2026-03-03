package br.edu.ufca.audiencias.repository;

import br.edu.ufca.audiencias.models.AssuntoCNJ;
import br.edu.ufca.audiencias.padroes.criacionais.singleton.ConexaoBancoDadosSingleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssuntoCNJRepository {

    private final Connection conn;

    public AssuntoCNJRepository() {
        this.conn = ConexaoBancoDadosSingleton.getInstancia().getConexao();
        garantirDados();
    }

    /** Importa lib/assuntos.json para o banco se a tabela estiver vazia. */
    private void garantirDados() {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM assuntos_cnj")) {
            if (rs.next() && rs.getInt(1) > 0) return;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar assuntos_cnj: " + e.getMessage(), e);
        }

        File arquivo = new File("lib/assuntos.json");
        if (!arquivo.exists()) {
            System.err.println("[AssuntoCNJRepository] lib/assuntos.json não encontrado — assuntos CNJ não importados.");
            return;
        }

        List<AssuntoCNJ> assuntos = lerAssuntosDoJson(arquivo);
        importarEmBatch(assuntos);
        System.out.println("[AssuntoCNJRepository] " + assuntos.size() + " assuntos CNJ importados.");
    }

    private List<AssuntoCNJ> lerAssuntosDoJson(File arquivo) {
        List<AssuntoCNJ> lista = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(arquivo), StandardCharsets.UTF_8))) {
            String line;
            Long codItem = null;
            Long codItemPai = null;
            String nome = null;
            boolean inObject = false;

            while ((line = reader.readLine()) != null) {
                String t = line.trim();
                if (t.equals("{")) {
                    inObject = true;
                    codItem = null;
                    codItemPai = null;
                    nome = null;
                } else if (inObject && (t.startsWith("}") )) {
                    if (codItem != null && nome != null) {
                        lista.add(new AssuntoCNJ(codItem, codItemPai, nome));
                    }
                    inObject = false;
                } else if (inObject) {
                    if (t.startsWith("\"cod_item\":") && !t.startsWith("\"cod_item_pai\":")) {
                        String val = afterColon(t);
                        if (!val.equals("null")) codItem = Long.parseLong(val);
                    } else if (t.startsWith("\"cod_item_pai\":")) {
                        String val = afterColon(t);
                        if (!val.equals("null")) codItemPai = Long.parseLong(val);
                    } else if (t.startsWith("\"nome\":")) {
                        nome = extractStringValue(t);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler assuntos.json: " + e.getMessage(), e);
        }
        return lista;
    }

    /** Extrai o valor após ':' de uma linha JSON, removendo vírgula e espaços. */
    private static String afterColon(String line) {
        int idx = line.indexOf(':');
        if (idx < 0) return "null";
        return line.substring(idx + 1).trim().replaceAll("[,\\s]", "");
    }

    /** Extrai o valor entre aspas de uma linha do tipo "chave": "valor". */
    private static String extractStringValue(String line) {
        int colonIdx = line.indexOf(':');
        if (colonIdx < 0) return null;
        String after = line.substring(colonIdx + 1).trim();
        if (after.startsWith("\"")) {
            int start = 1;
            StringBuilder sb = new StringBuilder();
            for (int i = start; i < after.length(); i++) {
                char c = after.charAt(i);
                if (c == '\\' && i + 1 < after.length()) {
                    char next = after.charAt(i + 1);
                    if (next == '"') { sb.append('"'); i++; }
                    else if (next == '\\') { sb.append('\\'); i++; }
                    else { sb.append(c); }
                } else if (c == '"') {
                    break;
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }
        return null;
    }

    private void importarEmBatch(List<AssuntoCNJ> assuntos) {
        String sql = "INSERT OR IGNORE INTO assuntos_cnj (cod_item, cod_item_pai, nome) VALUES (?, ?, ?)";
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (AssuntoCNJ a : assuntos) {
                    ps.setLong(1, a.getCodItem());
                    if (a.getCodItemPai() != null)
                        ps.setLong(2, a.getCodItemPai());
                    else
                        ps.setNull(2, Types.INTEGER);
                    ps.setString(3, a.getNome());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            throw new RuntimeException("Erro ao importar assuntos CNJ: " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { /* ignore */ }
        }
    }

    public List<AssuntoCNJ> buscarPorNome(String termo) {
        List<AssuntoCNJ> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT cod_item, cod_item_pai, nome FROM assuntos_cnj WHERE nome LIKE ? ORDER BY nome LIMIT 100")) {
            ps.setString(1, "%" + termo.toUpperCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar assuntos CNJ: " + e.getMessage(), e);
        }
        return lista;
    }

    private AssuntoCNJ mapear(ResultSet rs) throws SQLException {
        AssuntoCNJ a = new AssuntoCNJ();
        a.setCodItem(rs.getLong("cod_item"));
        long pai = rs.getLong("cod_item_pai");
        if (!rs.wasNull()) a.setCodItemPai(pai);
        a.setNome(rs.getString("nome"));
        return a;
    }
}
