package org.sim.umira.dtos;

import java.time.LocalDate;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateAnnouncementDto {
    public String id_announcement;

    @FormParam("judul_announcement")
    public String judul_announcement;

    @FormParam("isi_announcement")
    public String isi_announcement;


    @FormParam("role_id")
    public String role_id;

    @FormParam("dokumen")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public FileUpload dokumen;

    // @NotNull(message = "file upload is required")
    // @FormParam("dokumen")
    // @PartType(MediaType.APPLICATION_OCTET_STREAM)
    // public FileUpload dokumen;

}
