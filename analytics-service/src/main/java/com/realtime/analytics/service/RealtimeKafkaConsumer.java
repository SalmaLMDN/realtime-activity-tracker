package com.realtime.analytics.service;


import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

@Service
public class RealtimeKafkaConsumer {
    private final RealtimeEventStream realtimeEventStream;

    public RealtimeKafkaConsumer(RealtimeEventStream realtimeEventStream) {
        this.realtimeEventStream = realtimeEventStream;
    }



    @KafkaListener(topics = "events_raw", groupId = "analytics-stream-group")
    public void consume(String message) {
        System.out.println("Realtime analytics received: "+message);
        realtimeEventStream.publish(message);
    }
}
