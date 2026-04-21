package org.sim.umira.dtos.Reimbursement;

import java.math.BigInteger;
import java.time.LocalDate;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateReimbursementDto {

    @FormParam("id_reimbursement")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_reimbursement;

    @NotBlank(message = "jenis_reimbursement must be required")
    @FormParam("jenis_reimbursement")
    @PartType(MediaType.TEXT_PLAIN)
    public String jenis_reimbursement;

    @NotNull(message = "tanggal_reimbursement must be required")
    @FormParam("tanggal_reimbursement")
    @PartType(MediaType.TEXT_PLAIN)
    public LocalDate tanggal_reimbursement;

    @NotNull(message = "jumlah must be required")
    @FormParam("jumlah")
    @PartType(MediaType.TEXT_PLAIN)
    public BigInteger jumlah;

    @NotBlank(message = "keterangan must be required")
    @FormParam("keterangan")
    @PartType(MediaType.TEXT_PLAIN)
    public String keterangan;

    // @NotBlank(message = "id_approver must be required")
    // @FormParam("id_approver")
    // @PartType(MediaType.TEXT_PLAIN)
    // public String id_approver;

    // optional
    @FormParam("dokumen_reimbursement")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public FileUpload dokumen_reimbursement;
}