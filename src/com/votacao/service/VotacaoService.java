package com.votacao.service;

import com.votacao.model.*;
import com.votacao.dao.*;
import java.util.List;

public class VotacaoService {
    public VotoDAO votoDAO;
    private EleitorDAO eleitorDAO;
    private CandidatoDAO candidatoDAO;

    public VotacaoService() {
        this.votoDAO = new VotoDAO();
        this.eleitorDAO = new EleitorDAO();
        this.candidatoDAO = new CandidatoDAO();
    }

    public boolean votar(int idEleitor, int idCandidato) {
        // Verificar se votação está aberta
        if (!SistemaVotacao.isVotacaoAberta()) {
            System.out.println("DEBUG: Votação não está aberta.");
            return false;
        }

        // Verificar se eleitor já votou
        EleitorDAO eleitorDAO = new EleitorDAO();
        Eleitor eleitor = eleitorDAO.buscarPorId(idEleitor);
        if (eleitor == null) {
            System.out.println("DEBUG: Eleitor não encontrado.");
            return false;
        }
        if (eleitor.isVotou()) {
            System.out.println("DEBUG: Eleitor já votou.");
            return false;
        }

        // Verificar se candidato existe
        CandidatoDAO candidatoDAO = new CandidatoDAO();
        Candidato candidato = candidatoDAO.buscarPorId(idCandidato);
        if (candidato == null) {
            System.out.println("DEBUG: Candidato não encontrado.");
            return false;
        }

        // Registar voto
        VotoDAO votoDAO = new VotoDAO();
        Voto voto = new Voto(idEleitor, idCandidato);
        votoDAO.inserir(voto);

        // Marcar eleitor como votou
        eleitor.setVotou(true);
        eleitorDAO.atualizar(eleitor);

        return true;
    }

    public List<Candidato> listarCandidatos() {
        return candidatoDAO.listarTodos();
    }

    public boolean podeVotar(int idEleitor) {
        // Verifica se a votação está aberta
        if (!SistemaVotacao.isVotacaoAberta()) {
            return false;
        }
        // Verifica se o eleitor existe
        Eleitor eleitor = eleitorDAO.buscarPorId(idEleitor);
        if (eleitor == null) {
            return false;
        }
        // Verifica se o eleitor já votou
        return !eleitor.isVotou();
    }
}