package org.sim.umira.configs;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import io.vertx.mutiny.ext.web.multipart.MultipartForm;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConfigHttpService {
    
    @Inject
    Vertx vertx;

    @Inject
    ConfigService config;

    @Inject
    Mailer mailer;


    private WebClient client;

    @jakarta.annotation.PostConstruct
    void init() {
        client = WebClient.create(vertx);
    }



    public String SendWhatsapp(String Receipent, String message){
        System.out.println(config.accountWa);
        MultipartForm mf = MultipartForm.create()
        .attribute("secret", config.apiWa)
        .attribute("account", config.accountWa)
        .attribute("recipient", Receipent)
        .attribute("type", "text")
        .attribute("message", message);

       return client.postAbs("https://backup.whapify.id/api/send/whatsapp")
        .sendMultipartFormAndAwait(mf)
        .bodyAsString();
    }

    public String sendEmail(String emailReceipent, String Message, String Subject) {
        mailer.send(
            Mail.withText(emailReceipent,
                          Subject,
                          Message)
        );
        return "Mail sent!";
    }
}
