package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreatePuDto {

    public String id_pu;
    
    @NotBlank(message = "id_proyek must be required")
    public String id_proyek;

    @NotNull(message = "nominal pendapatan usaha must be required")
    public Integer nominal_pu;

}