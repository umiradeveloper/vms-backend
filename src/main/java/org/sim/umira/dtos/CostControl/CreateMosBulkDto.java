package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateMosBulkDto {
    

    @NotNull(message = "id_rapa is required")
    public List<String> id_rapa;

    @NotNull(message = "volume is required")
    public List<BigDecimal> volume;

    @NotBlank(message = "id_proyek is required")
    public String id_proyek;

    @NotBlank(message = "id_pu is required")
    public String id_pu;

    

}


