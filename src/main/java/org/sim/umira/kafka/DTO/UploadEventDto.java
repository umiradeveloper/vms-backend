package org.sim.umira.kafka.DTO;

public class UploadEventDto {
    public String url;
    public String filename;
    public String fileTemp;
    public UploadEventDto(String url, String filename, String fileTemp) {
        this.url = url;
        this.filename = filename;
        this.fileTemp = fileTemp;
    }


    

}
