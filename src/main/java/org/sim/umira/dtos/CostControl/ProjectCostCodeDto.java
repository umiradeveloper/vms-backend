package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;

public class ProjectCostCodeDto {
    public String nama_proyek;
    public BigDecimal volume;
    // public BigDecimal volume_rapa;
    // public BigDecimal harga_total_rapa;
    public BigDecimal harga_total;
    public ProjectCostCodeDto(String nama_proyek, BigDecimal volume,
            BigDecimal harga_total) {
        this.nama_proyek = nama_proyek;
        this.volume = volume;
        // this.volume_rapa = volume_rapa;
        // this.harga_total_rapa = harga_total_rapa;
        this.harga_total = harga_total;
    }

    


    
    
}


