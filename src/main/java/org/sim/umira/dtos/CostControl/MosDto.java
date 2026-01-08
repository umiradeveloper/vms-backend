package org.sim.umira.dtos.CostControl;

import java.math.BigInteger;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MosDto {
    
    public String id_mos;

    @NotBlank(message = "nama satuan is required")
    public String id_proyek;

    @NotBlank(message = "nama satuan is required")
    public String week;

    @NotNull(message = "nama satuan is required")
    public BigInteger nominal_mos;

    

}
