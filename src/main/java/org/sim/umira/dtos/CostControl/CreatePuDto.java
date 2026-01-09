package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;

public class CreatePuDto {

    public String id_pu;
    
    // @NotBlank(message = "id_proyek must be required")
    @FormParam("id_proyek")
    public String id_proyek;

    @NotBlank(message = "week_pu must be required")
    @FormParam("week_pu")
    public String week_pu;

    @NotNull(message = "week_pu must be required")
    @FormParam("tanggal_awal")
    public LocalDate tanggal_awal;

    @NotNull(message = "week_pu must be required")
    @FormParam("tanggal_akhir")
    public LocalDate tanggal_akhir;

    @NotNull(message = "nominal pendapatan usaha must be required")
    @FormParam("nominal_pu")
    public Integer nominal_pu;

    @NotNull(message = "file upload must be required")
    @FormParam("dokumen_upload")
    public FileUpload dokumen_upload;

}