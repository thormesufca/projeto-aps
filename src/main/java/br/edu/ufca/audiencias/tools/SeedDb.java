package br.edu.ufca.audiencias.tools;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SeedDb {

    private static final String URL = "jdbc:sqlite:audiencias.db";

    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");

        try (Connection conn = DriverManager.getConnection(URL);
             InputStream is = SeedDb.class.getClassLoader()
                     .getResourceAsStream("database/seed.sql")) {

            if (is == null) {
                System.err.println("[SeedDb] ERRO: database/seed.sql nao encontrado no classpath.");
                System.exit(1);
            }

            String sql = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            try (Statement st = conn.createStatement()) {
                for (String stmt : sql.split(";")) {
                    String clean = Arrays.stream(stmt.split("\n"))
                            .filter(l -> !l.trim().startsWith("--") && !l.trim().isEmpty())
                            .collect(Collectors.joining("\n"))
                            .trim();
                    if (!clean.isEmpty()) {
                        st.execute(clean);
                    }
                }
            }

            System.out.println("[SeedDb] Dados de teste inseridos com sucesso.");
        }
    }
}
