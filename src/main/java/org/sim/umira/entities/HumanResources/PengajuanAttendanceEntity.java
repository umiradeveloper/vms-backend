package org.sim.umira.entities.HumanResources;

import java.time.LocalDate;
import java.util.List;

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
@Table(name = "hr_pengajuan_absensi")
public class PengajuanAttendanceEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_pengajuan_absensi;

    public LocalDate tanggal;

    public String jam_masuk;

    public String jam_keluar;

    public String status_absensi;

    public String keterangan;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    public EmployeeEntity employee;

    @OrderBy("urutan ASC")
    @OneToMany(mappedBy = "pengajuanAbsensi")
    @JsonManagedReference
    public List<PengajuanApprovalAttendanceEntity> approval;

}
