package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreatePengajuanBkDto {

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

    @NotNull(message = "id_user must be required")
    public List<String> id_user;

    @NotNull(message = "id_user must be required")
    public List<Integer> urutan;
    
    
}
