package br.edu.ufca.audiencias.padroes.criacionais.singleton;

import java.sql.*;


public class ConexaoBancoDadosSingleton {

    private static final String URL = "jdbc:sqlite:audiencias.db";

    private static ConexaoBancoDadosSingleton instancia;
    private Connection conexao;

    private ConexaoBancoDadosSingleton() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.conexao = DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver SQLite não encontrado: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    public static ConexaoBancoDadosSingleton getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoBancoDadosSingleton();
        }
        return instancia;
    }

    public Connection getConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                conexao = DriverManager.getConnection(URL);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Conexão com banco perdida: " + e.getMessage(), e);
        }
        return conexao;
    }

    public void fecharConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) conexao.close();
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
