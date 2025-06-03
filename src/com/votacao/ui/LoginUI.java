package com.votacao.ui;

import com.votacao.model.Eleitor;
import com.votacao.service.AutenticacaoService;
import com.votacao.model.Usuario;
import com.votacao.utils.ValidationUtils;

import java.util.Scanner;

public class LoginUI {
    private AutenticacaoService autenticacaoService;
    private Scanner scanner;

    public LoginUI() {
        this.autenticacaoService = new AutenticacaoService();
        this.scanner = new Scanner(System.in);
    }

    public void mostrarLogin() {
        System.out.println("=== SISTEMA DE VOTAÇÃO ELETRÓNICA ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();

        if (!ValidationUtils.isNotEmpty(username)) {
            System.out.println("O username não pode estar vazio!");
            mostrarLogin();
            return;
        }

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (!ValidationUtils.isNotEmpty(password)) {
            System.out.println("A password não pode estar vazia!");
            mostrarLogin();
            return;
        }

        Usuario usuario = autenticacaoService.autenticar(username, password);

        if (usuario != null) {
            if (autenticacaoService.isAdministrador(usuario)) {
                AdminUI adminUI = new AdminUI();
                adminUI.mostrarMenu();
            } else if (autenticacaoService.isEleitor(usuario)) {
                EleitorUI eleitorUI = new EleitorUI((Eleitor) usuario);
                eleitorUI.mostrarMenu();
            }
        } else {
            System.out.println("Credenciais inválidas!");
            mostrarLogin();
        }
    }
}