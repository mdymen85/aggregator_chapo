package com.chapo.aggregator.domain;

import com.chapo.aggregator.domain.dtos.LancamentoDTO;
import com.chapo.aggregator.domain.dtos.LoteDTO;
import com.chapo.aggregator.domain.entities.Conta;
import com.chapo.aggregator.domain.repository.ContaRepository;
import com.chapo.aggregator.domain.repository.LancamentoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@RequiredArgsConstructor
@Slf4j
public class LancamentoService {

    private final ContaRepository contaRepository;
    private final LancamentoRepository lancamentoRepository;

    public LancamentoService(ContaRepository contaRepository, LancamentoRepository lancamentoRepository) {
        this.contaRepository = contaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    public void save(LoteDTO loteDTO) {
        BigDecimal net = loteDTO
            .getLancamentoDTOS()
            .stream()
            .map(LancamentoDTO::getValorWithSign)
            .reduce(BigDecimal::add)
            .get();

        contaRepository.updateSaldo(loteDTO.getAgencia(), loteDTO.getConta(), net);
    }

    @Transactional
    public LoteDTO updateBalance(LoteDTO loteDTO) {
        loteDTO.getLancamentoDTOS()
               .forEach(l -> System.out.println(l.getConta() + " " + l.getDescricao() + " " + loteDTO.getUuid()));

        Optional<Conta> optConta = contaRepository.findByAgenciaAndConta(loteDTO.getAgencia(), loteDTO.getConta());
        if (optConta.isEmpty()) {
            throw new RuntimeException("Conta não existe");
        }

        save(loteDTO);

        return loteDTO;
    }

}
