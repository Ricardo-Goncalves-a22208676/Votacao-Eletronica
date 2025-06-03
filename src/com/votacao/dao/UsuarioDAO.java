package com.votacao.dao;

import com.votacao.model.Usuario;
import com.votacao.model.Eleitor;
import com.votacao.model.Administrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public Usuario buscarPorUsername(String username) {
        // Primeiro tenta encontrar um Administrador
        String sqlAdmin = "SELECT * FROM Administrador WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlAdmin)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Administrador admin = new Administrador();
                admin.setId(rs.getInt("id"));
                admin.setNome(rs.getString("nome"));
                admin.setUsername(rs.getString("username"));
                admin.setPasswordHash(rs.getString("password_hash"));
                return admin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Se não encontrou, tenta encontrar um Eleitor
        String sqlEleitor = "SELECT * FROM Eleitor WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlEleitor)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Eleitor eleitor = new Eleitor();
                eleitor.setId(rs.getInt("id"));
                eleitor.setNome(rs.getString("nome"));
                eleitor.setUsername(rs.getString("username"));
                eleitor.setPasswordHash(rs.getString("password_hash"));
                eleitor.setNumeroEleitor(rs.getString("numero_eleitor"));
                eleitor.setVotou(rs.getBoolean("votou"));
                return eleitor;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Se não encontrou em lado nenhum
        return null;
    }
}