package com.votacao.ui;

import com.votacao.model.Candidato;
import com.votacao.model.Eleitor;
import com.votacao.model.SistemaVotacao;
import com.votacao.service.VotacaoService;
import com.votacao.dao.EleitorDAO;

import java.util.List;
import java.util.Scanner;

public class EleitorUI {
    private Scanner scanner = new Scanner(System.in);
    private VotacaoService votacaoService = new VotacaoService();
    private EleitorDAO eleitorDAO = new EleitorDAO();
    private Eleitor eleitorAtual;

    public EleitorUI() {
        // O eleitor atual deve ser passado após autenticação
    }

    public EleitorUI(Eleitor eleitor) {
        this.eleitorAtual = eleitor;
    }

    public void mostrarMenu() {
        System.out.println("\n=== Menu Eleitor ===");
        if (!SistemaVotacao.isVotacaoAberta()) {
            System.out.println("A votação ainda não está aberta.");
            return;
        }
        if (eleitorAtual == null) {
            System.out.println("Erro: eleitor não autenticado.");
            return;
        }
        if (eleitorAtual.isVotou()) {
            System.out.println("Já votou. Obrigado pela sua participação!");
            return;
        }
        List<Candidato> candidatos = votacaoService.listarCandidatos();
        System.out.println("Candidatos disponíveis:");
        for (Candidato c : candidatos) {
            System.out.println(c.getId() + " - " + c.getNome() + " (" + c.getPartido() + ")");
        }
        System.out.print("Digite o ID do candidato em quem deseja votar: ");
        int idCandidato = scanner.nextInt();
        scanner.nextLine();
        boolean sucesso = votacaoService.votar(eleitorAtual.getId(), idCandidato);
        if (sucesso) {
            System.out.println("Voto registado com sucesso! Obrigado por votar.");
            eleitorAtual.setVotou(true);
            eleitorDAO.atualizar(eleitorAtual);
        } else {
            System.out.println("Não foi possível registar o voto. Verifique se já votou ou se o candidato existe.");
        }
    }
}