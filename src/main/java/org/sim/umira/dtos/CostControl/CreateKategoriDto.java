package org.sim.umira.dtos.CostControl;

import java.time.LocalDate;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateKategoriDto {
    public String id_kategori;

    @NotBlank(message = "nama kategori is required")
    public String nama_kategori;

    @NotBlank(message = "kode kategori is required")
    public String kode_kategori;
}
