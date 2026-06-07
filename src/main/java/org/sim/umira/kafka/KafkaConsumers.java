package org.sim.umira.kafka;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.sim.umira.kafka.DTO.DeleteFileEventDto;
import org.sim.umira.kafka.DTO.EmailEventDto;
import org.sim.umira.kafka.DTO.UploadEventDto;
import org.sim.umira.minio.MinioServices;
import org.sim.umira.services.SuperappsExecutor;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.list.ListCommands;
import io.smallrye.reactive.messaging.annotations.Blocking;
import io.vertx.core.json.Json;
import jakarta.inject.Inject;

public class KafkaConsumers {

    @Inject
    RedisDataSource redis;

    @Inject
    MinioServices minio;

    @Inject
    @SuperappsExecutor
    ExecutorService executor;

    @Inject
    LogsKafka logs;

    @Incoming("email-in")
    public void consumeEmail(EmailEventDto event) {
        try {
            String json = Json.encode(event);
            // System.out.println(json);
            ListCommands<String, String> list = redis.list(String.class);

            // push ke Redis queue
            list.rpush("email-queue", json);

            logs.save(
                    "KafkaConsumer",
                    "SEND_EMAIL",
                    "email-in",
                    "SUCCESS",
                    Json.encode(event),
                    null);

        } catch (Exception e) {

            logs.save(
                    "KafkaConsumer",
                    "SEND_EMAIL",
                    "email-in",
                    "FAILED",
                    Json.encode(event),
                    e.getMessage());
            // TODO: handle exception
        }

    }

    @Incoming("file-upload-in")
    @Blocking
    public void consumeUpload(UploadEventDto upload) {
        try {
            logs.save(
                    "KafkaConsumer",
                    "FILE_UPLOAD",
                    "file-upload-in",
                    "SUCCESS",
                    Json.encode(upload),
                    null);


                long start = System.currentTimeMillis();

                try {

                    Path path = Path.of(upload.fileTemp);

                    if (!Files.exists(path)) {
                        System.out.printf(
                                "[UPLOAD] File not found: %s%n",
                                upload.fileTemp);
                        return;
                    }

                    System.out.printf(
                            "[UPLOAD] Start upload file=%s size=%d%n",
                            upload.filename,
                            Files.size(path));

                    try (InputStream stream = Files.newInputStream(path)) {

                        minio.uploadFile(
                                upload.url,
                                upload.filename,
                                stream,
                                Files.size(path));
                    }

                    System.out.printf(
                            "[UPLOAD] Success file=%s duration=%d ms%n",
                            upload.filename,
                            System.currentTimeMillis() - start);

                    Files.deleteIfExists(path);

                } catch (Exception e) {

                    System.err.printf(
                            "[UPLOAD] Failed file=%s error=%s%n",
                            upload.filename,
                            e.getMessage());

                    e.printStackTrace();
                }

        } catch (Exception e) {

            logs.save(
                    "KafkaConsumer",
                    "FILE_UPLOAD",
                    "file-upload-in",
                    "FAILED",
                    Json.encode(upload),
                    e.getMessage());
            // TODO: handle exception
        }

    }

  

    @Incoming("delete-file-in")
    public void consumeDeleteFile(DeleteFileEventDto upload) {
        try {
            logs.save(
                    "KafkaConsumer",
                    "DELETE_FILE",
                    "delete-file-in",
                    "SUCCESS",
                    Json.encode(upload),
                    null);
            minio.deleteFile(upload.objectName);
        } catch (Exception e) {
            logs.save(
                    "KafkaConsumer",
                    "DELETE_FILE",
                    "delete-file-in",
                    "FAILED",
                    Json.encode(upload),
                    e.getMessage());
            // TODO: handle exception
        }
    }
}
