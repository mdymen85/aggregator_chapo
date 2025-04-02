//package com.chapo.aggregator;
//
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
//import org.springframework.amqp.support.converter.SimpleMessageConverter;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.config.EnableIntegration;
//import org.springframework.integration.dsl.IntegrationFlow;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHandler;
//import org.springframework.messaging.MessageHeaders;
//import org.springframework.messaging.MessagingException;
//
//@Slf4j
//@Configuration
//@EnableIntegration
//public class IntegrationConfig {
//
//    //... RabbitMQ Connection Factory (from previous example)...
//
//    @Bean
//    public Queue myQueue() {
//        return new Queue("queue-agreggator-chapo", true); // Your queue name, durable
//    }
//
//    @Bean
//    public MessageChannel inputChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean("inputChannel2")
//    public MessageChannel outputChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    public AmqpInboundChannelAdapter amqpInbound(CachingConnectionFactory connectionFactory, Queue myQueue) {
//        DirectMessageListenerContainer container = new DirectMessageListenerContainer(connectionFactory);
//        container.addQueues(myQueue);
//        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(container);
////        adapter.setMessageConverter(new ChapoConverter());
//        adapter.setOutputChannel(outputChannel()); // Connect to your input channel
//        return adapter;
//    }
//
//    /*
//    @ServiceActivator(inputChannel = "aggregatorChannel")
//@Bean
//public MessageHandler aggregator(MessageGroupStore jdbcMessageGroupStore) {
//     AggregatingMessageHandler aggregator =
//                       new AggregatingMessageHandler(new DefaultAggregatingMessageGroupProcessor(),
//                                                 jdbcMessageGroupStore);
//     aggregator.setOutputChannel(resultsChannel());
//     aggregator.setGroupTimeoutExpression(new ValueExpression<>(500L));
//     aggregator.setTaskScheduler(this.taskScheduler);
//     return aggregator;
//}
//     */
//
////
////    @Bean
////    public MessageHandler outputMessageHandler() {
////        return new MessageHandler() {
////            @Override
////            public void handleMessage(Message<?> message) throws MessagingException {
////                System.out.println("Custom MessageHandler received: " + message.getPayload());
////                // Your message processing logic here
////            }
////        };
////    }
////
////    @Bean
////    public void subscribeOutputChannel(
////        @Qualifier("outputChannel") MessageChannel outputChannel,
////        @Qualifier("outputMessageHandler") MessageHandler messageHandler) {
////
////        outputChannel.subscribe(messageHandler);
////    }
//
//    @ServiceActivator(inputChannel = "requestChannel2", outputChannel = "inputChannel2")
//    public void handleMessage(Message<?> message) {
//        byte[] payloadBytes = (byte[]) message.getPayload();
//        String payloadString = new String(payloadBytes, StandardCharsets.UTF_8); // Or another encoding
//        System.out.println("Received message from RabbitMQ: " + payloadString);
//
////        Message<String> message1 = new Message<String>() {
////            @Override
////            public String getPayload() {
////                return "pepito";
////            }
////
////            @Override
////            public MessageHeaders getHeaders() {
////                Map<String, Object> map = new HashMap<>();
////                map.put("groupId", 10);
////                return new MessageHeaders(map);
////            }
////        };
////        MessageChannel messageChannel = (MessageChannel) message.getHeaders().getReplyChannel();
////        messageChannel.send(message1);
//    }
//}