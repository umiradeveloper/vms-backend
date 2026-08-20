package org.sim.umira.resources.HumanResources;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.HumanResources.EmployeeDto;
import org.sim.umira.dtos.HumanResources.LoanDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.HumanResources.LoanDetailEntity;
import org.sim.umira.entities.HumanResources.LoanEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/Loan")
@Secured
public class LoanRes {
    @POST
    @Path("/create-loan")
    @Transactional
    public Response createLoan(@Valid @RequestBody LoanDto create, @Context SecurityContext ctx) {
        EmployeeEntity emp = EmployeeEntity.findById(create.id_employee);
        if (emp == null) {
            throw new BadRequestException("Employee tidak terdaftar");
        }
        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        try {

            LoanEntity loan = new LoanEntity();
            loan.employee = emp;
            loan.jumlah_cicilan = create.jumlah_cicilan;
            loan.total_pinjaman = create.total_pinjaman;
            loan.bulan_tahun_awal = create.bulan_awal + "-" + create.tahun_awal;
            loan.bulan_tahun_akhir = create.bulan_akhir + "-" + create.tahun_akhir;
            loan.created_at = LocalDateTime.now();
            loan.createdBy = (ue != null) ? ue : null;
            loan.status_paid = "UNPAIDFULL";
            loan.persist();

            
            Integer nominalPinjaman = create.total_pinjaman;
            Integer jumlahCicilan = create.jumlah_cicilan;

            Integer cicilanPerbulan = nominalPinjaman / jumlahCicilan;

            Integer sisa = nominalPinjaman % jumlahCicilan;

            YearMonth start = YearMonth.of(Integer.parseInt(create.tahun_awal), Integer.parseInt(create.bulan_awal));

            for (int i = 0; i < jumlahCicilan; i++) {

                YearMonth month = start.plusMonths(i);

                Integer nominal = cicilanPerbulan;

                if (i == jumlahCicilan - 1) {
                    nominal += sisa;
                }
                LoanDetailEntity loanDetail = new LoanDetailEntity();
                loanDetail.idPinjaman = loan;
                loanDetail.nominal_cicilan = nominal;
                loanDetail.cicilan_ke = i;
                loanDetail.bulan = month.getMonth().toString();
                loanDetail.tahun = String.valueOf(month.getYear());
                loanDetail.status = "PENDING";
                loanDetail.persist();

                // LoanInstallment item = new LoanInstallment();

                // item.bulan = month.getMonthValue();
                // item.tahun = month.getYear();
                // item.nominal = nominal;

                // result.add(item);
            }
            return Response.ok().entity(ResponseHandler.ok("Create Loan Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-loan")
    public Response getLoan() {

        try {
            List<LoanEntity> loanList = LoanEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("get Loan Berhasil", loanList)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/delete-loan")
    @Transactional
    public Response deleteLoan(@QueryParam("id") String id) {

        try {

            boolean deleted = LoanEntity.deleteById(id);
            return Response.ok().entity(ResponseHandler.ok("delete Loan Berhasil", deleted)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
}
