package com.chapo.aggregator.domain.repository;

import com.chapo.aggregator.domain.entities.Conta;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Optional<Conta> findByAgenciaAndConta(Integer agencia, Long conta);

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Conta c SET c.saldo = c.saldo + :saldo WHERE c.agencia = :agencia and c.conta = :conta")
    void updateSaldo(Integer agencia, Long conta, BigDecimal saldo);

}
