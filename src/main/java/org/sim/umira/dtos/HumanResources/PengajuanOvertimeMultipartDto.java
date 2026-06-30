package org.sim.umira.dtos.HumanResources;

import java.time.LocalDate;
import java.util.List;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class PengajuanOvertimeMultipartDto {
    // @FormParam("upload_bukti_bayar")
    @FormParam("id_employee")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_employee;

    @FormParam("tanggal")
    @PartType(MediaType.TEXT_PLAIN)
    public LocalDate tanggal;

    @FormParam("jam_mulai")
    @PartType(MediaType.TEXT_PLAIN)
    public String jam_mulai;

    @FormParam("jam_selesai")
    @PartType(MediaType.TEXT_PLAIN)
    public String jam_selesai;

    @FormParam("durasi")
    @PartType(MediaType.TEXT_PLAIN)
    public String durasi;

    @FormParam("alasan")
    @PartType(MediaType.TEXT_PLAIN)
    public String alasan;

    @FormParam("dokumen")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public FileUpload dokumen;

    @FormParam("id_employee_approval")
    @PartType(MediaType.TEXT_PLAIN)
    public List<String> id_employee_approval;

    @FormParam("level_approval")
    @PartType(MediaType.TEXT_PLAIN)
    public List<String> level_approval;

    @FormParam("urutan")
    @PartType(MediaType.TEXT_PLAIN)
    public List<Integer> urutan;
}
