package org.sim.umira.dtos.Cuti;

import java.time.LocalDate;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateCutiDto {

    @FormParam("id_cuti")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_cuti;

// @NotBlank(message = "id_user must be required")
// @FormParam("id_user")
// @PartType(MediaType.TEXT_PLAIN)
// public String id_user;


    @NotBlank(message = "jenis_cuti Harus Di Isi")
    @FormParam("jenis_cuti")
    @PartType(MediaType.TEXT_PLAIN)
    public String jenis_cuti;

    @FormParam("kode_cuti")
    @PartType(MediaType.TEXT_PLAIN)
    public String kode_cuti;

    @NotNull(message = "tanggal_mulai Harus Di Isi")
    @FormParam("tanggal_mulai")
    @PartType(MediaType.TEXT_PLAIN)
    public LocalDate tanggal_mulai;

    @NotNull(message = "tanggal_selesai Harus Di Isi")
    @FormParam("tanggal_selesai")
    @PartType(MediaType.TEXT_PLAIN)
    public LocalDate tanggal_selesai;

    @NotBlank(message = "alasan_cuti Harus Di Isi")
    @FormParam("alasan_cuti")
    @PartType(MediaType.TEXT_PLAIN)
    public String alasan_cuti;

    @NotBlank(message = "tanggal_selesai Harus Di Isi")
    @FormParam("id_delegasi")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_delegasi;

    @NotNull(message = "Document Harus Di Isi")
    @FormParam("dokumen_upload")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public FileUpload dokumen_upload;

    @NotBlank(message = "Checker Harus Di Isi")
    @FormParam("id_employee_approval")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_employee_approval;

    @NotBlank(message = "Signer Harus Di Isi")
    @FormParam("id_employee_manager")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_employee_manager;

}