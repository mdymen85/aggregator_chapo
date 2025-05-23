package com.chapo.aggregator.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OnAggregatorDisabled
public class RabbitMQConfig {

    private final String queueName = "queue-agreggator-chapo"; // Replace with your desired queue name

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

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(5);      // Start with 5 consumer threads
        factory.setMaxConcurrentConsumers(10);  // Allow up to 10 consumer threads
        factory.setPrefetchCount(1);           // Only fetch one message at a time per consumer
        // (important for fair dispatch in round-robin)
        factory.setDefaultRequeueRejected(false); // Do not requeue rejected messages by default
        return factory;
    }
}