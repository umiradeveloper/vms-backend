package org.sim.umira.entities.CostControl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "cc_pengajuan_bk")
public class PengajuanBiayaKonstruksiEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_pengajuan_bk;
    
    public String nama_vendor;

    public BigDecimal volume_bk;

    public BigDecimal harga_total;

    public String nama_penerima;

    public LocalDateTime tanggal_penerima;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyek")
    @JsonBackReference
    public ProyekEntity proyek;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rapa")
    // @JsonBackReference
    public RapaEntity rapa;


    @OrderBy("urutan ASC")
    @OneToMany(mappedBy = "pengajuan_bk", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    public List<PengajuanBiayaKonstruksiPersetujuanEntity> pengajuan_persetujuan_bk;


}
