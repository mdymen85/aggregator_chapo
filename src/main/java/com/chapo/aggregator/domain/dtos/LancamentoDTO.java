package com.chapo.aggregator.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@Builder
@ToString
public class LancamentoDTO implements Serializable {

    @Getter
    @JsonProperty("conta")
    private Long conta;

    @Getter
    @JsonProperty("agencia")
    private Integer agencia;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("valor")
    private BigDecimal valor;

    @JsonProperty("tipo")
    private TipoLancamento tipo;

    public void setConta(Long conta) {
        this.conta = conta;
    }

    public void setAgencia(Integer agencia) {
        this.agencia = agencia;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoLancamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoLancamento tipo) {
        this.tipo = tipo;
    }

    public enum TipoLancamento {
        D, C
    }

    public BigDecimal getValorWithSign() {
        if (tipo.name().equalsIgnoreCase(TipoLancamento.D.name())) {
            return valor.negate();
        }
        return valor;
    }

    public Integer getAgencia() {
        return agencia;
    }

    public Long getConta() {
        return conta;
    }

    public String getDescricao() {
        return descricao;
    }

}
