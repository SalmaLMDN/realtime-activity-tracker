package com.realtime.gateway_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtime.gateway_service.service.EventProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventProducer eventProducer;

    public EventController(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    @PostMapping
    public Map<String, String> receiveEvent(@RequestBody Map<String, String> eventJson) throws JsonProcessingException {
        String json = new ObjectMapper().writeValueAsString(eventJson);
        eventProducer.sendEvent(json);
        return Map.of("status", "sent","message", json);
    }
}
