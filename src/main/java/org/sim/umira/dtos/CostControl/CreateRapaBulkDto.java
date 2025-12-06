package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;

public class CreateRapaBulkDto {

    @NotBlank(message = "kode proyek must be required")
    public String kode_proyek;

    
    public List<String> kategori;

    public List<String> kode_rap;

   
    public List<String> group;

    public List<String> item_pekerjaan;

    
    public List<String> spesifikasi;

  
    public List<String> satuan;


    public List<BigDecimal> volume;


    public List<Integer> harga_satuan;

  
    public List<BigDecimal> harga_total;
}
