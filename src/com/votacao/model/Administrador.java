package com.votacao.model;

public class Administrador extends Usuario {

    public Administrador() {}

    public Administrador(String nome, String username, String passwordHash) {
        super(nome, username, passwordHash);
    }
}