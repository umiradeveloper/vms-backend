package org.sim.umira.dtos.CostControl;

import java.math.BigInteger;

public class ReportDetailProyekDto {
    public BigInteger bk;
    public BigInteger pu;
    public BigInteger mos;
    public String bkpu;
    public ReportDetailProyekDto(BigInteger bk, BigInteger pu, BigInteger mos, String bkpu) {
        this.bk = bk;
        this.pu = pu;
        this.mos = mos;
        this.bkpu = bkpu;
    }
    

}
