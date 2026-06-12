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
@Table(name = "sim_asset")
public class SimAssetEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_asset;

    public String kode_asset;

    public String nama_asset;

    public String kategori;

    public String lokasi;

    public BigInteger nilai_perolehan;

    public LocalDate tanggal_perolehan;
    
    public String kondisi;

    public String status_asset;

    public String deskripsi_asset;

    public String foto_url;

    public BigInteger nilai_saat_ini;

    public Integer umur_ekonomis;



    @ManyToOne
    @JoinColumn(name = "user_pemilik")
    // @JsonBackReference
    public UserEntity user_pemilik;




}
