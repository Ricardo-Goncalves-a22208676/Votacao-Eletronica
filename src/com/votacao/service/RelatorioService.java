package com.votacao.service;

import com.votacao.dao.CandidatoDAO;
import com.votacao.dao.VotoDAO;
import com.votacao.model.Candidato;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatorioService {
    private CandidatoDAO candidatoDAO;
    private VotoDAO votoDAO;

    public RelatorioService() {
        this.candidatoDAO = new CandidatoDAO();
        this.votoDAO = new VotoDAO();
    }

    public void mostrarResultados() {
        List<Candidato> candidatos = candidatoDAO.listarTodos();
        int totalVotos = 0;
        Map<Integer, Integer> votosPorCandidato = new HashMap<>();

        // Conta votos por candidato
        for (Candidato c : candidatos) {
            int votos = votoDAO.contarVotosPorCandidato(c.getId());
            votosPorCandidato.put(c.getId(), votos);
            totalVotos += votos;
        }

        System.out.println("\n--- Resultados da Votação ---");
        for (Candidato c : candidatos) {
            int votos = votosPorCandidato.getOrDefault(c.getId(), 0);
            double percentagem = totalVotos > 0 ? (votos * 100.0) / totalVotos : 0;
            System.out.printf("%s (%s): %d votos (%.2f%%)%n", c.getNome(), c.getPartido(), votos, percentagem);
        }

        // Determina o vencedor
        Candidato vencedor = null;
        int maxVotos = -1;
        for (Candidato c : candidatos) {
            int votos = votosPorCandidato.getOrDefault(c.getId(), 0);
            if (votos > maxVotos) {
                maxVotos = votos;
                vencedor = c;
            }
        }
        if (vencedor != null) {
            System.out.println("Vencedor: " + vencedor.getNome() + " (" + vencedor.getPartido() + ")");
        } else {
            System.out.println("Nenhum voto registado.");
        }
    }
}