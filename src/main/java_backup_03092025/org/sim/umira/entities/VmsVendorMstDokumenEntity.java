package org.sim.umira.entities;





import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;

@Entity
@Table(name = "vms_vendor_mst_dokumen")
public class VmsVendorMstDokumenEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_mst_dokumen;

    public String kode_dokumen;

    public String nama_dokumen;

    // @OneToOne(mappedBy = "dokumen", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // @JsonManagedReference
    // public List<VmsVendorDetailEntity> vendorDetail;
}
