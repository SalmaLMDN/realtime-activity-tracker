package com.realtime.alert_service.service;

import com.realtime.alert_service.dto.EventDTO;
import com.realtime.alert_service.entity.Alert;
import com.realtime.alert_service.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

class AlertServiceTest {

    private AlertRepository repo;
    private AlertService service;

    @BeforeEach
    void setUp() {
        repo = mock(AlertRepository.class);
        service = new AlertService(repo);
    }

    private EventDTO evt(long userId, String action, Instant ts) {
        EventDTO e = new EventDTO();
        e.setUserId(userId);
        e.setAction(action);
        e.setTimestamp(ts.toString());
        return e;
    }

    @Test
    void creates_alert_on_5th_consecutive_same_action() {
        long user = 42L;
        Instant t = Instant.now();

        // 4 identical actions → no alert yet
        for (int i = 0; i < 4; i++) {
            service.verifyActionCount(evt(user, "CLICK", t.plusSeconds(i)));
        }
        verify(repo, never()).save(any(Alert.class));

        // 5th identical → must alert once
        service.verifyActionCount(evt(user, "CLICK", t.plusSeconds(4)));

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(repo, times(1)).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(user);
        assertThat(captor.getValue().getReason()).containsIgnoringCase("Same action");
        assertThat(captor.getValue().getCount()).isGreaterThanOrEqualTo(5);
    }

    @Test
    void handle_event() {
        long user = 42L;
        Instant t = Instant.now();

        service.handleEvent(evt(user, "LOGIN", t));                 // 1
        service.handleEvent(evt(user, "LOGIN", t.plusSeconds(5)));  // 2
        verify(repo, never()).save(any(Alert.class));               // still none

        service.handleEvent(evt(user, "LOGIN", t.plusSeconds(10))); // 3 within 30s
        verify(repo, times(1)).save(any(Alert.class));


    }

    @Test
    void handle_event_two_logins() {
        long user = 42L;
        Instant t = Instant.now();
        service.handleEvent(evt(user, "LOGIN", t));                 // 1
        service.handleEvent(evt(user, "LOGIN", t.plusSeconds(5)));  // 2
        service.handleEvent(evt(user, "CLICK", t.plusSeconds(5)));
        verify(repo, never()).save(any(Alert.class));

        service.handleEvent(evt(user, "LOGIN", t.plusSeconds(5)));
        verify(repo, times(1)).save(any(Alert.class));


    }

    @Test
    void handle_two_many_actions() {
        long user = 42L;
        Instant t = Instant.now();

        // 4 identical actions → no alert yet
        for (int i = 0; i < 9; i++) {
            service.verifyTooManyActions(evt(user, "CLICK", t.plusSeconds(i)));
        }
        verify(repo, never()).save(any(Alert.class));
        service.verifyTooManyActions(evt(user, "CLICK", t.plusSeconds(9)));

        verify(repo, times(1)).save(any(Alert.class));

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(repo, times(1)).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(user);
        assertThat(captor.getValue().getReason()).containsIgnoringCase("Too many actions in last 30 seconds");
        assertThat(captor.getValue().getCount()).isGreaterThanOrEqualTo(10);
    }

    @Test
    void handle_too_many_critical_actions() {
        long user = 42L;
        Instant t = Instant.now();
        service.verifyTooManyCriticalActions(evt(user, "DELETE_ACCOUNT", t));
        service.verifyTooManyCriticalActions(evt(user, "DELETE_ACCOUNT", t.plusSeconds(5)));
        verify(repo, never()).save(any(Alert.class));
        service.verifyTooManyCriticalActions(evt(user, "TRANSFER_FUNDS", t.plusSeconds(5)));
        verify(repo, times(1)).save(any(Alert.class));
    }
}
