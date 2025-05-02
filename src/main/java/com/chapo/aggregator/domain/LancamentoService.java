package com.chapo.aggregator.domain;

import com.chapo.aggregator.domain.dtos.LancamentoDTO;
import com.chapo.aggregator.domain.dtos.LoteDTO;
import com.chapo.aggregator.domain.entities.Conta;
import com.chapo.aggregator.domain.entities.Lancamento;
import com.chapo.aggregator.domain.entities.TipoLancamento;
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

    /*
        1 per Lote
        1746012082798 - 1746012123280 = 40.482 seconds
        1746012123351 - 1746012161730 = 38.379 seconds
        1746012161795 - 1746012199431 = 37.636 seconds

        5 per Lote
        1746012827975 - 1746012848551 = 20.576 seconds
        1746012848734 - 1746012867333 = 18.599 seconds
        1746012867494 - 1746012887373 - 19.879 seconds

        5 por lote sem NET
        1746013293222 - 1746013312618 = 19.396 seconds
        1746013312781 - 1746013331611 = 18.830 seconds
        1746013331805 - 1746013350367 = 18.562 seconds

        1 por lote salvando o lançamento
        1746014163450 - 1746014202242 = 38.792 seconds
        1746014202304 - 1746014241943 = 39.639 seconds
        1746014242018 - 1746014286680 = 44.662 seconds

        10 por lote com net
        1746014374700 - 1746014390022 = 15.322 seconds
        1746014390306 - 1746014406980 = 16.674 seconds
        1746014407280 - 1746014423354 = 16.074 seconds

        1 por fez sem Spring integration
        1746186449257 - 1746186454812 = 5.555 seconds
        1746186425001 - 1746186431228 = 6.227 seconds

        16.68 seconds

     */

    private static Integer MESSAGE_COUNT = 0;

    private final ContaRepository contaRepository;
    private final LancamentoRepository lancamentoRepository;

    public LancamentoService(ContaRepository contaRepository, LancamentoRepository lancamentoRepository) {
        this.contaRepository = contaRepository;
        this.lancamentoRepository = lancamentoRepository;
    }

    public void save(LoteDTO loteDTO, Conta conta) {

        int quantity = loteDTO.getLancamentos().size();

        long init = 0;
        if (MESSAGE_COUNT == 0) {
            init = System.currentTimeMillis();
            System.out.println("INIT : " + init);
        }

        MESSAGE_COUNT = MESSAGE_COUNT + quantity;

        BigDecimal net = loteDTO
            .getLancamentos()
            .stream()
            .map(LancamentoDTO::getValorWithSign)
            .reduce(BigDecimal::add)
            .get();

        if (canApply(conta, net)) {
//            lancamentoRepository.saveAll(loteDTO.getLancamentos().stream().map(this::from).toList());
            contaRepository.updateSaldo(loteDTO.getAgencia(), loteDTO.getConta(), net);
//            contaRepository.updateSaldo(loteDTO.getAgencia(), loteDTO.getConta(), BigDecimal.ZERO);
        } else {
            //@Mamedio: after we aggregated the entries, what happend with them when we throw an exception for all (inside the batch)
            throw new RuntimeException("insufficient founds!");
        }

        if (MESSAGE_COUNT >= 500) {
            long end = System.currentTimeMillis();
            System.out.println("END : " + end);
            long result = end - init;
            System.out.println("RESULT : " + result);
            MESSAGE_COUNT = 0;
        }

    }

    private Lancamento from(LancamentoDTO lancamentoDTO) {
        Lancamento lancamento = new Lancamento();
        lancamento.setAgencia(lancamentoDTO.getAgencia());
        lancamento.setConta(lancamentoDTO.getConta());
        lancamento.setValor(lancamentoDTO.getValor());
        lancamento.setHistorico(1);
        lancamento.setTipo(TipoLancamento.valueOf(lancamentoDTO.getTipo().name()));
        return lancamento;
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
//        loteDTO.getLancamentos()
//               .forEach(l -> System.out.println(MESSAGE_COUNT + " : " + l.getConta() + " " + l.getDescricao() + " " + loteDTO.getUuid()));

        Conta conta = getAndLockAccount(loteDTO.getAgencia(), loteDTO.getConta());
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        save(loteDTO, conta);

        return loteDTO;
    }

}
