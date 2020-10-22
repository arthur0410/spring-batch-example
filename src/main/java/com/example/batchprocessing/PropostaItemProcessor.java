package com.example.batchprocessing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PropostaItemProcessor implements ItemProcessor<Proposta, Proposta> {

    private static final Logger log = LoggerFactory.getLogger(PropostaItemProcessor.class);

    @Override
    public Proposta process(final Proposta proposta) throws Exception {

        log.info("Converting (" + proposta.getIdProposta() + ") into (" + proposta.getIdProposta() + ")");

        return proposta;
    }
}
