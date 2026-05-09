package org.sim.umira.entities.CostControl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "cc_pengajuan_bk_detail")
public class PengajuanBiayaKonstruksiDetailEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_pengajuan_bk_detail;

    public BigDecimal volume_bk;

    public BigDecimal harga_total;


    public LocalDateTime tanggal_penerima;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyek")
    @JsonBackReference
    public ProyekEntity proyek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rapa")
    // @JsonBackReference
    public RapaEntity rapa;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pengajuan_bk")
    @JsonBackReference
    public PengajuanBiayaKonstruksiEntity pengajuanBk;


    public LocalDateTime created_at;

    public String created_by;

    public String invoice_nota;

    public String no_po;
}
