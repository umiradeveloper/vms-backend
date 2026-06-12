package org.sim.umira.entities.SIMAsset;

import java.math.BigInteger;
import java.time.LocalDate;

import org.sim.umira.entities.UserEntity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sim_asset_disposal")
public class SimAssetDisposalEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_asset_disposal;

    @ManyToOne
    @JoinColumn(name = "id_asset")
    // @JsonBackReference
    public SimAssetEntity asset;
    
    public String alasan;

    public String keterangan;

    public BigInteger nilai_sisa;

    public String metode_penghapusan;

    @ManyToOne
    @JoinColumn(name = "user_pengajuan")
    // @JsonBackReference
    public UserEntity user_pengajuan;

    public LocalDate tanggal_pengajuan;

    public String status_disposal;

    public String hasil_kajian;

    @ManyToOne
    @JoinColumn(name = "user_approval")
    // @JsonBackReference
    public UserEntity user_approval;

    public LocalDate tanggal_approval;

  
}
