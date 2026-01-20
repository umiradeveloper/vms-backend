package org.sim.umira.entities;

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
@Table(name = "vms_vendor_access_dokumen")
public class VmsVendorAccessDokumen extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_access_dokumen;

    // public String id_mst_dokumen;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mst_dokumen")
    public VmsVendorMstDokumenEntity dokumen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mst_kualifikasi")
    public VmsVendorMstKualifikasi kualifikasi;
    // public String id_mst_kualifikasi;
}
