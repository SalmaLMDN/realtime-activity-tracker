package com.realtime.event_processor_service.repository;

import com.realtime.event_processor_service.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

}
