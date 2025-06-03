package com.votacao.dao;

import com.votacao.model.Voto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VotoDAO {

    public void inserir(Voto voto) {
        String sql = "INSERT INTO Voto (eleitor_id, candidato_id, data_voto) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, voto.getEleitorId());
            stmt.setInt(2, voto.getIdCandidato());
            stmt.setString(3, voto.getTimestamp().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Voto> listarTodos() {
        List<Voto> votos = new ArrayList<>();
        String sql = "SELECT * FROM Voto";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Voto voto = new Voto();
                voto.setId(rs.getInt("id"));
                voto.setIdCandidato(rs.getInt("id_candidato"));
                voto.setTimestamp(LocalDateTime.parse(rs.getString("timestamp")));
                votos.add(voto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return votos;
    }

    public List<Voto> buscarPorEleitor(int idEleitor) {
        List<Voto> votos = new ArrayList<>();
        String sql = "SELECT v.* FROM Voto v " +
                "JOIN Eleitor e ON e.id = ? " +
                "WHERE e.votou = 1";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEleitor);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Voto voto = new Voto();
                voto.setId(rs.getInt("id"));
                voto.setIdCandidato(rs.getInt("id_candidato"));
                voto.setTimestamp(LocalDateTime.parse(rs.getString("timestamp")));
                votos.add(voto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return votos;
    }

    public int contarVotosPorCandidato(int idCandidato) {
        String sql = "SELECT COUNT(*) AS total FROM Voto WHERE candidato_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCandidato);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int contarTotalVotos() {
        String sql = "SELECT COUNT(*) AS total FROM Voto";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}