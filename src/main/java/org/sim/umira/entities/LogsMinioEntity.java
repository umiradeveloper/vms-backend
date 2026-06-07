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
@Table(name = "logs_minio")
public class LogsMinioEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_logs_minio;

    public String operation; // UPLOAD, DOWNLOAD, DELETE

    public String bucket;

    public String objectName;

    public Long fileSize;

    public Long durationMs;

    public String stats; // SUCCESS, FAILED

    public String message;

    public String error;

    public LocalDateTime createdAt;

}
