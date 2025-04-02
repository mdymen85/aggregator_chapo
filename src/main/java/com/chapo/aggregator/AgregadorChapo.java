//package com.chapo.aggregator;
//
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.integration.annotation.Aggregator;
//import org.springframework.integration.annotation.CorrelationStrategy;
//import org.springframework.integration.annotation.MessageEndpoint;
//import org.springframework.integration.annotation.MessagingGateway;
//import org.springframework.integration.annotation.ReleaseStrategy;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.store.MessageGroup;
//import org.springframework.messaging.Message;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHandler;
//import org.springframework.messaging.MessageHeaders;
//import org.springframework.messaging.MessagingException;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.ArrayList;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Component
////@MessageEndpoint
//@Slf4j
//@MessagingGateway
//public class AgregadorChapo {
//
////    @ReleaseStrategy
////    public boolean canRelease(MessageGroup group) {
////        return true;
////    }
//////
//    @CorrelationStrategy
//    public Object getCorrelationKey(Message<?> message) {
//        // Correlate based on a custom header "groupId"
//    //            return message.getHeaders().get("groupId").toString();
//        return 10;
//    }
//
////    // Custom Correlation Strategy
////    public static class CustomCorrelationStrategy implements CorrelationStrategy {
////        @Override
////        public Object getCorrelationKey(Message<?> message) {
////            // Correlate based on a custom header "groupId"
////            return message.getHeaders().get("groupId");
////        }
////    }
//
//    // Custom Release Strategy
////    public static class CustomReleaseStrategy implements ReleaseStrategy {
////
////        private final int completionSize;
////
////        public CustomReleaseStrategy(int completionSize) {
////            this.completionSize = completionSize;
////        }
////
//////        @Override
//////        public boolean canRelease(List<Message<?>> messages) {
//////            // Release when a specific number of messages with the same groupId are received.
//////            return messages.size() == completionSize;
//////        }
////
////        @Override
////        public boolean canRelease(MessageGroup group) {
////            return false;
////        }
////    }
//
//
//    private final AtomicInteger counter = new AtomicInteger(0);
//
////    @Aggregator(inputChannel = "inputChannel", outputChannel = "outputChannel")
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
////    @Override
////    public void handleMessage(Message<?> message) throws MessagingException {
////        System.out.println("Received aggregated message: " + message);
//////        log.info("handle message chapo {}", message);
////    }
//
////    @ServiceActivator(inputChannel = "outputChannel")
////    public void handleAggregatedMessages(List<String> aggregatedPayload) {
////        System.out.println("Received aggregated message: " + aggregatedPayload);
////        counter.incrementAndGet();
////    }
//
//
//
//}
