package org.sim.umira.entities.CostControl;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    public BigInteger biaya_rap;

    public BigInteger biaya_rab;  

    public BigInteger kerja_tambah;

    public BigInteger kerja_kurang; 
    
    public String bk_pu_awal;

    // @Column(columnDefinition = "DATE", name = "tanggal_awal_kontrak", nullable = false)
    public LocalDate tanggal_awal_kontrak;

    // @Column(columnDefinition = "DATE", name = "tanggal_akhir_kontrak", nullable = false)
    public LocalDate tanggal_akhir_kontrak;

    @OneToMany(mappedBy = "proyek", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    public List<RapaEntity> rapa;
}
