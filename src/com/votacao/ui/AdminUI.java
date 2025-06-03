package com.votacao.ui;

import com.votacao.model.Candidato;
import com.votacao.service.RelatorioService;
import com.votacao.service.VotacaoService;
import com.votacao.dao.CandidatoDAO;
import com.votacao.dao.EleitorDAO;
import com.votacao.model.Eleitor;
import com.votacao.model.SistemaVotacao;

import java.util.List;
import java.util.Scanner;

public class AdminUI {
    private Scanner scanner = new Scanner(System.in);
    private CandidatoDAO candidatoDAO = new CandidatoDAO();
    private EleitorDAO eleitorDAO = new EleitorDAO();
    private VotacaoService votacaoService = new VotacaoService();

    public void mostrarMenu() {
        int opcao;
        do {
            System.out.println("\n=== Menu Administrador ===");
            System.out.println("1. Gerir Candidatos");
            System.out.println("2. Gerir Eleitores");
            System.out.println("3. Iniciar Votação");
            System.out.println("4. Encerrar Votação");
            System.out.println("5. Ver Resultados");
            System.out.println("0. Sair");
            System.out.print("Opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1:
                    gerirCandidatos();
                    break;
                case 2:
                    gerirEleitores();
                    break;
                case 3:
                    SistemaVotacao.iniciarVotacao();
                    break;
                case 4:
                    SistemaVotacao.encerrarVotacao();
                    break;
                case 5:
                    verResultados();
                    break;
                case 0:
                    System.out.println("A sair...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void gerirCandidatos() {
        int opcao;
        do {
            System.out.println("\n--- Gestão de Candidatos ---");
            System.out.println("1. Listar Candidatos");
            System.out.println("2. Adicionar Candidato");
            System.out.println("3. Editar Candidato");
            System.out.println("4. Remover Candidato");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    List<Candidato> candidatos = candidatoDAO.listarTodos();
                    System.out.println("Candidatos:");
                    for (Candidato c : candidatos) {
                        System.out.println(c.getId() + " - " + c.getNome() + " (" + c.getPartido() + ")");
                    }
                    break;
                case 2:
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    if (!com.votacao.utils.ValidationUtils.isValidNome(nome)) {
                        System.out.println("Nome inválido!");
                        break;
                    }
                    System.out.print("Número de Eleitor: ");
                    String numeroEleitor = scanner.nextLine();
                    if (!com.votacao.utils.ValidationUtils.isValidNumeroEleitor(numeroEleitor)) {
                        System.out.println("Número de eleitor inválido! (Exemplo: E123)");
                        break;
                    }
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    if (!com.votacao.utils.ValidationUtils.isValidUsername(username)) {
                        System.out.println("Username inválido! (Apenas letras e números, 3-20 caracteres)");
                        break;
                    }
                    if (eleitorDAO.buscarPorUsername(username) != null) {
                        System.out.println("Erro: username já existe!");
                        break;
                    }
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    if (!com.votacao.utils.ValidationUtils.isNotEmpty(password)) {
                        System.out.println("Password não pode estar vazia!");
                        break;
                    }
                    String passwordHash = com.votacao.utils.PasswordUtils.hashPassword(password);
                    boolean sucesso = eleitorDAO.inserir(new Eleitor(nome, username, passwordHash, numeroEleitor));
                    if (sucesso) {
                        System.out.println("Eleitor adicionado!");
                    } else {
                        System.out.println("Erro ao adicionar eleitor.");
                    }
                    break;
                case 3:
                    System.out.print("ID do candidato a editar: ");
                    int idEdit = scanner.nextInt();
                    scanner.nextLine();
                    Candidato cEdit = candidatoDAO.buscarPorId(idEdit);
                    if (cEdit != null) {
                        System.out.print("Novo nome (" + cEdit.getNome() + "): ");
                        String novoNome = scanner.nextLine();
                        System.out.print("Novo partido (" + cEdit.getPartido() + "): ");
                        String novoPartido = scanner.nextLine();
                        cEdit.setNome(novoNome.isEmpty() ? cEdit.getNome() : novoNome);
                        cEdit.setPartido(novoPartido.isEmpty() ? cEdit.getPartido() : novoPartido);
                        candidatoDAO.atualizar(cEdit);
                        System.out.println("Candidato atualizado!");
                    } else {
                        System.out.println("Candidato não encontrado.");
                    }
                    break;
                case 4:
                    System.out.print("ID do candidato a remover: ");
                    int idRemover = scanner.nextInt();
                    scanner.nextLine();
                    candidatoDAO.remover(idRemover);
                    System.out.println("Candidato removido!");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void gerirEleitores() {
        int opcao;
        do {
            System.out.println("\n--- Gestão de Eleitores ---");
            System.out.println("1. Listar Eleitores");
            System.out.println("2. Adicionar Eleitor");
            System.out.println("3. Editar Eleitor");
            System.out.println("4. Remover Eleitor");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    List<Eleitor> eleitores = eleitorDAO.listarTodos();
                    System.out.println("Eleitores:");
                    for (Eleitor e : eleitores) {
                        System.out.println(e.getId() + " - " + e.getNome() + " (" + e.getNumeroEleitor() + ")");
                    }
                    break;
                case 2:
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("Número de Eleitor: ");
                    String numeroEleitor = scanner.nextLine();
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    String passwordHash = com.votacao.utils.PasswordUtils.hashPassword(password);
                    eleitorDAO.inserir(new Eleitor(nome, username, passwordHash, numeroEleitor));
                    System.out.println("Eleitor adicionado!");
                    break;
                case 3:
                    System.out.print("ID do eleitor a editar: ");
                    int idEdit = scanner.nextInt();
                    scanner.nextLine();
                    Eleitor eEdit = eleitorDAO.buscarPorId(idEdit);
                    if (eEdit != null) {
                        System.out.print("Novo nome (" + eEdit.getNome() + "): ");
                        String novoNome = scanner.nextLine();
                        System.out.print("Novo número de eleitor (" + eEdit.getNumeroEleitor() + "): ");
                        String novoNumero = scanner.nextLine();
                        System.out.print("Novo username (" + eEdit.getUsername() + "): ");
                        String novoUsername = scanner.nextLine();
                        System.out.print("Nova password (deixe vazio para manter): ");
                        String novaPassword = scanner.nextLine();
                        if (!novaPassword.isEmpty()) {
                            eEdit.setPasswordHash(com.votacao.utils.PasswordUtils.hashPassword(novaPassword));
                        }
                        eEdit.setNome(novoNome.isEmpty() ? eEdit.getNome() : novoNome);
                        eEdit.setNumeroEleitor(novoNumero.isEmpty() ? eEdit.getNumeroEleitor() : novoNumero);
                        eEdit.setUsername(novoUsername.isEmpty() ? eEdit.getUsername() : novoUsername);
                        eleitorDAO.atualizar(eEdit);
                        System.out.println("Eleitor atualizado!");
                    } else {
                        System.out.println("Eleitor não encontrado.");
                    }
                    break;
                case 4:
                    System.out.print("ID do eleitor a remover: ");
                    int idRemover = scanner.nextInt();
                    scanner.nextLine();
                    eleitorDAO.remover(idRemover);
                    System.out.println("Eleitor removido!");
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void verResultados() {
        RelatorioService relatorioService = new RelatorioService();
        relatorioService.mostrarResultados();
    }
}