package org.sim.umira.entities.SIMAsset;

import java.time.LocalDateTime;

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
@Table(name = "sim_asset_mutasi")
public class SimAssetMutasiEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_mutasi_asset;

    public LocalDateTime tanggal_mutasi;

    public LocalDateTime tanggal_penerimaan;

    public String alasan_mutasi;

    public String lokasi_asal;

    public String lokasi_tujuan;

    public String dokumen_referensi;

    public String status_mutasi;

    @ManyToOne
    @JoinColumn(name = "id_asset")
    // @JsonBackReference
    public SimAssetEntity asset;


    @ManyToOne
    @JoinColumn(name = "pic_sebelum")
    // @JsonBackReference
    public UserEntity pic_sebelum;

    @ManyToOne
    @JoinColumn(name = "pic_tujuan")
    // @JsonBackReference
    public UserEntity pic_tujuan;
}
