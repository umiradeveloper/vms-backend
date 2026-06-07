package org.sim.umira.kafka;

import java.time.LocalDateTime;

import org.sim.umira.entities.LogsKafkaEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class LogsKafka {
    
    
    @Transactional
    public void save(
            String service,
            String action,
            String topic,
            String status,
            String message,
            String error) {

        LogsKafkaEntity log = new LogsKafkaEntity();

        log.createdAt = LocalDateTime.now();
        log.service = service;
        log.action = action;
        log.topic = topic;
        log.status = status;
        log.message = message;
        log.error = error;

        log.persist();
    }
    
}
