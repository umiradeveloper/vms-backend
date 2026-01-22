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
    public List<@NotBlank(message = "kategori must be required") String> kategori;

    @NotEmpty(message = "kode_rap must be required")
    @Size(min = 1, message = "kode_rap must contain at least one value")
    public List<@NotBlank(message = "kode_rap must be required") String> kode_rap;

   @NotEmpty(message = "group must be required")
   @Size(min = 1, message = "group must contain at least one value")
    public List<@NotBlank(message = "group must be required") String> group;

    @NotEmpty(message = "item_pekerjaan must be required")
    @Size(min = 1, message = "item_pekerjaan must contain at least one value")
    public List<@NotBlank(message = "item_pekerjaan must be required") String> item_pekerjaan;

    @NotEmpty(message = "spesifikasi must be required")
    @Size(min = 1, message = "spesifikasi must contain at least one value")
    public List<@NotBlank(message = "spesifikasi must be required") String> spesifikasi;

    @NotEmpty(message = "satuan must be required")
    @Size(min = 1, message = "satuan must contain at least one value")
    public List<@NotBlank(message = "Satuan must be required") String> satuan;

    @NotEmpty(message = "volume must be required")
    @Size(min = 1, message = "volume must contain at least one value")
    public List<@NotNull(message = "volume must be required") BigDecimal> volume;

    @NotEmpty(message = "harga_satuan must be required")
    @Size(min = 1, message = "harga_satuan must contain at least one value")
    public List<@NotNull(message = "harga_satuan must be required") Integer> harga_satuan;

    @NotEmpty(message = "harga_total must be required")
    @Size(min = 1, message = "harga_total must contain at least one value")
    public List<@NotNull(message = "harga_total must be required") BigDecimal> harga_total;
}
