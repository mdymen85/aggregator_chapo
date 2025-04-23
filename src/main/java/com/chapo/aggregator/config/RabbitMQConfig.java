package com.chapo.aggregator.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final String queueName = "queue-aggregator-chapo-release"; // Replace with your desired queue name

    @Bean
    public Queue directQueue() {
        return new Queue(queueName, true); // durable: true
    }

    public String getDirectQueueName() {
        return queueName;
    }

    @Bean
    public MessageConverter stringMessageConverter() {
        return new SimpleMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
                                         MessageConverter stringMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(stringMessageConverter);
        return template;
    }
}