package org.sim.umira.dtos.CostControl;

import java.math.BigInteger;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;

public class MosDto {
    
    @FormParam("id_mos")
    public String id_mos;

    @NotBlank(message = "id_proyek is required")
    @FormParam("id_proyek")
    public String id_proyek;

    @NotBlank(message = "week is required")
    @FormParam("week")
    public String week;

    @NotNull(message = "nominal_mos is required")
    @FormParam("nominal_mos")
    public BigInteger nominal_mos;

    // @NotNull(message = "dokumen_upload is required")
    @FormParam("dokumen_upload")
    public FileUpload dokumen_upload;

    

}
