package org.sim.umira.dtos.CostControl;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public class CreateCostCodeDto {

    @NotNull(message = "kode must be required")
    public List<String> kode;

    @NotNull(message = "kategori be required")
    public List<String> kategori;

    @NotNull(message = "kode kategori be required")
    public List<String> kode_kategori;

    @NotNull(message = "klasifikasi must be required")
     public List<String> klasifikasi;


     @NotNull(message = "kode jenis be required")
     public List<String> kode_jenis;

     @NotNull(message = "spesifikasi be required")
     public List<String> spesifikasi;

     @NotNull(message = "jenis must be required")
     public List<String> jenis;

     @NotNull(message = "nama must be required")
     public List<String> nama;

     @NotNull(message = "satuan must be required")
     public List<String> satuan;
    
}
