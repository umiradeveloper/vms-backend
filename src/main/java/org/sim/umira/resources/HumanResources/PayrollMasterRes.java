package org.sim.umira.resources.HumanResources;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.HumanResources.PayrollMasterDto;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.HumanResources.PayrollDeductionMasterEntity;
import org.sim.umira.entities.HumanResources.PayrollMasterEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/HR-Payroll")
@Secured
public class PayrollMasterRes {
    @POST
    @Path("/create-payroll-master")
    @Transactional
    public Response createPayrollMaster(@RequestBody PayrollMasterDto payroll){
        EmployeeEntity employee = EmployeeEntity.findById(payroll.id_employee);
        try {
            PayrollMasterEntity payrollMaster = new PayrollMasterEntity();
            payrollMaster.employee = employee;
            payrollMaster.gaji_pokok = payroll.gaji_pokok;
            payrollMaster.tunjangan_transport = payroll.tunjangan_transport;
            payrollMaster.tunjangan_makan = payroll.tunjangan_makan;
            payrollMaster.tunjangan_lembur = payroll.tunjangan_lembur;
            payrollMaster.tunjangan_lainnya = payroll.tunjangan_lainnya;
            payrollMaster.bpjs_kesehatan = payroll.bpjs_kesehatan;
            payrollMaster.bpjs_ketenagakerjaan = payroll.bpjs_ketenagakerjaan;
            payrollMaster.persist();
            PayrollDeductionMasterEntity payrollMasterDeduction = new PayrollDeductionMasterEntity();
            payrollMasterDeduction.payrollMaster = payrollMaster;
            payrollMasterDeduction.kasbon = payroll.kasbon;
            payrollMasterDeduction.pinjaman = payroll.pinjaman;
            payrollMasterDeduction.thr_paid = payroll.thr_paid;
            payrollMasterDeduction.jaminan_pensiun = payroll.jaminan_pensiun;
            payrollMasterDeduction.bpjs_kesehatan = payroll.bpjs_kesehatan_deduction;
            payrollMasterDeduction.bpjs_kesehatan_family = payroll.bpjs_kesehatan_family;
            payrollMasterDeduction.jht_employee = payroll.jht_employee;
            payrollMasterDeduction.pph21 = payroll.pph21;
            payrollMasterDeduction.persist();

            return Response.ok().entity(ResponseHandler.ok("Create Payroll Master", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
}
