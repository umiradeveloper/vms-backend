package org.sim.umira.entities.ChecklistTransaksi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.sim.umira.entities.UserEntity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "checklist_transaksi")
public class TransaksiEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_transaksi;

    public String jenis_transaksi;

    public LocalDate tanggal_pengajuan;

    public String keterangan;

    public String proyek;

    public String layak_bayar;

    public String status_pengajuan;

    public LocalDateTime last_updated;

    public String upload_bukti_pembayaran;

    public String catatan_verified;

    public String kode_transaksi;

    public LocalDateTime payment_at;
     public LocalDateTime approved_at;

    public String catatan_payment;

    public String tempo_pembayaran_after_verified;

    public LocalDate tanggal_jatuh_tempo_after_verified;

    @ManyToOne
    @JoinColumn(name = "payment_by")
    public UserEntity paymentBy;

    @ManyToOne
    @JoinColumn(name = "user_pengajuan")
    public UserEntity user_pengajuan;

     @ManyToOne
    @JoinColumn(name = "approved_by")
    public UserEntity approvedBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    public UserEntity updatedBy;



    @OneToMany(mappedBy = "transaksi", cascade = CascadeType.ALL)
    @JsonManagedReference
    public List<TransaksiDetailEntity> detailTransaksi;
}   
