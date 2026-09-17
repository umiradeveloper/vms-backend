package org.sim.umira.dtos.HumanResources;

import java.time.LocalDate;
import java.util.List;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class PengajuanOvertimeMultipartDto {
    // @FormParam("upload_bukti_bayar")

    // @NotBlank(message = "employee wajib diisi")
    @FormParam("id_employee")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_employee;

    @NotNull(message = "wajib diisi")
    @FormParam("tanggal")
    @PartType(MediaType.TEXT_PLAIN)
    public LocalDate tanggal;

    @NotBlank(message = "wajib diisi")
    @FormParam("jam_mulai")
    @PartType(MediaType.TEXT_PLAIN)
    public String jam_mulai;

    @NotBlank(message = "wajib diisi")
    @FormParam("jam_selesai")
    @PartType(MediaType.TEXT_PLAIN)
    public String jam_selesai;

    @NotBlank(message = "wajib diisi")
    @FormParam("durasi")
    @PartType(MediaType.TEXT_PLAIN)
    public String durasi;

    @NotBlank(message = "wajib diisi")
    @FormParam("alasan")
    @PartType(MediaType.TEXT_PLAIN)
    public String alasan;

    @NotNull(message = "wajib diupload")
    @FormParam("dokumen")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public FileUpload dokumen;

    @NotEmpty(message = "wajib diisi")
    @FormParam("id_employee_approval")
    @PartType(MediaType.TEXT_PLAIN)
    public List<String> id_employee_approval;

    @NotEmpty(message = "wajib diisi")
    @FormParam("level_approval")
    @PartType(MediaType.TEXT_PLAIN)
    public List<String> level_approval;

    @NotEmpty(message = "wajib diisi")
    @FormParam("urutan")
    @PartType(MediaType.TEXT_PLAIN)
    public List<Integer> urutan;
}
