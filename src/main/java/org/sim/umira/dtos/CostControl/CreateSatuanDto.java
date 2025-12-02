package org.sim.umira.dtos.CostControl;

import java.time.LocalDate;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateSatuanDto {
    public String id_satuan;

    @NotBlank(message = "nama satuan is required")
    public String nama_satuan;

    @NotBlank(message = "kode satuan is required")
    public String kode_satuan;
}
