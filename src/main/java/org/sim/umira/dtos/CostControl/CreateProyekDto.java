package org.sim.umira.dtos.CostControl;

import java.math.BigInteger;
import java.time.LocalDate;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateProyekDto {

    @NotBlank(message = "nama proyek is required")
    public String nama_proyek;

    @NotBlank(message = "kode proyek is required")
    public String kode_proyek;

    @NotBlank(message = "deskripsi proyek is required")
    public String deskripsi_proyek;

    // @NotBlank(message = "Bk PU Awal is required")
    // public String bk_pu_awal;

    @NotNull(message = "biaya_rap is required")
    public BigInteger biaya_rap;

    @NotNull(message = "biaya rab is required")
    public BigInteger biaya_rab;  

    // @NotNull(message = "kerja_tambah is required")
    public BigInteger kerja_tambah;

    // @NotNull(message = "kerja_kurang is required")
    public BigInteger kerja_kurang;  

    @NotNull(message = "tanggal awal kontrak is required")
    public LocalDate tanggal_awal_kontrak;

    @NotNull(message = "tanggal akhir kontrak is required")
    public LocalDate tanggal_akhir_kontrak;
}
