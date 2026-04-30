package org.sim.umira.entities.HumanResources;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hr_payroll_master")
public class PayrollMasterEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_payroll_master;

    public Integer gaji_pokok;

    public Integer tunjangan_transport;


    public Integer tunjangan_makan;

    public Integer tunjangan_lembur;

    public Integer tunjangan_lainnya;

    public Integer bpjs_kesehatan;

    public Integer bpjs_ketenagakerjaan;

    @OneToOne
    @JoinColumn(name = "id_employee")
    public EmployeeEntity employee;


}
