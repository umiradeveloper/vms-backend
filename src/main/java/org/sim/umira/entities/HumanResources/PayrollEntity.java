package org.sim.umira.entities.HumanResources;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hr_payroll")
public class PayrollEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_payroll;

    public String bulan;

    public String hari_kerja;

    public String hari_izin;

    public String hari_sakit;

    public String hari_alpha;

     @ManyToOne
    @JoinColumn(name = "id_employee")
    public EmployeeEntity employee;

}
