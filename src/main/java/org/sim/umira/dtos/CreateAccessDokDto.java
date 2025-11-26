package org.sim.umira.dtos;

import jakarta.validation.constraints.NotBlank;

public class CreateAccessDokDto {
    @NotBlank(message = "kode_dokumen must be required")
    public String kode_dokumen;

    @NotBlank(message = "id_mst_kualifikasi must be required")
    public String id_mst_kualifikasi;
}
