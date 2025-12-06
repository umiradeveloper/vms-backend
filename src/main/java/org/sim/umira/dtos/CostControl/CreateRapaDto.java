package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateRapaDto {

    
    public String id_rapa;

    // @NotBlank(message = "kode proyek must be required")
    public String kode_proyek;

    @NotBlank(message = "kategori must be required")
    public String kategori;

    @NotBlank(message = "kode rap must be required")
    public String kode_rap;

    @NotBlank(message = "group must be required")
    public String group;

    @NotBlank(message = "item pekerjaan must be required")
    public String item_pekerjaan;

    @NotBlank(message = "spesifikasi must be required")
    public String spesifikasi;

    @NotBlank(message = "satuan must be required")
    public String satuan;

    @NotNull(message = "volume must be required")
    public BigDecimal volume;

    @NotNull(message = "harga satuan must be required")
    public Integer harga_satuan;

    @NotNull(message = "harga total must be required")
    public BigDecimal harga_total;
}
