package org.sim.umira.dtos;

import jakarta.validation.constraints.NotBlank;

public class CreateDokumenDto {
    @NotBlank(message = "kode_dokumen must be required")
    public String kode_dokumen;

    @NotBlank(message = "nama_dokumen must be required")
    public String nama_dokumen;
}
