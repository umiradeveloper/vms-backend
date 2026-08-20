package org.sim.umira.entities.HumanResources;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hr_payroll")
public class PayrollEntity extends PanacheEntityBase{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_payroll;

    public String bulan;

    public String tahun;

    public String hari_kerja;

    public String hari_izin;

    public String hari_sakit;

    public String hari_alpha;

    public Integer gaji_pokok;

    public Integer tunjangan_transport;

    public Integer tunjangan_jabatan;

    public Integer tunjangan_makan;

    public Integer tunjangan_lembur;

    public Integer tunjangan_lainnya;

    public Integer bpjs_kesehatan;

    public Integer bpjs_ketenagakerjaan;

     @ManyToOne
    @JoinColumn(name = "id_employee")
    public EmployeeEntity employee;

}
