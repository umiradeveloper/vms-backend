package org.sim.umira.dtos.ChecklistTransaksi;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateApprovalTransaksiDto {
    @FormParam("upload_bukti_bayar")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    @NotBlank(message = "upload_bukti_bayar must be required")
    public FileUpload bukti_bayar;

    
    @FormParam("layak_bayar")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "status_approval must be required")
    public String layak_bayar;

    @FormParam("status_approval")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "status_approval must be required")
    public String status_approval;

    
}
