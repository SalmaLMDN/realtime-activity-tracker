package com.realtime.event_processor_service.service;


import com.realtime.event_processor_service.dto.EventDTO;
import com.realtime.event_processor_service.entity.Event;
import com.realtime.event_processor_service.mapper.EventMapper;
import com.realtime.event_processor_service.repository.EventRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer {
    private final EventRepository eventRepository;

    public EventConsumer(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @KafkaListener(topics="events_raw", groupId = "event-processor-group")
    public void consume(String message){
        System.out.println("Here is ur message: " +message);
        try{
            EventDTO eventDTO = EventMapper.jsonToDTO(message);
            Event event = EventMapper.toEntity(eventDTO,message);
            eventRepository.save(event);
            System.out.println("Ur event has ben logged successfully: " +event.toString());
        } catch (Exception e) {
            System.err.println("Error while sending event: "+message);
            e.printStackTrace();
        }
    }
}


