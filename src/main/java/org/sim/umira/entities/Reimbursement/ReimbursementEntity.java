package org.sim.umira.entities.Reimbursement;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
import org.sim.umira.entities.UserEntity;

@Entity
@Table(name = "hr_reimbursement")
public class ReimbursementEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_reimbursement;

    public String jenis_reimbursement;

    public LocalDate tanggal_reimbursement;

    public BigInteger jumlah;

    public String keterangan;

    // public String id_approver;

    public String dokumen_reimbursement;

    public String status_reimbursement;

    public String alasan_penolakan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    @JsonBackReference
    public UserEntity user;

    public LocalDateTime created_at;

    public String created_by;
}