package com.chapo.aggregator.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name = "CHAPO_CONTA")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name="AGENCIA", nullable = false, updatable = false)
    private Integer agencia;


    @Column(name="CONTA", nullable = false, updatable = false)
    private Long conta;

    @Column(name="SALDO", nullable = false)
    private BigDecimal saldo;

    public Conta() {

    }

//    public Conta of(Integer agencia, Long conta) {
//        return Conta
//            .builder()
//            .agencia(agencia)
//            .conta(conta)
//            .build();
//    }

    public void updateSaldo(BigDecimal saldo) {
        this.saldo.add(saldo);
    }

}
