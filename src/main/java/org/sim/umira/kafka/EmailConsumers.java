package org.sim.umira.kafka;

import java.util.List;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.list.ListCommands;
import io.vertx.core.json.Json;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailConsumers {
     @Inject
    RedisDataSource redis;
      
    @Incoming("email-in")
    public void consume(EmailEventDto event) {

        String json = Json.encode(event);
        System.out.println(json);
        ListCommands<String, String> list = redis.list(String.class);

        // push ke Redis queue
        list.rpush("email-queue", json);
    }
}
