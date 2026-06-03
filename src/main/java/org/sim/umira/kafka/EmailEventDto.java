package org.sim.umira.kafka;

public class EmailEventDto {
     public String to;
    public String subject;
    public String message;
    public String filename;
    public byte[] attachment;

    public EmailEventDto() {}

    public EmailEventDto(String to, String subject, String message,
                      String filename, byte[] attachment) {
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.filename = filename;
        this.attachment = attachment;
    }
}
