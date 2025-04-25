package com.chapo.aggregator.domain.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class LoteDTO implements Serializable {

    private Integer agencia;
    private Long conta;
    private List<LancamentoDTO> lancamentos;
    private String uuid = UUID.randomUUID().toString();

    public static LoteDTO criarLote() {
        LoteDTO lote = new LoteDTO();
        lote.lancamentos = new ArrayList<>();
        return lote;
    }

    public void adicionarLancamento(LancamentoDTO lancamentoDTO) {
        agencia = lancamentoDTO.getAgencia();
        conta = lancamentoDTO.getConta();
        this.lancamentos.add(lancamentoDTO);
    }

    public List<LancamentoDTO> getLancamentos() {
        return this.lancamentos;
    }

    public String getUuid() {
        return uuid;
    }

    public Integer getAgencia() {
        return agencia;
    }

    public Long getConta() {
        return conta;
    }

}
