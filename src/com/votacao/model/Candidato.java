package com.votacao.model;

public class Candidato {
    private int id;
    private String nome;
    private String partido;

    public Candidato() {
    }

    public Candidato(String nome, String partido) {
        this.nome = nome;
        this.partido = partido;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPartido() {
        return partido;
    }

    public void setPartido(String partido) {
        this.partido = partido;
    }
}