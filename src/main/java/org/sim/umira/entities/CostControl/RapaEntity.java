package org.sim.umira.entities.CostControl;

import java.math.BigDecimal;

import org.sim.umira.entities.VmsVendorEntity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cc_rapa")
public class RapaEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_rapa;

    public String Kategori;

    public String kode_rap;

    @Column(name = "`group`")
    public String group;

    public String item_pekerjaan;

    public String spesifikasi;

    public String satuan;

    public BigDecimal volume;

    public Integer harga_satuan;

    public BigDecimal harga_total;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "id_proyek")
    // @JsonBackReference
    // public ProyekEntity proyek;
    
}
