package org.sim.umira.kafka;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.sim.umira.kafka.DTO.DeleteFileEventDto;
import org.sim.umira.kafka.DTO.EmailEventDto;
import org.sim.umira.kafka.DTO.UploadEventDto;

import io.vertx.core.json.Json;
import jakarta.inject.Inject;

public class KafkaProducers {

    @Inject
    LogsKafka logs;

    @Inject
    @Channel("email-out")
    Emitter<EmailEventDto> emitter;

    public void sendEmail(EmailEventDto event) {

        emitter.send(event);

        logs.save(
                "KafkaProducer",
                "SEND_EMAIL",
                "email-out",
                "SUCCESS",
                Json.encode(event),
                null);

    }

    @Inject
    @Channel("file-upload-out")
    Emitter<UploadEventDto> emitterUpload;

    public void uploadDoc(UploadEventDto upload) {
        emitterUpload.send(upload);

        logs.save(
                "KafkaProducer",
                "FILE_UPLOAD",
                "file-upload-out",
                "SUCCESS",
                Json.encode(upload),
                null);
    }

     @Inject
    @Channel("delete-file-out")
    Emitter<DeleteFileEventDto> emitterDelete;

    public void deleteDoc(DeleteFileEventDto delete) {
        emitterDelete.send(delete);

        logs.save(
                "KafkaProducer",
                "DELETE_FILE",
                "delete-file-out",
                "SUCCESS",
                Json.encode(delete),
                null);
    }

}
