package com.chapo.aggregator.domain;

import com.chapo.aggregator.domain.dtos.LancamentoDTO;
import com.chapo.aggregator.domain.dtos.LoteDTO;
import com.chapo.aggregator.domain.entities.Conta;
import com.chapo.aggregator.domain.entities.Lancamento;
import com.chapo.aggregator.domain.entities.TipoLancamento;
import com.chapo.aggregator.domain.repository.ContaRepository;
import com.chapo.aggregator.domain.repository.LancamentoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@RequiredArgsConstructor
@Slf4j
//todo: evaluate use cache for the checking account
public class LancamentoService {

//    public static final ConcurrentHashMap<Long, Long> TIME = new ConcurrentHashMap<>();

    @Value("${application.aggregator.sleep:20}")
    private Integer sleep;

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

        sem Spring integration sem nenhum sleep
        1746186449257 - 1746186454812 = 5.555 seconds
        1746186425001 - 1746186431228 = 6.227 seconds

        com sleep de 50ms sem Spring integration:
        1747394009973 - 1747393993720 = 16.253 seconds
        1747394025508 - 1747394010014 = 15.494 seconds
        1747394041754 - 1747394025537 = 16.217 seconds

        com sleep de 50ms com Spring Integration agrupando de a 10. -> provavelmente problema de memoria
        1747394268221 - 1747394257776 = 10.455 seconds
        1747394281339 - 1747394268248 = 13.091 seconds
        1747394299955 - 1747394282750 = 17.205 seconds

        com sleep de 50ms com Spring Integration agrupando de a 5. -> provavelmente problema de memoria
        1747394736592 - 1747394725748 = 10.844 seconds
        1747394756772 - 1747394736625 = 20.147 seconds
        1747394778914 - 1747394756973 = 21.941 seconds

        apos reiniciar : com sleep de 50ms com Spring Integration agrupando de a 5. -> provavelmente problema de memoria
        1747395217828 - 1747395206596 = 11.232 seconds
        1747395240948 - 1747395217856 = 23.092 seconds
        1747395262956 - 1747395241187 = 21.769 seconds

        1747395498561 - 1747395487264 = 11.297 seconds
        1747395515995 - 1747395498593 = 17.402 seconds
        1747395540325 - 1747395516698 = 23.627 seconds

        de 1 em 1 -> lock. (A)
        de 10 em 10 -> lock + acesso a base + net + memoria + spring integration (B)

        (A) > (B)

        Teste - Máquina Mamédio

        1747828010221 - 1747828010219 = 2 ms   - Inicio do processamento até Inicio de lock
        1747828010248 - 1747828010221 = 27 ms  - Inicio de lock até Inicio do NET
        1747828010248 - 1747828010248 = 0 ms   - Inicio do NET até Fim do NET
        1747828010262 - 1747828010248 = 14 ms  - Fim do NET até Fim do processamento
        1747828010426 - 1747828010262 = 164 ms - Fim do processamento até Inicio do processamento

        Sem o aggregator, levou cerca de 220ms para processar 5 mensagens.

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
//        if (MESSAGE_COUNT == 0) {
//            init = System.currentTimeMillis();
//            System.out.println("INIT : " + init);
//        }

        MESSAGE_COUNT = MESSAGE_COUNT + quantity;

//        System.out.println("[" + Thread.currentThread().getId() + "]" + "Inicio do NET : " + System.currentTimeMillis());
        BigDecimal net = loteDTO
            .getLancamentos()
            .stream()
            .map(LancamentoDTO::getValorWithSign)
            .reduce(BigDecimal::add)
            .get();
//        System.out.println("[" + Thread.currentThread().getId() + "]" + "Fim do NET : " + System.currentTimeMillis());

        if (canApply(conta, net)) {
//            lancamentoRepository.saveAll(loteDTO.getLancamentos().stream().map(this::from).toList());
            contaRepository.updateSaldo(loteDTO.getAgencia(), loteDTO.getConta(), net);
//            contaRepository.updateSaldo(loteDTO.getAgencia(), loteDTO.getConta(), BigDecimal.ZERO);
        } else {
            //@Mamedio: after we aggregated the entries, what happend with them when we throw an exception for all (inside the batch)
            throw new RuntimeException("insufficient founds!");
        }

//        if (MESSAGE_COUNT >= 500) {
//            long end = System.currentTimeMillis();
//            System.out.println("END : " + end);
//            long result = end - init;
//            System.out.println("RESULT : " + result);
//            MESSAGE_COUNT = 0;
//        }

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
        System.out.println("[" + Thread.currentThread().getName() + "]" + "Conta : " + loteDTO.getConta() + " Tenta pegar o lock : " + LocalDateTime.now());
        Conta conta = getAndLockAccount(loteDTO.getAgencia(), loteDTO.getConta());
        System.out.println("[" + Thread.currentThread().getName() + "]" + "Conta : " + loteDTO.getConta() + " Lock foi pego : " + LocalDateTime.now());
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        save(loteDTO, conta);

        return loteDTO;
    }

}
