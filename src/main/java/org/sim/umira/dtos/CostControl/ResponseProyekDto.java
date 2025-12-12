package org.sim.umira.dtos.CostControl;

import java.math.BigDecimal;

public class ResponseProyekDto<T> {
    public BigDecimal total_bk;
    public Integer total_pu;
    public T proyek;
    
    public ResponseProyekDto(BigDecimal total_bk, Integer total_pu, T proyek) {
        this.total_bk = total_bk;
        this.total_pu = total_pu;
        this.proyek = proyek;
    }
    

}
