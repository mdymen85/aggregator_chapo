//package com.chapo.aggregator;
//
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.springframework.integration.annotation.Aggregator;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHeaders;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AgregadorChapoService {
//
//    @Aggregator(inputChannel = "inputChannel2", outputChannel = "requestChannel2")
//    public List<String> aggregate(List<Message<String>> messages) {
//        System.out.println("Aggregating messages: " + messages);
//        List<String> payloads = new ArrayList<>();
//        for (Message<String> message : messages) {
//            payloads.add(message.getPayload());
//        }
//        return payloads;
//    }
//
//    @ServiceActivator(inputChannel = "activator-channel")
//    public void handleMessage(Message<?> message) {
//        byte[] payloadBytes = (byte[]) message.getPayload();
//        String payloadString = new String(payloadBytes, StandardCharsets.UTF_8); // Or another encoding
//        System.out.println("Received message from RabbitMQ: " + payloadString);
//
//        Message<String> message1 = new Message<String>() {
//            @Override
//            public String getPayload() {
//                return "pepito";
//            }
//
//            @Override
//            public MessageHeaders getHeaders() {
//                Map<String, Object> map = new HashMap<>();
//                map.put("groupId", 10);
//                return new MessageHeaders(map);
//            }
//        };
//        MessageChannel messageChannel = (MessageChannel) message.getHeaders().getReplyChannel();
//        messageChannel.send(message1);
//    }
//
//}
