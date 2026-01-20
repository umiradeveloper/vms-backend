package org.sim.umira.dtos;

import java.util.List;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;


public class CreateVendorUploadBulkDto {

    @FormParam("id_vendor")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_vendor;

    @FormParam("id_dokumen")
    @PartType(MediaType.TEXT_PLAIN)
    public List<String> id_dokumen;

    @FormParam("files")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public List<FileUpload> files; 
    
}

