package org.sim.umira.entities.ChecklistTransaksi;



import java.time.LocalDateTime;

import org.sim.umira.entities.UserEntity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "checklist_detail_transaksi")
public class TransaksiDetailEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_detail_transaksi;

    public String pertanyaan;

    public String jawaban;

    public Integer checklist;

    public String catatan;
    
    public Integer nilai;

    public LocalDateTime verified_at;

    @ManyToOne
    @JoinColumn(name = "verified_by")
    public UserEntity user_verified;

    

    @ManyToOne
    @JoinColumn(name = "id_transaksi")
    @JsonBackReference
    public TransaksiEntity transaksi;

}
