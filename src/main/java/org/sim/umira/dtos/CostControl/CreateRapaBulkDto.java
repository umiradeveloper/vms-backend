package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CreateRapaBulkDto {

    @NotBlank(message = "kode proyek must be required")
    public String kode_proyek;

    @NotEmpty(message = "kategori must be required")
    public List<String> kategori;

    @NotEmpty(message = "kode_rap must be required")
    public List<String> kode_rap;

   @NotEmpty(message = "group must be required")
    public List<String> group;

    @NotEmpty(message = "item_pekerjaan must be required")
    public List<String> item_pekerjaan;

    @NotEmpty(message = "spesifikasi must be required")
    public List<String> spesifikasi;

    @NotEmpty(message = "satuan must be required")
    public List<String> satuan;

    @NotEmpty(message = "volume must be required")
    public List<BigDecimal> volume;

    @NotEmpty(message = "harga_satuan must be required")
    public List<Integer> harga_satuan;

    @NotEmpty(message = "harga_total must be required")
    public List<BigDecimal> harga_total;
}
