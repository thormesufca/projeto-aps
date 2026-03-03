package br.edu.ufca.audiencias.config;

import br.edu.ufca.audiencias.models.Escritorio;

import java.time.LocalDate;
import java.util.prefs.Preferences;

/**
 * Armazena os dados do único escritório da aplicação no registro do sistema
 * operacional via {@link java.util.prefs.Preferences}, sem gravar no banco de dados.
 */
public final class EscritorioConfig {

    private static final Preferences PREFS =
            Preferences.userRoot().node("br/edu/ufca/audiencias/escritorio");

    private EscritorioConfig() {}

    /** Carrega os dados do escritório a partir das preferências do sistema. */
    public static Escritorio carregar() {
        Escritorio e = new Escritorio();
        e.setNome(PREFS.get("nome", ""));
        String cnpj = PREFS.get("cnpj", "");
        if (!cnpj.isBlank()) e.setCnpj(cnpj);
        e.setTelefone(nullIfBlank(PREFS.get("telefone", "")));
        e.setEmail(nullIfBlank(PREFS.get("email", "")));
        e.setEndereco(nullIfBlank(PREFS.get("endereco", "")));
        String fundacao = PREFS.get("data_fundacao", "");
        if (!fundacao.isBlank()) e.setDataFundacao(LocalDate.parse(fundacao));
        return e;
    }

    /** Persiste os dados do escritório nas preferências do sistema. */
    public static void salvar(Escritorio e) {
        PREFS.put("nome",          e.getNome()         != null ? e.getNome()                    : "");
        PREFS.put("cnpj",          e.getCnpj()         != null ? e.getCnpj()                    : "");
        PREFS.put("telefone",      e.getTelefone()     != null ? e.getTelefone()                : "");
        PREFS.put("email",         e.getEmail()        != null ? e.getEmail()                   : "");
        PREFS.put("endereco",      e.getEndereco()     != null ? e.getEndereco()                : "");
        PREFS.put("data_fundacao", e.getDataFundacao() != null ? e.getDataFundacao().toString() : "");
    }

    /** Retorna {@code true} se o nome do escritório já foi configurado. */
    public static boolean isConfigurado() {
        return !PREFS.get("nome", "").isBlank();
    }

    private static String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
