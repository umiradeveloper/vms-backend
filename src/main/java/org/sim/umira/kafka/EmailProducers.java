package org.sim.umira.kafka;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailProducers {
    @Inject
    @Channel("email-out")
    Emitter<EmailEventDto> emitter;

    public void send(EmailEventDto event) {
        emitter.send(event);
    }
}
