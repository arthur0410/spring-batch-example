package com.example.batchprocessing;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Proposta {

    @JsonProperty("id_proposta")
    private int idProposta;

    public Proposta() {
    }

    public Proposta(int idProposta) {
        this.idProposta = idProposta;
    }

    public int getIdProposta() {
        return idProposta;
    }

    public void setIdProposta(int idProposta) {
        this.idProposta = idProposta;
    }
}
