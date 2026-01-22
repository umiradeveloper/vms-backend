package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateRapaBulkDto {

    @NotBlank(message = "kode proyek must be required")
    public String kode_proyek;

    @NotEmpty(message = "kategori must be required")
    @Size(min = 1, message = "kategori must contain at least one value")
    public List<String> kategori;

    @NotEmpty(message = "kode_rap must be required")
    @Size(min = 1, message = "kode_rap must contain at least one value")
    public List<String> kode_rap;

   @NotEmpty(message = "group must be required")
   @Size(min = 1, message = "group must contain at least one value")
    public List<String> group;

    @NotEmpty(message = "item_pekerjaan must be required")
    @Size(min = 1, message = "item_pekerjaan must contain at least one value")
    public List<String> item_pekerjaan;

    @NotEmpty(message = "spesifikasi must be required")
    @Size(min = 1, message = "spesifikasi must contain at least one value")
    public List<String> spesifikasi;

    @NotEmpty(message = "satuan must be required")
    @Size(min = 1, message = "satuan must contain at least one value")
    public List<String> satuan;

    @NotEmpty(message = "volume must be required")
    @Size(min = 1, message = "volume must contain at least one value")
    public List<BigDecimal> volume;

    @NotEmpty(message = "harga_satuan must be required")
    @Size(min = 1, message = "harga_satuan must contain at least one value")
    public List<Integer> harga_satuan;

    @NotEmpty(message = "harga_total must be required")
    @Size(min = 1, message = "harga_total must contain at least one value")
    public List<BigDecimal> harga_total;
}
