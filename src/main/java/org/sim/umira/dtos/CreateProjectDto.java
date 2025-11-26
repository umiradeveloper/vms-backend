package org.sim.umira.dtos;

import java.util.List;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateProjectDto {

    @NotBlank(message = "nama_project must be required")
    @FormParam("nama_project")
    @PartType(MediaType.TEXT_PLAIN)
    public String nama_project;

    @NotBlank(message = "deskripsi_project must be required")
    @FormParam("deskripsi_project")
    @PartType(MediaType.TEXT_PLAIN)
    public String deskripsi_project;

    @NotBlank(message = "dokumentasi must be required")
    @FormParam("dokumentasi")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public List<FileUpload> files; 
}
