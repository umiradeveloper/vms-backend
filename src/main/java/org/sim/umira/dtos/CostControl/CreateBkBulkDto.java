package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CreateBkBulkDto {

    public String id_proyek;
    
    public List<String> cost_code;

    public List<BigDecimal> volume_bk;

    public List<BigDecimal> harga_total;

    public List<LocalDate> tanggal;

    public List<String> no_po;

    public List<String> invoice_nota;


    
}
