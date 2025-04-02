//package com.chapo.aggregator;
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class MessageReceiver {
//
//    @RabbitListener(queues = "queue-agreggator-chapo") // Listen to this queue
//    public void receiveMessage(String message) {
//        System.out.println("Received message: " + message);
//        // Process the received message here
//    }
//
////    @RabbitListener(queues = "another_queue")
////    public void receiveAnotherMessage(String message) {
////        System.out.println("Received another message: " + message);
////    }
//
//}