package com.chapo.aggregator.config;

import com.chapo.aggregator.domain.LancamentoService;
import com.chapo.aggregator.domain.dtos.LancamentoDTO;
import com.chapo.aggregator.domain.dtos.LoteDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
@OnAggregatorDisabled
public class MyMessageListener {

    private final LancamentoService lancamentoService;
    private final ObjectMapper objectMapper;

    public MyMessageListener(LancamentoService lancamentoService, ObjectMapper objectMapper) {
        this.lancamentoService = lancamentoService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "queue-agreggator-chapo")
    public void receiveMessage(Message<?> message) throws JsonProcessingException {
        byte[] payloadBytes = (byte[]) message.getPayload();
        String payloadString = new String(payloadBytes, StandardCharsets.UTF_8); // Or another encoding
        LancamentoDTO lancamentoDTO = this.objectMapper.readValue(payloadString, LancamentoDTO.class);
        lancamentoService.updateBalance(LoteDTO.with(lancamentoDTO));
    }
}