package org.sim.umira.entities.Cuti;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hr_cuti")
public class CutiEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_cuti;

    public String jenis_cuti;

    public LocalDate tanggal_mulai;

    public LocalDate tanggal_selesai;

    public String alasan_cuti;

    public String id_delegasi;

    public String dokumen_cuti;

    public String status_cuti;

    public String alasan_penolakan;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "id_user")
    // @JsonBackReference
    // public UserEntity user;

    // @ManyToOne
    // @JoinColumn(name = "id_employee_pengajuan")
    // public EmployeeEntity employee_pengajuan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_employee_approval")
    @JsonBackReference
    public UserEntity employee_approval;

    @ManyToOne
    @JoinColumn(name = "id_employee_pengajuan")
    public EmployeeEntity employee_pengajuan;
    

    public LocalDateTime created_at;

    public String created_by;

    // public String id_approver;
}