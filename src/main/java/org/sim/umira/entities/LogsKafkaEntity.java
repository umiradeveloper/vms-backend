package org.sim.umira.entities;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "logs_kafka")
public class LogsKafkaEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_kafka_logs;

    public String service;

    public String action;

    public String topic;

    public String status;

    public String message;

    public String error;

    public LocalDateTime createdAt;
}
