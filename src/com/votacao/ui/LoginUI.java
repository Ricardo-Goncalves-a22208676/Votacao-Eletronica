package com.votacao.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class LoginFX extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label lblUser = new Label("Username:");
        TextField txtUser = new TextField();
        Label lblPass = new Label("Password:");
        PasswordField txtPass = new PasswordField();
        Button btnLogin = new Button("Entrar");
        Label lblMsg = new Label();

        btnLogin.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                String user = txtUser.getText();
                String pass = txtPass.getText();
                if ("admin".equals(user) && "admin".equals(pass)) {
                    lblMsg.setText("Login com sucesso!");
                } else {
                    lblMsg.setText("Credenciais inválidas.");
                }
            }
        });

        VBox root = new VBox(10, lblUser, txtUser, lblPass, txtPass, btnLogin, lblMsg);
        root.setStyle("-fx-padding: 20;");
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login - Votação Eletrónica");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}