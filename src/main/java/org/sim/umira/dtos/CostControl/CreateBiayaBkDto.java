package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateBiayaBkDto {

    public String id_biaya_konstruksi;
    
    @NotBlank(message = "id_proyek must be required")
    public String id_proyek;

    @NotBlank(message = "id_rapa must be required")
    public String id_rapa;

    @NotBlank(message = "nama_vendor must be required")
    public String nama_vendor;

    @NotNull(message = "volume_bk must be required")
    public BigDecimal volume_bk;

    @NotNull(message = "harga_total must be required")
    public BigDecimal harga_total;

    @NotBlank(message = "nama_penerima must be required")
    public String nama_penerima;

    @NotNull(message = "harga_total must be required")
    public LocalDateTime tanggal_penerima;

}
