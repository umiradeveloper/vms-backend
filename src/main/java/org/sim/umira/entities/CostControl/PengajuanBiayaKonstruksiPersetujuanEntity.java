package org.sim.umira.entities.CostControl;

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
@Table(name = "cc_pengajuan_bk_persetujuan")
public class PengajuanBiayaKonstruksiPersetujuanEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_persetujuan;

    // public String id_pengajuan_bk;

    public String id_user;

    public String nama_persetujuan;

    public String status_approver;

    public String jabatan_persetujuan;

    public LocalDateTime tanggal_persetujuan;

    public String catatan_persetujuan;

    public Integer urutan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pengajuan_bk")
    @JsonBackReference
    public PengajuanBiayaKonstruksiEntity pengajuan_bk;
}
