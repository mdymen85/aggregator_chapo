package com.chapo.aggregator.second;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.ReleaseStrategy;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChapoService {

    @CorrelationStrategy
    public Object getCorrelationKey(Message<Lancamento> message) {
        return message.getPayload().getConta();
    }

    @ReleaseStrategy
    public boolean release(List<Message<Lancamento>> messageList) {
        return messageList.size() >= 5;
    }

    @Aggregator(inputChannel = "requestChannel", outputChannel = "outputChannel")
    public Lote aggregate(List<Message<Lancamento>> messageList) {
        Lote lote = Lote.criarLote();

        for (Message<Lancamento> message : messageList) {
            lote.adicionarLancamento(message.getPayload());
        }
        return lote;
    }

    @ServiceActivator(inputChannel = "outputChannel")
    public void handleAggregatedMessage(Lote lote) {
        lote.getLancamentos()
            .forEach(l -> System.out.println(l.getConta() + " " + l.getDescricao() + " " + lote.getUuid()));
    }

}
