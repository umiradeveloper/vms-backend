package org.sim.umira.minio;

import java.time.LocalDateTime;

import org.sim.umira.entities.LogsMinioEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;


@ApplicationScoped
public class MinioLogServices {
    @Transactional
    public void save(
            String operation,
            String bucket,
            String objectName,
            Long fileSize,
            Long durationMs,
            String status,
            String message,
            String error) {

        LogsMinioEntity log = new LogsMinioEntity();

        log.createdAt = LocalDateTime.now();
        log.operation = operation;
        log.bucket = bucket;
        log.objectName = objectName;
        log.fileSize = fileSize;
        log.durationMs = durationMs;
        log.stats = status;
        log.message = message;
        log.error = error;

        log.persist();
    }
}
