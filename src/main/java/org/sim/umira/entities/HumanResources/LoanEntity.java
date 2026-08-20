package org.sim.umira.entities.HumanResources;

import java.time.LocalDateTime;
import java.util.List;

import org.sim.umira.entities.UserEntity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "hr_pinjaman")
public class LoanEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_pinjaman;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    public EmployeeEntity employee;

    public Integer total_pinjaman;

    public Integer jumlah_cicilan;

    public String bulan_tahun_awal;

    public String bulan_tahun_akhir;

    public String status_paid;

    public LocalDateTime created_at;

    @ManyToOne
    @JoinColumn(name = "created_by")
    public UserEntity createdBy;

    @OrderBy("cicilan_ke ASC")
    @OneToMany(
        mappedBy = "idPinjaman",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @JsonManagedReference
    public List<LoanDetailEntity> loanDetail;


    
}
