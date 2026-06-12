package org.sim.umira.entities.SIMAsset;

import java.math.BigInteger;
import java.time.LocalDate;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sim_asset_maintenance")
public class SimAssetMaintenanceEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_maintenance_asset;

    public LocalDate tanggal_maintenance;

    public String tipe_maintenance;

    public LocalDate tanggal_selesai;

    public BigInteger biaya;

    public String deskripsi;

    public String kondisi_setelah;

    public String status_maintenance;

    @ManyToOne
    @JoinColumn(name = "id_asset")
    // @JsonBackReference
    public SimAssetEntity asset;
}
