package com.realtime.event_processor_service.service;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer {

    @KafkaListener(topics="events_raw", groupId = "event-processor-group")
    public void consume(String message){
        System.out.println("Here is ur message: " +message);
    }
}


