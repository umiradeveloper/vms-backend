package org.sim.umira.entities.CostControl;

import java.time.LocalDateTime;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cc_pengajuan_bk_persetujuan")
public class PengajuanBiayaKonstruksiPersetujuanEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_persetujuan;

    public String nama_persetujuan;

    public String jabatan_persetujuan;

    public LocalDateTime tanggal_persetujuan;

    public String catatan_persetujuan;
}
