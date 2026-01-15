package org.sim.umira.dtos.CostControl;

import java.math.BigInteger;
import java.time.LocalDate;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;

public class CreateScurveDto {
    @FormParam("id_scurve")
    public String id_scurve;

    @NotBlank(message = "week must required")
    @FormParam("week")
    public String week;

    @NotNull(message = "nominal_scurve must required")
    @FormParam("nominal_scurve")
    public BigInteger nominal_scurve;

    @NotNull(message = "tanggal_awal must required")
    @FormParam("tanggal_awal")
    public LocalDate tanggal_awal;

    @NotNull(message = "tanggal_akhir must required")
    @FormParam("tanggal_akhir")
    public LocalDate tanggal_akhir;

    @NotBlank(message = "nominal_scurve must required")
    @FormParam("id_proyek")
    public String id_proyek;

    @NotNull(message = "file_dokumen must required")
    @FormParam("file_dokumen")
    public FileUpload file_dokumen;
}
