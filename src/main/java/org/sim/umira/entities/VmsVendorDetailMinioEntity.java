package org.sim.umira.entities;



import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Table;

@Entity
@Table(name = "vms_vendor_detail_minio_upload")
public class VmsVendorDetailMinioEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_vendor_detail;

    public String id_vendor;

    public String id_dokumen;

    public String nama_dokumen;

    public String url_dokumen;

    public Integer is_minio;


}
