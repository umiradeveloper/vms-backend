package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ResponseApprovePengajuanBkDto {
    public String id_pengajuan_bk;
    
    public String nama_vendor;

    public BigDecimal volume_bk;

    public BigDecimal harga_total;

    public String nama_penerima;

    public LocalDate tanggal_penerima;
}
