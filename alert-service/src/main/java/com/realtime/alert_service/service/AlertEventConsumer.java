package com.realtime.alert_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtime.alert_service.dto.EventDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class AlertEventConsumer {
    private final AlertService alertService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AlertEventConsumer(AlertService alertService) {
        this.alertService = alertService;
    }

    @KafkaListener(topics="events_raw",groupId="alert-service-group")
    public void consume(String message) throws JsonProcessingException {
        EventDTO eventDTO = objectMapper.readValue(message, EventDTO.class);
        System.out.println("In the consume method of AlertEventConsumer");
        alertService.handleEvent(eventDTO);
        alertService.verifyActionCount(eventDTO);
        alertService.verifyTooManyActions(eventDTO);
        System.out.println("Alert service processed event: "+message);
    }
}
