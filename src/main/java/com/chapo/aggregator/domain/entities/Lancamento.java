package com.chapo.aggregator.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;

@Entity
@Table(name = "CHAPO_LANCAMENTO")
@Data
public class Lancamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name="AGENCIA", nullable = false, updatable = false)
    private Integer agencia;


    @Column(name="CONTA", nullable = false, updatable = false)
    private Long conta;

    @Column(name="VALOR", nullable = false, updatable = false)
    private BigDecimal valor;

    @Column(name="HISTORICO", nullable = false, updatable = false)
    private Integer historico;

    @Column(name="TIPO", nullable = false, updatable = false)
    @Enumerated(value = EnumType.STRING)
    private TipoLancamento tipo;

    public Lancamento() {

    }

//    public Lancamento from(Integer agencia, Long conta, BigDecimal valor, Integer historico, TipoLancamento tipo) {
//        return Lancamento
//            .builder()
//            .agencia(agencia)
//            .historico(historico)
//            .tipo(tipo)
//            .conta(conta)
//            .valor(valor)
//            .build();
//    }


}
