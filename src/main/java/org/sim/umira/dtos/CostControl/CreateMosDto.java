package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

public class CreateMosDto {
     public String id_mos;

    @NotBlank(message = "id_rapa is required")
    public String id_rapa;

    @NotBlank(message = "volume is required")
    public BigDecimal volume;

    @NotBlank(message = "id_proyek is required")
    public String id_proyek;
}
