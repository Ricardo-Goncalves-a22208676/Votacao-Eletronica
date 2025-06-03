package com.votacao;

import com.votacao.ui.LoginGUI;
import com.votacao.dao.DatabaseConnection;

public class Main {
    public static void main(String[] args) {
        // Initialize database
        DatabaseConnection.initializeDatabase();

        // Start application
        LoginGUI loginGUI = new LoginGUI();
        loginGUI.mostrarLogin();
    }
}