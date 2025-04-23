package com.chapo.aggregator.domain.repository;

import com.chapo.aggregator.domain.entities.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
