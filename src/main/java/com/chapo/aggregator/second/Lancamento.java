package com.chapo.aggregator.second;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class Lancamento {

    @JsonProperty("conta")
    private Integer conta;

    @JsonProperty("descricao")
    private String descricao;

    public Integer getConta() {
        return conta;
    }

    public String getDescricao() {
        return descricao;
    }

}
