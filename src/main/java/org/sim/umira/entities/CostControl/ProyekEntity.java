package org.sim.umira.entities.CostControl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "cc_proyek")
public class ProyekEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_proyek;

    public String nama_proyek;

    public String kode_proyek;

    public String deskripsi_proyek;

    public Integer biaya_rap;

    public Integer biaya_rab;  

    // @Column(columnDefinition = "DATE", name = "tanggal_awal_kontrak", nullable = false)
    public LocalDate tanggal_awal_kontrak;

    // @Column(columnDefinition = "DATE", name = "tanggal_akhir_kontrak", nullable = false)
    public LocalDate tanggal_akhir_kontrak;
}
