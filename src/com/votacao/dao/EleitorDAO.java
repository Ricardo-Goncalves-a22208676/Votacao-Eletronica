package com.votacao.dao;

import com.votacao.model.Eleitor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EleitorDAO {

    public boolean inserir(Eleitor eleitor) {
        String sql = "INSERT INTO Eleitor (nome, numero_eleitor, username, password_hash, votou) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eleitor.getNome());
            stmt.setString(2, eleitor.getNumeroEleitor());
            stmt.setString(3, eleitor.getUsername());
            stmt.setString(4, eleitor.getPasswordHash());
            stmt.setBoolean(5, eleitor.isVotou());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void atualizar(Eleitor eleitor) {
        String sql = "UPDATE Eleitor SET nome = ?, numero_eleitor = ?, username = ?, password_hash = ?, votou = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eleitor.getNome());
            stmt.setString(2, eleitor.getNumeroEleitor());
            stmt.setString(3, eleitor.getUsername());
            stmt.setString(4, eleitor.getPasswordHash());
            stmt.setBoolean(5, eleitor.isVotou());
            stmt.setInt(6, eleitor.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM Eleitor WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Eleitor buscarPorId(int id) {
        String sql = "SELECT * FROM Eleitor WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Eleitor eleitor = new Eleitor();
                eleitor.setId(rs.getInt("id"));
                eleitor.setNome(rs.getString("nome"));
                eleitor.setNumeroEleitor(rs.getString("numero_eleitor"));
                eleitor.setUsername(rs.getString("username"));
                eleitor.setPasswordHash(rs.getString("password_hash"));
                eleitor.setVotou(rs.getBoolean("votou"));
                return eleitor;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Eleitor buscarPorUsername(String username) {
        String sql = "SELECT * FROM Eleitor WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Eleitor eleitor = new Eleitor();
                eleitor.setId(rs.getInt("id"));
                eleitor.setNome(rs.getString("nome"));
                eleitor.setNumeroEleitor(rs.getString("numero_eleitor"));
                eleitor.setUsername(rs.getString("username"));
                eleitor.setPasswordHash(rs.getString("password_hash"));
                eleitor.setVotou(rs.getBoolean("votou"));
                return eleitor;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Eleitor> listarTodos() {
        List<Eleitor> eleitores = new ArrayList<>();
        String sql = "SELECT * FROM Eleitor";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Eleitor eleitor = new Eleitor();
                eleitor.setId(rs.getInt("id"));
                eleitor.setNome(rs.getString("nome"));
                eleitor.setNumeroEleitor(rs.getString("numero_eleitor"));
                eleitor.setUsername(rs.getString("username"));
                eleitor.setPasswordHash(rs.getString("password_hash"));
                eleitor.setVotou(rs.getBoolean("votou"));
                eleitores.add(eleitor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eleitores;
    }
}