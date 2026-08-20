package org.sim.umira.entities.HumanResources;

import java.time.LocalDateTime;

import org.sim.umira.entities.UserEntity;

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
@Table(name = "hr_pinjaman_detail")
public class LoanDetailEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_detail_pinjaman;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pinjaman")
    @JsonBackReference
    public LoanEntity idPinjaman;

    public Integer cicilan_ke;

    public Integer nominal_cicilan;

    public String status;

    public String bulan;

    public String tahun;

    public LocalDateTime created_at;

    @ManyToOne
    @JoinColumn(name = "created_by")
    public UserEntity createdBy;


}
