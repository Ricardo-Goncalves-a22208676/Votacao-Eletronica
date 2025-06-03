package com.votacao.dao;

import com.votacao.model.Administrador;
import java.sql.*;

public class AdministradorDAO {
    public Administrador buscarPorUsername(String username) {
        String sql = "SELECT * FROM Administrador WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        return null;
    }

    public void inserir(Administrador admin) {
        String sql = "INSERT INTO Administrador (nome, username, password_hash) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, admin.getNome());
            stmt.setString(2, admin.getUsername());
            stmt.setString(3, admin.getPasswordHash());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}