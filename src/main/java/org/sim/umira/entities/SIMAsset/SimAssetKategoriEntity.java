package org.sim.umira.entities.SIMAsset;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sim_asset_kategori")
public class SimAssetKategoriEntity extends PanacheEntityBase {
    @Id
    public String kode_kategori;

    public String nama_kategori;
}
