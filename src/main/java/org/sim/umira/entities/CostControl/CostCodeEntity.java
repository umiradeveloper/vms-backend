package org.sim.umira.entities.CostControl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "cc_cost_code")
public class CostCodeEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_cost_code;

    public String cost_code;

    public String nama;

    public String klasifikasi;

    public String spesifikasi;

    public String satuan;

    public String kode_jenis;

    public String jenis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kode_kategori")
    // @JsonIgnoreProperties({"costCodes"})
    // @JsonIgnore
    // @JsonBackReference
    public KategoriEntity kategori;


    @OneToMany(mappedBy = "costCodeRapa")
    // @JsonManagedReference
    @JsonIgnore
    public List<RapaEntity> rapa;



}
