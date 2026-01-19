package org.sim.umira.entities.CostControl;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "cc_pengajuan_bk")
public class PengajuanBiayaKonstruksiEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_pengajuan_bk;
    
    public String nama_vendor;

    public BigDecimal volume_bk;

    public BigDecimal harga_total;

    public String nama_penerima;

    public LocalDate tanggal_penerima;


}
