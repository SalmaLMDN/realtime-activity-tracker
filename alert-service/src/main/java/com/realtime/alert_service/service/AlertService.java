package com.realtime.alert_service.service;


import com.realtime.alert_service.config.AlertConstants;
import com.realtime.alert_service.dto.EventDTO;
import com.realtime.alert_service.entity.Alert;
import com.realtime.alert_service.model.LastAction;
import com.realtime.alert_service.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

@Service
public class AlertService {

    private final AlertRepository alertRepository;


    Map<Long, Deque<Instant>> loginEvents = new HashMap<>();
    Map<Long, Deque<Instant>> lastEvents = new HashMap<>();
    Map<Long, Deque<Instant>> criticalEvents = new HashMap<>();
    Map<Long, Deque<Instant>> actionEvents = new HashMap<>();
    Map<Long, LastAction> lastActions = new HashMap<>();



    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }


    public void handleEvent(EventDTO event) {
        if(!"LOGIN".equalsIgnoreCase(event.getAction())) {
            return;
        }
        Long userId = event.getUserId();
        if(userId==null) {
            System.out.println("User id missing!!!");
            return;
        }
        Instant ts = Instant.parse(event.getTimestamp());
        Instant cutoff = ts.minus(AlertConstants.WINDOW);
        Deque<Instant> deque = loginEvents.computeIfAbsent(userId, id-> new ArrayDeque<>());
        while(!deque.isEmpty() && deque.peekFirst().isBefore(cutoff)) {
            deque.pollFirst();
        }
        deque.addLast(ts);
        if(deque.size()>=AlertConstants.LOGIN_THRESHOLD) {
            Alert alert = new Alert();
            alert.setUserId(userId);
            alert.setTimestamp(ts);
            alert.setReason("Too many login attempts");
            alert.setCount(deque.size());
            alertRepository.save(alert);

            deque.clear();
        }
    }

    public void verifyActionCount(EventDTO event) {
        Long userId = event.getUserId();
        if(userId==null) {
            System.out.println("User id missing!!!");
            return;
        }
        String action = event.getAction();


        LastAction lastAction = lastActions.getOrDefault(userId, new LastAction(action,0));
        if(lastAction.getNameAction().equalsIgnoreCase(event.getAction())) lastAction.incrementCountAction();
        else {
            lastAction.setNameAction(event.getAction());
            lastAction.setCountAction(1);
        }

        if (lastAction.getCountAction()>=5){
            Alert alert = new Alert();
            alert.setUserId(userId);
            alert.setTimestamp(Instant.parse(event.getTimestamp()));
            alert.setReason("Same action repeated over 5 times");
            alert.setCount(lastAction.getCountAction());
            alertRepository.save(alert);

            lastAction.setCountAction(0);


        }

        lastActions.put(userId, lastAction);
    }

    public void verifyTooManyActions(EventDTO event) {
        Long userId = event.getUserId();
        if(userId==null) {
            System.out.println("User id missing!!!");
        }

        Instant ts = Instant.parse(event.getTimestamp());
        Instant  cutoff = ts.minus(AlertConstants.WINDOW);
        Deque<Instant> deque = lastEvents.computeIfAbsent(userId, id-> new ArrayDeque<>());
        while(!deque.isEmpty() && deque.peekFirst().isBefore(cutoff)) {
            deque.pollFirst();
        }
        deque.addLast(ts);
        if(deque.size()>=AlertConstants.ACTIONS_THRESHOLD) {
            Alert alert = new Alert();
            alert.setUserId(userId);
            alert.setTimestamp(ts);
            alert.setReason("Too many actions in last 30 seconds");
            alert.setCount(deque.size());
            alertRepository.save(alert);

            deque.clear();
        }

    }

    public void verifyTooManyCriticalActions(EventDTO event) {
        Long userId = event.getUserId();
        if(userId==null) {
            System.out.println("User id missing!!!");
            return;
        }
        Instant ts = Instant.parse(event.getTimestamp());
        String action = event.getAction();
        if(!AlertConstants.CRITICAL_ACTIONS.contains(action)) {
            return;
        }
        Deque<Instant> deque =  criticalEvents.computeIfAbsent(userId, id-> new ArrayDeque<>());
        Instant cutoff = ts.minus(AlertConstants.CRITICAL_WINDOW);
        while(!deque.isEmpty() && deque.peekFirst().isBefore(cutoff)) {
            deque.pollFirst();
        }
        deque.addLast(ts);
        if(deque.size()>=AlertConstants.CRITICAL_ACTION_THRESHOLD) {
            Alert alert = new Alert();
            alert.setUserId(userId);
            alert.setTimestamp(ts);
            alert.setReason("Too many critical actions in the last 60 seconds");
            alert.setCount(deque.size());
            alertRepository.save(alert);

            deque.clear();
        }
    }

}
