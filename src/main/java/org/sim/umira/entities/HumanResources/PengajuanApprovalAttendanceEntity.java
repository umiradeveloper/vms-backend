package org.sim.umira.entities.HumanResources;

import java.time.LocalDateTime;

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
@Table(name = "hr_pengajuan_approval_absensi")
public class PengajuanApprovalAttendanceEntity extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_pengajuan_approval;

    public LocalDateTime tanggal_approval;

    public String status_approval;

    public String keterangan;

    public String level_approval;

    public Integer urutan;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    public EmployeeEntity employee;

    @ManyToOne
    @JoinColumn(name = "id_pengajuan_absensi")
    @JsonBackReference
    public PengajuanAttendanceEntity pengajuanAbsensi;
}
