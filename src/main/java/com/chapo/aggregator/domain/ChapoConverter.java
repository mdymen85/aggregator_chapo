package com.chapo.aggregator.domain;

import com.chapo.aggregator.domain.dtos.LancamentoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AllowedListDeserializingMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.beans.factory.BeanClassLoaderAware;

public class ChapoConverter extends AllowedListDeserializingMessageConverter implements BeanClassLoaderAware {



    @Override
    protected Message createMessage(Object o, MessageProperties messageProperties) {
        return null;
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] payloadBytes = message.getBody();
        String payloadString = new String(payloadBytes, StandardCharsets.UTF_8); // Or another encoding
//        System.out.println("Received message from RabbitMQ: " + payloadString + " " + Thread.currentThread().getId());
        try {
            return objectMapper.readValue(payloadString, LancamentoDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {

    }
}
