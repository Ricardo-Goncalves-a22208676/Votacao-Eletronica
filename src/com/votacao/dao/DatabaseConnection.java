package com.votacao.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import com.votacao.utils.PasswordUtils;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:votacao.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // Tabela Administrador
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Administrador (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "nome TEXT NOT NULL," +
                            "username TEXT NOT NULL UNIQUE," +
                            "password_hash TEXT NOT NULL" +
                            ")");

            // Tabela Eleitor
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Eleitor (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "nome TEXT NOT NULL," +
                            "numero_eleitor TEXT NOT NULL," +
                            "username TEXT NOT NULL UNIQUE," +
                            "password_hash TEXT NOT NULL," +
                            "votou BOOLEAN NOT NULL DEFAULT 0" +
                            ")");

            // Tabela Candidato
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Candidato (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "nome TEXT NOT NULL," +
                            "partido TEXT NOT NULL" +
                            ")");

            // Tabela Voto
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Voto (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "eleitor_id INTEGER NOT NULL," +
                            "candidato_id INTEGER NOT NULL," +
                            "data_voto TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (eleitor_id) REFERENCES Eleitor(id)," +
                            "FOREIGN KEY (candidato_id) REFERENCES Candidato(id)" +
                            ")");

            // Tabela EstadoVotacao
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS EstadoVotacao (" +
                            "id INTEGER PRIMARY KEY CHECK (id = 1)," +
                            "aberta BOOLEAN NOT NULL" +
                            ")");

            // Criar administrador padrão se não existir
            String adminPasswordHash = PasswordUtils.hashPassword("admin");
            stmt.executeUpdate(
                    "INSERT OR IGNORE INTO Administrador (nome, username, password_hash) " +
                            "VALUES ('Administrador', 'admin', '" + adminPasswordHash + "')");

            // Inicializar estado da votação como aberta
            stmt.executeUpdate(
                    "INSERT OR IGNORE INTO EstadoVotacao (id, aberta) VALUES (1, 1)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}