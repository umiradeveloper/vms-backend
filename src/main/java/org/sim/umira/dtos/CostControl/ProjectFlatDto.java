package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;

public class ProjectFlatDto {
     public String cost_code;
    public String nama_proyek;
    public BigDecimal volume;
    public BigDecimal harga_total;

    public String getCostCode() {
        return cost_code;
    }

    public String getNamaProyek() {
        return nama_proyek;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public BigDecimal getHargaTotal() {
        return harga_total;
    }
}
