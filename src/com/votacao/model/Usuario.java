package com.votacao.model;

public abstract class Usuario {
    protected int id;
    protected String nome;
    protected String username;
    protected String passwordHash;

    // Construtores
    public Usuario() {}

    public Usuario(String nome, String username, String passwordHash) {
        this.nome = nome;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}