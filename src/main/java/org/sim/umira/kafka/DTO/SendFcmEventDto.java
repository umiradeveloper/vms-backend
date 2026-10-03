package org.sim.umira.kafka.DTO;

public class SendFcmEventDto {
    public String token;
    public String nama;
    public String message;
    public String title;
    public SendFcmEventDto() {
    }
    public SendFcmEventDto(String token, String nama, String message, String title) {
        this.token = token;
        this.nama = nama;
        this.message = message;
        this.title = title;
    }

    
}
