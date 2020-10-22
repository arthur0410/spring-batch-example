package com.example.batchprocessing;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RESTPropostasReader implements ItemReader<Proposta> {

    @Value("${api.url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    private int proximaPropostaIndex;
    private List<Proposta> propostas;

    @Override
    public Proposta read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (studentDataIsNotInitialized()) {
            propostas = fetchPropostasFromAPI();
        }

        Proposta proximaProposta = null;

        if (proximaPropostaIndex < propostas.size()) {
            proximaProposta = propostas.get(proximaPropostaIndex);
            proximaPropostaIndex++;
        }

        return proximaProposta;
    }


    private boolean studentDataIsNotInitialized() {
        return this.propostas == null;
    }

    private List<Proposta> fetchPropostasFromAPI() {
        ResponseEntity<Proposta[]> response = restTemplate.getForEntity(
                apiUrl,
                Proposta[].class);

        Proposta[] propostas = response.getBody();
        return Arrays.asList(propostas);
    }
}
