package com.realtime.analytics.controller;


import com.realtime.analytics.service.RealtimeEventStream;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/stream")
public class RealtimeStreamController {
    private final RealtimeEventStream realtimeEventStream;

    public RealtimeStreamController(RealtimeEventStream realtimeEventStream) {
        this.realtimeEventStream = realtimeEventStream;
    }

    @GetMapping(value="/events", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvents() {
        return realtimeEventStream.subscribe();
    }
}
