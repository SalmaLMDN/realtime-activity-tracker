package com.realtime.analytics.repository;

import com.realtime.analytics.entity.Event;
import com.realtime.analytics.projection.ActionCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    @Query("SELECT e.action AS action, COUNT(e) AS count FROM Event e GROUP BY e.action")
    List<ActionCount> countEventsByAction();
}
