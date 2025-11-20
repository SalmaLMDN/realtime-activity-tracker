package com.realtime.analytics.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Table(name="events")
@Entity
public class Event {
    @Id
    private UUID id;

    private Long userId;

    private String action;

    private Instant timestamp;

    @Column(name = "raw_payload", columnDefinition="TEXT")
    private String rawPayload;

    public UUID getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getAction() {
        return action;
    }

    public String getRawPayLoad() {
        return rawPayload;
    }
}
