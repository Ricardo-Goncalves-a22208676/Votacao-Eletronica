package com.votacao.model;

public class Eleitor extends Usuario {
    private String numeroEleitor;
    private boolean votou;

    public Eleitor() {}

    public Eleitor(String nome, String username, String passwordHash, String numeroEleitor) {
        super(nome, username, passwordHash);
        this.numeroEleitor = numeroEleitor;
        this.votou = false;
    }

    // Getters e Setters
    public String getNumeroEleitor() { return numeroEleitor; }
    public void setNumeroEleitor(String numeroEleitor) { this.numeroEleitor = numeroEleitor; }

    public boolean isVotou() { return votou; }
    public void setVotou(boolean votou) { this.votou = votou; }
}