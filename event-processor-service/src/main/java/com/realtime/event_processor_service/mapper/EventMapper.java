package com.realtime.event_processor_service.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.realtime.event_processor_service.dto.EventDTO;
import com.realtime.event_processor_service.entity.Event;

import java.time.Instant;

public class EventMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static EventDTO jsonToDTO(String json)  throws Exception{
        return  objectMapper.readValue(json, EventDTO.class);
    }

    public static Event toEntity(EventDTO eventDTO, String rawJson) throws Exception{
        Event event = new Event();
        event.setAction(eventDTO.getAction());
        event.setTimestamp(Instant.parse(eventDTO.getTimestamp()));
        event.setUserId(eventDTO.getUserId());
        event.setRawPayload(rawJson);
        return event;
    }

}

