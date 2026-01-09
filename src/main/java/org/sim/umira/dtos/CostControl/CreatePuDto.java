package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreatePuDto {

    @FormParam("id_pu")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_pu;
    
    // @NotBlank(message = "id_proyek must be required")
    @FormParam("id_proyek")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_proyek;

    @NotBlank(message = "week_pu must be required")
    @FormParam("week_pu")
    @PartType(MediaType.TEXT_PLAIN)
    public String week_pu;

    @NotNull(message = "week_pu must be required")
    @FormParam("tanggal_awal")
    @PartType(MediaType.TEXT_PLAIN)
    public LocalDate tanggal_awal;

    @NotNull(message = "week_pu must be required")
    @FormParam("tanggal_akhir")
    @PartType(MediaType.TEXT_PLAIN)
    public LocalDate tanggal_akhir;

    @NotNull(message = "nominal pendapatan usaha must be required")
    @FormParam("nominal_pu")
    @PartType(MediaType.TEXT_PLAIN)
    public Integer nominal_pu;

    // @NotNull(message = "file upload must be required")
    @FormParam("dokumen_upload")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public FileUpload dokumen_upload;

}