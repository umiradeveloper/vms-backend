package org.sim.umira.dtos.CostControl;

import java.math.BigInteger;
import java.time.LocalDate;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;

public class CreateActionPlanDto {
    @FormParam("id_action_plan")
    public String id_action_plan;

    @NotBlank(message = "week must required")
    @FormParam("week")
    public String week;

    @NotNull(message = "nominal_action_plan must required")
    @FormParam("nominal_action_plan")
    public BigInteger nominal_action_plan;

    @NotNull(message = "tanggal_awal must required")
    @FormParam("tanggal_awal")
    public LocalDate tanggal_awal;

    @NotNull(message = "tanggal_akhir must required")
    @FormParam("tanggal_akhir")
    public LocalDate tanggal_akhir;

    @NotBlank(message = "id_proyek must required")
    @FormParam("id_proyek")
    public String id_proyek;

    @NotNull(message = "file_dokumen must required")
    @FormParam("file_dokumen")
    public FileUpload file_dokumen;
}
