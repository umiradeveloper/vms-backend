package org.sim.umira.entities.HumanResources;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hr_payroll_deduction")
public class PayrollDeductionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_payroll_deduction;

    public Integer kasbon;

    public Integer pnjaman;

    public Integer thr_paid;

    public Integer jamina_pensiun;

    public Integer bpjs_kesehatan;

    public Integer bpjs_kesehatan_family;

    public Integer jht_employee;

    public Integer pph21;

    @OneToOne
    @JoinColumn(name = "id_payroll")
    public PayrollEntity payrollMaster;


}
