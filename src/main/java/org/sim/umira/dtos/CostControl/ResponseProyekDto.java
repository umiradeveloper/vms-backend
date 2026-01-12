package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;
import java.math.BigInteger;

public class ResponseProyekDto<T> {
    public BigDecimal total_bk;
    public Integer total_pu;
    public BigInteger current_mos;
    public BigInteger kerja_tambah;
    public BigInteger kerja_kurang;
    public T proyek;
    
    public ResponseProyekDto(BigDecimal total_bk, Integer total_pu, BigInteger current_mos, BigInteger kerja_tambah, BigInteger kerja_kurang, T proyek) {
        this.total_bk = total_bk;
        this.total_pu = total_pu;
        this.proyek = proyek;
        this.current_mos = current_mos;
    }
    

}
