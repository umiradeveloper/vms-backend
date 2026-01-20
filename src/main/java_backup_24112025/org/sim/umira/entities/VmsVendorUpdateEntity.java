package org.sim.umira.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "vms_vendor_update")
public class VmsVendorUpdateEntity extends PanacheEntityBase  {
     @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_vendor_update;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vendor")
    public VmsVendorEntity vendor;

    public String id_pengajuan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    public UserEntity user;

    public String nama_perusahaan;

    public LocalDateTime tanggal_pengajuan;
    // public String id_kualifikasi_usaha;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_kualifikasi_usaha")
    public VmsVendorMstKualifikasi kualifikasi_usaha;

    public String klasifikasi_usaha;

    public String alamat_perusahaan;

    public String kategori;

    public String spesialisasi;

    public String nama_pic;

    public String email_pic;

    public String no_hp_pic;

    public String nama_direktur;

    public String email_direktur;

    public String no_hp_direktur;

    public String website;

    public Integer isApproval;
    
    public String approvedBy;

    public LocalDateTime approvedAt;

    public String no_skt;

    public LocalDate tanggal_awal_skt;

    public LocalDate tanggal_akhir_skt;
    
    public String catatan;

    public String alasan_update;
}
