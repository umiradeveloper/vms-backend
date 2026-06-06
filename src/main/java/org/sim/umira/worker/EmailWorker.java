package org.sim.umira.worker;

import org.sim.umira.configs.ConfigHttpService;
import org.sim.umira.kafka.EmailEventDto;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.list.ListCommands;
import io.quarkus.scheduler.Scheduled;
import io.vertx.core.json.Json;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


@ApplicationScoped
public class EmailWorker {
    @Inject
    ConfigHttpService mailer;

    @Inject
    RedisDataSource redis;

    @Scheduled(every = "2s")
    public void processQueue() {

        // String data;
        // try {
        //     data = redis.list(String.class).lpop("email-queue");
        // } catch (Exception e) {
        //     e.printStackTrace();
        //     return;
        // }

        // if (data == null) {
        //     return;
        // }

        // EmailEventDto event = Json.decodeValue(data, EmailEventDto.class);


        // try {
        //     mailer.sendEmailWithAttach(
        //             event.to,
        //             event.message,
        //             event.subject,
        //             event.filename,
        //             event.attachment
        //     );

        // } catch (Exception e) {
        //     // requeue if failed
        //     // redis.rpush("email-queue", data);
        //     ListCommands<String, String> list = redis.list(String.class);

        //     // push ke Redis queue
        //     list.rpush("email-queue", data);
        // }
    }
}
