package org.sim.umira.resources.HumanResources;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

            }
            return Response.ok().entity(ResponseHandler.ok("Create Loan Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/update-detail-loan")
    @Transactional
    public Response updateDetailLoan(@QueryParam("id") String id, @QueryParam("nominal") String nominal) {
        try {
            // List<LoanEntity> loanList = LoanEntity.listAll();
            LoanDetailEntity loanD = LoanDetailEntity.findById(id);
            loanD.nominal_cicilan = Integer.parseInt(nominal);
            return Response.ok().entity(ResponseHandler.ok("get Loan Berhasil", null)).build();
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

    private int getMonthNumber(String bulan) {

        if (bulan == null) {
            return 0;
        }

        return switch (bulan.toUpperCase()) {

            case "JANUARY", "JANUARI" -> 1;
            case "FEBRUARY", "FEBRUARI" -> 2;
            case "MARCH", "MARET" -> 3;
            case "APRIL" -> 4;
            case "MAY", "MEI" -> 5;
            case "JUNE", "JUNI" -> 6;
            case "JULY", "JULI" -> 7;
            case "AUGUST", "AGUSTUS" -> 8;
            case "SEPTEMBER" -> 9;
            case "OCTOBER", "OKTOBER" -> 10;
            case "NOVEMBER" -> 11;
            case "DECEMBER", "DESEMBER" -> 12;

            default -> 0;
        };
    }


    public record responseReportLoan(String nip, String nama, String jabatan, String departemen, Integer total_pinjaman, Integer januari, Integer februari, Integer maret, Integer april, Integer mei, Integer juni, Integer juli, Integer agustus, Integer september, Integer oktober, Integer november, Integer desember){}

    
    @GET
    @Path("/report-loan")
    public Response reportLoan(@QueryParam("tahun") String tahun) {

        try {

            // List<LoanEntity> loanList = LoanEntity.find("loanDetail.tahun = ?1", tahun).list();
            List<LoanEntity> loanList = LoanEntity.find(
                "select distinct l " +
                "from LoanEntity l " +
                "join l.loanDetail detail " +
                "where detail.tahun = ?1",
                tahun
            ).list();
          
            // LoanEntity loan = new LoanEntity();
    
            List<responseReportLoan> report = new ArrayList<>();
            for(LoanEntity loan: loanList){
                // Integer januari, februari, maret, april, mei, juni, juli, agustus, september, oktober, november, desember = 0;
                Integer januari = 0;
                Integer februari = 0;
                Integer maret = 0;
                Integer april = 0;
                Integer mei = 0;
                Integer juni = 0;
                Integer juli = 0;
                Integer agustus = 0;
                Integer september = 0;
                Integer oktober = 0;
                Integer november = 0;
                Integer desember = 0;
                for(LoanDetailEntity loanD: loan.loanDetail){
                   Integer bulanInt =  getMonthNumber(loanD.bulan);
                   switch (bulanInt) {
                    case 1:
                        januari = loanD.nominal_cicilan;
                        break;
                    case 2:
                        februari = loanD.nominal_cicilan;
                        break;
                    case 3:
                        maret = loanD.nominal_cicilan;
                        break;
                    case 4:
                        april = loanD.nominal_cicilan;
                        break;
                    case 5:
                        mei = loanD.nominal_cicilan;
                        break;
                    case 6:
                        juni = loanD.nominal_cicilan;
                        break;
                    case 7:
                        juli = loanD.nominal_cicilan;
                        break;
                    case 8:
                        agustus = loanD.nominal_cicilan;
                        break;
                    case 9:
                        september = loanD.nominal_cicilan;
                        break;
                    case 10:
                        oktober = loanD.nominal_cicilan;
                        break;
                    case 11:
                        november = loanD.nominal_cicilan;
                        break;
                    case 12:
                        desember = loanD.nominal_cicilan;
                        break;
                    default:
                        break;
                   }
                }
                report.add(new responseReportLoan(loan.employee.nip, loan.employee.nama,loan.employee.jabatan, loan.employee.departemen, loan.total_pinjaman, januari, februari, maret, april, mei, juni, juli, agustus, september, oktober, november, desember));
            }
            
            // return Response.ok(response).build();

            return Response.ok().entity(ResponseHandler.ok("Report Loan Berhasil", report)).build();
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
