package com.chapo.aggregator.domain;

import com.chapo.aggregator.domain.dtos.LancamentoDTO;
import com.chapo.aggregator.domain.dtos.LoteDTO;
import com.chapo.aggregator.domain.entities.Conta;
import com.chapo.aggregator.domain.repository.ContaRepository;
import com.chapo.aggregator.domain.repository.LancamentoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@RequiredArgsConstructor
@Slf4j
//todo: evaluate use cache for the checking account
public class LancamentoService {

    private static Integer MESSAGE_COUNT = 0;

    private final ContaRepository contaRepository;
    private final LancamentoRepository lancamentoRepository;

    public LancamentoService(ContaRepository contaRepository, LancamentoRepository lancamentoRepository) {
        this.contaRepository = contaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    public void save(LoteDTO loteDTO, Conta conta) {

        int quantity = loteDTO.getLancamentos().size();

        if (MESSAGE_COUNT == 0) {
            System.out.println("INIT : " + System.currentTimeMillis());
        }

        MESSAGE_COUNT = MESSAGE_COUNT + quantity;

        BigDecimal net = loteDTO
            .getLancamentos()
            .stream()
            .map(LancamentoDTO::getValorWithSign)
            .reduce(BigDecimal::add)
            .get();

        if (canApply(conta, net)) {
            contaRepository.updateSaldo(loteDTO.getAgencia(), loteDTO.getConta(), net);
        } else {
            //@Mamedio: after we aggregated the entries, what happend with them when we throw an exception for all (inside the batch)
            throw new RuntimeException("insufficient founds!");
        }

        if (MESSAGE_COUNT >= 1000) {
            System.out.println("END : " + System.currentTimeMillis());
        }

    }

    public void save(LancamentoDTO lancamentoDTO) {
        LoteDTO loteDTO = LoteDTO.criarLote();

        Conta conta = getAndLockAccount(lancamentoDTO.getAgencia(), lancamentoDTO.getConta());

        if (canApply(conta, lancamentoDTO.getValorWithSign())) {
            contaRepository.updateSaldo(lancamentoDTO.getAgencia(), lancamentoDTO.getConta(), lancamentoDTO.getValorWithSign());
        }

    }

    private boolean canApply(Conta conta, BigDecimal net) {
        //if the new value that i want to add in the account is positive doesnt matter
        // the actual balance in the account
        if (net.compareTo(BigDecimal.ZERO) >= 0) {
            return true;
        }

        //if the new value isnt it positive, we might return if is negative or not
        return conta.getSaldo().compareTo(BigDecimal.ZERO) > 0;
    }

    private Conta getAndLockAccount(Integer agencia, Long conta) {
        Optional<Conta> optConta = contaRepository.findByAgenciaAndConta(agencia, conta); //lock

        if (optConta.isEmpty()) {
            throw new RuntimeException("Conta não existe");
        }

        return optConta.get();
    }

    @Transactional
    public LoteDTO updateBalance(LoteDTO loteDTO) {
        loteDTO.getLancamentos()
               .forEach(l -> System.out.println(l.getConta() + " " + l.getDescricao() + " " + loteDTO.getUuid()));

        Conta conta = getAndLockAccount(loteDTO.getAgencia(), loteDTO.getConta());

        save(loteDTO, conta);

        return loteDTO;
    }

}
