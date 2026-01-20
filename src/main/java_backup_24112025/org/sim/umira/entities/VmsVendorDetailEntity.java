package org.sim.umira.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vms_vendor_detail")
public class VmsVendorDetailEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_vendor_detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vendor")
    @JsonBackReference
    public VmsVendorEntity vendor;

    // public String id_dokumen;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dokumen")
    @JsonBackReference
    public VmsVendorMstDokumenEntity dokumen;

    public String nama_dokumen;

    public String url_dokumen;
}
