package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreatePengajuanBkBulkDto {
    @NotBlank(message = "id_proyek must be required")
    public String id_proyek;

    @NotBlank(message = "id_proyek must be required")
    public String catatan;

    @NotNull(message = "cost_code must be required")
    public List<String> cost_code;

    @NotNull(message = "invoice_nota must be required")
    public List<String> invoice_nota;

    @NotNull(message = "no_po must be required")
    public List<String> no_po;

    @NotNull(message = "volume_bk must be required")
    public List<BigDecimal> volume_bk;

    @NotNull(message = "harga_total must be required")
    public List<BigDecimal> harga_total;

    @NotNull(message = "harga_total must be required")
    public List<LocalDate> tanggal_penerima;

    @NotNull(message = "id_user must be required")
    public List<String> id_user;

    @NotNull(message = "id_user must be required")
    public List<Integer> urutan;
}
