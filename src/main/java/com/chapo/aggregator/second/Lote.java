package com.chapo.aggregator.second;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.ToString;

@ToString
public class Lote {

    private List<Lancamento> lancamentos;
    private String uuid = UUID.randomUUID().toString();

    public static Lote criarLote() {
        Lote lote = new Lote();
        lote.lancamentos = new ArrayList<>();
        return lote;
    }

    public void adicionarLancamento(Lancamento lancamento) {
        this.lancamentos.add(lancamento);
    }

    public List<Lancamento> getLancamentos() {
        return this.lancamentos;
    }

    public String getUuid() {
        return uuid;
    }

}
