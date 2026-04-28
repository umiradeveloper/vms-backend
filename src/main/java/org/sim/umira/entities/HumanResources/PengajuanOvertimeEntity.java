package org.sim.umira.entities.HumanResources;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "hr_pengajuan_lembur")
public class PengajuanOvertimeEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_pengajuan_lembur;

    public LocalDate tanggal;

    public String durasi;

    public String jam_mulai;

    public String jam_selesai;

    public String alasan;

    public String dokumen;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    public EmployeeEntity employee;


    @OrderBy("urutan ASC")
    @OneToMany(mappedBy = "pengajuanOvertime")
    @JsonManagedReference
    public List<PengajuanApprovalOvertimeEntity> approval;

}
