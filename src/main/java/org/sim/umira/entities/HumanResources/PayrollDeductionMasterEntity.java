package org.sim.umira.entities.HumanResources;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hr_payroll_master_deduction")
public class PayrollDeductionMasterEntity extends PanacheEntityBase{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_payroll_master_deduction;


    // public Integer bpjstk;

    // public Integer bpjskes;


    public String tarif_bpjstk;

    public String tarif_bpjskes;


    public Integer pph21;

    public Integer potongan_lainnya;

    @OneToOne
    @JoinColumn(name = "id_payroll_master")
    public PayrollMasterEntity payrollMaster;



}
