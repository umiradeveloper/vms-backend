package org.sim.umira.configs;

import java.net.URI;
import java.util.Base64;

import org.sim.umira.dtos.utils.WhatsappRequestDocumentDto;
import org.sim.umira.dtos.utils.WhatsappRequestDto;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.buffer.Buffer;
import io.vertx.mutiny.ext.web.client.HttpResponse;
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


    @Inject
    ObjectMapper mapper;



    private WebClient client;

    @jakarta.annotation.PostConstruct
    void init() {
        client = WebClient.create(vertx);
    }



    public String SendWhatsappOld(String Receipent, String message){
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


    public String SendWhatsapp(String phone, String message){
        try {

            WhatsappRequestDto body = new WhatsappRequestDto();
            String no_hp = phone;

            if (no_hp != null && !no_hp.isBlank()) {

                if (no_hp.startsWith("0")) {

                    no_hp = "+62" + no_hp.substring(1);
                }
            }
            
            body.phone = no_hp;
            body.message = message;
            String jsonBody = mapper.writeValueAsString(body);
            String result = client.postAbs(config.WhatsappUrl)
                    .putHeader("X-Secret-Key", config.WhatsappSecretKey)
                    .putHeader("Content-Type", "application/json")
                    .sendBuffer(Buffer.buffer(jsonBody))
                    .onItem()
                    .transform(response -> response.bodyAsString()).await()
                    .indefinitely();

            System.out.println(result);
            return result;


        } catch (Exception e) {
            return "";
            // TODO: handle exception
        }
    }
    public Uni<String> sendWhatsappDokumenNew(String phone, String message, byte[] dokumen, String nama_dokumen){
        try {

            WhatsappRequestDocumentDto body = new WhatsappRequestDocumentDto();
            body.phone = phone;
            body.message = message;
            body.document_url ="data:application/pdf;base64," +Base64.getEncoder().encodeToString(dokumen);
            body.document_name = nama_dokumen;
             String jsonBody = mapper.writeValueAsString(body);

            return client.postAbs(config.WhatsappUrl+"-doc")
                    .putHeader("X-Secret-Key", config.WhatsappSecretKey)
                    .putHeader("Content-Type", "application/json")
                    .sendBuffer(Buffer.buffer(jsonBody))
                    .onItem()
                    .transform(response -> response.bodyAsString());

        } catch (Exception e) {
            return Uni.createFrom().failure(e);
            // TODO: handle exception
        }
    }
    

    public String sendEmail(String emailReceipent, String Message, String Subject) {
        mailer.send(
            Mail.withText(emailReceipent,
                          Subject,
                          Message)
        );
        return "Mail sent!";
    }

     public String sendEmailWithAttach(String emailRecipient,
                            String message,
                            String subject,
                            String name_pdf,
                            byte[] pdfBytes) {

        mailer.send(
            Mail.withText(emailRecipient, subject, message)
                .addAttachment(
                    name_pdf+".pdf",
                    pdfBytes,
                    "application/pdf"
                )
        );

        return "Mail sent!";
    }

    
}
