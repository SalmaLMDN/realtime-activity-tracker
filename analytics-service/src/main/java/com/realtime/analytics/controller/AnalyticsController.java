package com.realtime.analytics.controller;
import org.springframework.kafka.annotation.KafkaListener;


import com.realtime.analytics.projection.ActionCount;
import com.realtime.analytics.repository.EventRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private final EventRepository eventRepository;
    public AnalyticsController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    @GetMapping("/actions")
    public List<ActionCount> getActionsCount() {
        return eventRepository.countEventsByAction();
    }

}
