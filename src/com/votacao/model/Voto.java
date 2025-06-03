package com.votacao.model;

import java.time.LocalDateTime;

public class Voto {
    private int id;
    private int eleitorId;
    private int idCandidato;
    private LocalDateTime timestamp;

    public Voto() {
    }

    public Voto(int eleitorId, int idCandidato) {
        this.eleitorId = eleitorId;
        this.idCandidato = idCandidato;
        this.timestamp = LocalDateTime.now();
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEleitorId() {
        return eleitorId;
    }

    public void setEleitorId(int eleitorId) {
        this.eleitorId = eleitorId;
    }

    public int getIdCandidato() {
        return idCandidato;
    }

    public void setIdCandidato(int idCandidato) {
        this.idCandidato = idCandidato;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}