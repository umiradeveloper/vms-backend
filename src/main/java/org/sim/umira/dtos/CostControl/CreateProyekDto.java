package org.sim.umira.dtos.CostControl;

import java.time.LocalDate;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateProyekDto {
    public String id_proyek;

    @NotBlank(message = "nama proyek is required")
    public String nama_proyek;

    @NotBlank(message = "kode proyek is required")
    public String kode_proyek;

    @NotBlank(message = "deskripsi proyek is required")
    public String deskripsi_proyek;

    @NotNull(message = "biaya_rap is required")
    public Integer biaya_rap;

    @NotNull(message = "biaya rab is required")
    public Integer biaya_rab;  

    // @NotBlank(message = "tanggal awal kontrak is required")
    @NotNull(message = "tanggal awal kontrak is required")
    public LocalDate tanggal_awal_kontrak;

    @NotNull(message = "tanggal akhir kontrak is required")
    public LocalDate tanggal_akhir_kontrak;
}
