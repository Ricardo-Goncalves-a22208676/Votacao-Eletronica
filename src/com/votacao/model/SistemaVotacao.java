package com.votacao.model;

import com.votacao.dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SistemaVotacao {

    public static boolean isVotacaoAberta() {
        String sql = "SELECT aberta FROM EstadoVotacao WHERE id = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("aberta");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Por defeito, fechada
    }

    public static void iniciarVotacao() {
        String sqlSelect = "SELECT id FROM EstadoVotacao WHERE id = 1";
        String sqlInsert = "INSERT INTO EstadoVotacao (id, aberta) VALUES (1, 1)";
        String sqlUpdate = "UPDATE EstadoVotacao SET aberta = 1 WHERE id = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
            ResultSet rs = stmtSelect.executeQuery();
            if (rs.next()) {
                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    stmtUpdate.executeUpdate();
                }
            } else {
                try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {
                    stmtInsert.executeUpdate();
                }
            }
            System.out.println("Votação iniciada!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void encerrarVotacao() {
        String sql = "UPDATE EstadoVotacao SET aberta = 0 WHERE id = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("Votação encerrada!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}