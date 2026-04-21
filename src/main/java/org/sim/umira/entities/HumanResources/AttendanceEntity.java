package org.sim.umira.entities.HumanResources;

import java.time.LocalDate;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hr_absensi")
public class AttendanceEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_absensi;

    public LocalDate tanggal;

    public String jam_masuk;

    public String jam_keluar;

    public String status;

    public String keterangan;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    public EmployeeEntity employee;
}
