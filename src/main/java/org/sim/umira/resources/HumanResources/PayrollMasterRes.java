package org.sim.umira.resources.HumanResources;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.configs.GoogleCalendarConfig;
import org.sim.umira.dtos.HumanResources.PayrollMasterDto;
import org.sim.umira.dtos.HumanResources.ResponseAttendanceDto;
import org.sim.umira.entities.HumanResources.AttendanceEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.HumanResources.LoanDetailEntity;
import org.sim.umira.entities.HumanResources.LoanEntity;
import org.sim.umira.entities.HumanResources.OvertimeEntity;
import org.sim.umira.entities.HumanResources.PayrollDeductionEntity;
import org.sim.umira.entities.HumanResources.PayrollDeductionMasterEntity;
import org.sim.umira.entities.HumanResources.PayrollEntity;
import org.sim.umira.entities.HumanResources.PayrollMasterEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;
import org.sim.umira.services.YearCalendarService;

import com.google.api.services.calendar.Calendar;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/HR-Payroll")
@Secured
public class PayrollMasterRes {

    @Inject
    EntityManager em;

    // ── Create Payroll for every employee
    // ────────────────────────────────────────────────────────────────
    @POST
    @Path("/create-payroll-master")
    @Transactional
    public Response createPayrollMaster(@RequestBody PayrollMasterDto payroll) {
        EmployeeEntity employee = EmployeeEntity.findById(payroll.id_employee);
        if (employee == null) {
            throw new BadRequestException("Employee tidak ditemukan");
        }

        // Check if master already exists for this employee
        PayrollMasterEntity existing = PayrollMasterEntity.find("employee = ?1", employee).firstResult();
        if (existing != null) {
            throw new BadRequestException("Master payroll untuk employee ini sudah ada");
        }

        try {
            PayrollMasterEntity payrollMaster = new PayrollMasterEntity();
            payrollMaster.employee = employee;
            payrollMaster.gaji_pokok = payroll.gaji_pokok;
            payrollMaster.tunjangan_jabatan = payroll.tunjangan_jabatan;
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

            return Response.ok().entity(ResponseHandler.ok("Create Payroll Master Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @ConfigProperty(name = "date-close-book")
    String tanggal_pembukuan;

    // ── Generate Payroll
    // Monthly──────────────────────────────────────────────────────
    @POST
    @Path("/generate-payroll")
    @Transactional
    public Response generatePayroll(
            @QueryParam("bulan") String bulan,
            @QueryParam("tahun") String tahun) {

        String holidayCalendarId = "id.indonesian#holiday@group.v.calendar.google.com";
        if (bulan == null || tahun == null)
            throw new BadRequestException("Bulan dan tahun wajib diisi");

        try {
            List<EmployeeEntity> employees = EmployeeEntity.listAll();
            int generated = 0;

            for (EmployeeEntity emp : employees) {
                // Skip if already generated
                PayrollEntity existing = PayrollEntity.find(
                        "employee = ?1 AND bulan = ?2 AND tahun = ?3", emp, bulan, tahun).firstResult();
                if (existing != null)
                    continue;

                int monthInt = Integer.parseInt(bulan);
                Month monthM = Month.of(monthInt);
                YearMonth ym = YearMonth.of(Integer.parseInt(tahun), monthM);
                LocalDate startDate = ym.minusMonths(1).atDay(Integer.parseInt(tanggal_pembukuan) + 1);
                LocalDate endDate = ym.atDay(Math.min(Integer.parseInt(tanggal_pembukuan), ym.lengthOfMonth()));
                Calendar service = GoogleCalendarConfig.getService();
                Boolean saturdayOff = true;
                Integer is_office = emp.klasifikasi_works.is_office;
                if (is_office == 1) {
                    saturdayOff = false;
                }

                // 1. ambil libur nasional
                Set<LocalDate> holidays = YearCalendarService.getHolidaysByParams(service, holidayCalendarId,
                        startDate.toString(), endDate.toString());

                // 2. generate 1 tahun
                List<YearCalendarService.DayInfo> calendar = YearCalendarService.generatedDay(Integer.parseInt(tahun),
                        holidays,
                        startDate.toString(), endDate.toString(), saturdayOff);

                Long total_hari_kerja = calendar.stream().filter(a -> "GREEN".equals(a.type)).count();
                List<AttendanceEntity> hadirList = AttendanceEntity.list(
                        "tanggal BETWEEN ?1 AND ?2 AND employee = ?3 AND status = ?4",
                        startDate,
                        endDate,
                        emp,
                        "Hadir");

                Long totalHadir = hadirList.stream()
                        .map(a -> a.tanggal)
                        .distinct()
                        .count();
                List<AttendanceEntity> sakitList = AttendanceEntity.list(
                        "tanggal BETWEEN ?1 AND ?2 AND employee = ?3 AND status = ?4",
                        startDate,
                        endDate,
                        emp,
                        "Sakit");

                Long totalSakit = sakitList.stream()
                        .map(a -> a.tanggal)
                        .distinct()
                        .count();
                List<AttendanceEntity> izinList = AttendanceEntity.list(
                        "tanggal BETWEEN ?1 AND ?2 AND employee = ?3 AND status = ?4",
                        startDate,
                        endDate,
                        emp,
                        "Izin");

                Long totalIzin = izinList.stream()
                        .map(a -> a.tanggal)
                        .distinct()
                        .count();
                List<AttendanceEntity> AlphaList = AttendanceEntity.list(
                        "tanggal BETWEEN ?1 AND ?2 AND employee = ?3",
                        startDate,
                        endDate,
                        emp);

                Set<LocalDate> alphaDates = AlphaList.stream()
                        .map(a -> a.tanggal)
                        .collect(Collectors.toSet());

                Long totalAlpha = calendar.stream()
                        .filter(a -> "GREEN".equals(a.type))
                        .filter(a -> !alphaDates.contains(a.date))
                        .count();
                System.out.println(AlphaList.size());
                // LocalDate startDate =
                // ym.minusMonths(1).atDay(Integer.parseInt(tanggal_pembukuan) + 1);
                // LocalDate endDate = ym.atDay(Math.min(Integer.parseInt(tanggal_pembukuan),
                // ym.lengthOfMonth()));
                List<OvertimeEntity> listOvertime = OvertimeEntity
                        .find("tanggal BETWEEN ?1 AND ?2 AND employee = ?3 ", startDate, endDate, emp).list();
                long totalMinutes = listOvertime.stream()
                        .filter(overtime -> overtime.durasi != null)
                        .filter(overtime -> !overtime.durasi.trim().isEmpty())
                        .mapToLong(overtime -> Long.parseLong(overtime.durasi))
                        .sum();

                long totalHours = (totalMinutes / 60) + (totalMinutes % 60 > 30 ? 1 : 0);

                PayrollMasterEntity master = PayrollMasterEntity.find("employee = ?1", emp).firstResult();

                PayrollEntity payroll = new PayrollEntity();
                payroll.employee = emp;
                payroll.bulan = bulan;
                payroll.tahun = tahun;
                payroll.hari_kerja = String.valueOf(totalHadir);
                payroll.hari_izin = String.valueOf(totalIzin);
                payroll.hari_sakit = String.valueOf(totalSakit);
                payroll.hari_alpha = String.valueOf(totalAlpha);
                payroll.gaji_pokok = (master != null) ? master.gaji_pokok : 0;
                payroll.tunjangan_transport = (master != null) ? master.tunjangan_transport : 0;
                payroll.tunjangan_makan = (master != null) ? master.tunjangan_makan * Math.toIntExact(totalHadir) : 0;
                payroll.tunjangan_lembur = (master != null) ? master.tunjangan_lembur * Math.toIntExact(totalHours) : 0;
                payroll.tunjangan_lainnya = (master != null) ? master.tunjangan_lainnya : 0;
                payroll.bpjs_kesehatan = (master != null) ? (int) Math.round(master.bpjs_kesehatan * 0.05) : 0;
                payroll.bpjs_ketenagakerjaan = (master != null) ? (int) Math.round(master.bpjs_kesehatan * 0.1074) : 0;
                payroll.tunjangan_jabatan = (master != null) ? master.tunjangan_jabatan : 0;
                payroll.persist();

                // Copy deductions from master
                // System.out.println(master.gaji_pokok / total_hari_kerja * totalAlpha);
                Long potongan_gaji = (master != null) ? master.gaji_pokok / total_hari_kerja * totalAlpha : 0;
                PayrollDeductionEntity deduction = new PayrollDeductionEntity();
                deduction.payrollMaster = payroll;
                // if (master != null) {
                // PayrollDeductionMasterEntity masterDed = PayrollDeductionMasterEntity
                // .find("payrollMaster = ?1", master).firstResult();
                // if (masterDed != null) {
                // //deduction.kasbon = masterDed.kasbon; temp sebentar

                // deduction.pinjaman = masterDed.pinjaman;
                // deduction.thr_paid = masterDed.thr_paid;
                // deduction.jaminan_pensiun = masterDed.jaminan_pensiun;
                // deduction.bpjs_kesehatan = masterDed.bpjs_kesehatan;
                // deduction.bpjs_kesehatan_family = masterDed.bpjs_kesehatan_family;
                // deduction.jht_employee = masterDed.jht_employee;
                // deduction.pph21 = masterDed.pph21;
                // }
                // }

                String monthName = monthM.getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH).toUpperCase();
                List<LoanDetailEntity> loan = LoanDetailEntity
                        .find("idPinjaman.employee = ?1 AND bulan = ?2 AND tahun = ?3", emp, monthName, tahun).list();
                Integer loanCicilan = 0;
                for (LoanDetailEntity loanD : loan) {
                    // System.out.println(loanD.nominal_cicilan);
                    loanCicilan += loanD.nominal_cicilan;
                    LoanDetailEntity upd = LoanDetailEntity.findById(loanD.id_detail_pinjaman);
                    upd.status = "PAID";
                }
                deduction.pinjaman = loanCicilan;
                deduction.potongan_kehadiran = Math.toIntExact(potongan_gaji);
                deduction.persist();
                generated++;
            }

            return Response.ok().entity(ResponseHandler.ok(
                    "Generate Payroll " + bulan + "/" + tahun + " Berhasil. Total: " + generated + " karyawan", null))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    // ── Read All ──────────────────────────────────────────────────────────────
    @GET
    @Path("/get-all-payroll-master")
    @Transactional
    public Response getAllPayrollMaster() {
        try {
            List<PayrollMasterEntity> masters = em.createQuery(
                    "SELECT m FROM PayrollMasterEntity m LEFT JOIN FETCH m.employee",
                    PayrollMasterEntity.class).getResultList();

            List<Map<String, Object>> result = new ArrayList<>();
            for (PayrollMasterEntity m : masters) {
                PayrollDeductionMasterEntity ded = PayrollDeductionMasterEntity
                        .find("payrollMaster = ?1", m).firstResult();
                Map<String, Object> map = new HashMap<>();
                map.put("payroll_master", m);
                map.put("deduction", ded);
                result.add(map);
            }
            return Response.ok().entity(ResponseHandler.ok("Get All Payroll Master Berhasil", result)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    // ── Read By Employee ──────────────────────────────────────────────────────
    @GET
    @Path("/get-payroll-master-by-employee")
    @Transactional
    public Response getPayrollMasterByEmployee(@QueryParam("id_employee") String id_employee) {
        try {
            EmployeeEntity employee = EmployeeEntity.findById(id_employee);
            PayrollMasterEntity master = PayrollMasterEntity.find("employee = ?1", employee).firstResult();
            if (master == null) {
                return Response.ok().entity(ResponseHandler.ok("Master payroll belum diatur", null)).build();
            }
            PayrollDeductionMasterEntity ded = PayrollDeductionMasterEntity
                    .find("payrollMaster = ?1", master).firstResult();
            Map<String, Object> result = new HashMap<>();
            result.put("payroll_master", master);
            result.put("deduction", ded);
            return Response.ok().entity(ResponseHandler.ok("Get Payroll Master Berhasil", result)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    // ── Update ────────────────────────────────────────────────────────────────
    @PUT
    @Path("/update-payroll-master")
    @Transactional
    public Response updatePayrollMaster(
            @QueryParam("id_payroll_master") String id_payroll_master,
            @RequestBody PayrollMasterDto payroll) {
        try {
            PayrollMasterEntity master = PayrollMasterEntity.findById(id_payroll_master);
            if (master == null)
                throw new BadRequestException("Payroll master tidak ditemukan");

            master.gaji_pokok = payroll.gaji_pokok;
            master.tunjangan_transport = payroll.tunjangan_transport;
            master.tunjangan_jabatan = master.tunjangan_jabatan;
            master.tunjangan_makan = payroll.tunjangan_makan;
            master.tunjangan_lembur = payroll.tunjangan_lembur;
            master.tunjangan_lainnya = payroll.tunjangan_lainnya;
            master.bpjs_kesehatan = payroll.bpjs_kesehatan;
            master.bpjs_ketenagakerjaan = payroll.bpjs_ketenagakerjaan;

            PayrollDeductionMasterEntity ded = PayrollDeductionMasterEntity
                    .find("payrollMaster = ?1", master).firstResult();
            if (ded != null) {
                ded.kasbon = payroll.kasbon;
                ded.pinjaman = payroll.pinjaman;
                ded.thr_paid = payroll.thr_paid;
                ded.jaminan_pensiun = payroll.jaminan_pensiun;
                ded.bpjs_kesehatan = payroll.bpjs_kesehatan_deduction;
                ded.bpjs_kesehatan_family = payroll.bpjs_kesehatan_family;
                ded.jht_employee = payroll.jht_employee;
                ded.pph21 = payroll.pph21;
            }

            return Response.ok().entity(ResponseHandler.ok("Update Payroll Master Berhasil", null)).build();
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    // ── Get Payroll by Bulan/Tahun ────────────────────────────────────────────
    @GET
    @Path("/get-payroll")
    @Transactional
    public Response getPayroll(
            @QueryParam("bulan") String bulan,
            @QueryParam("tahun") String tahun) {
        try {
            List<PayrollEntity> list = em.createQuery(
                    "SELECT p FROM PayrollEntity p LEFT JOIN FETCH p.employee " +
                            "WHERE p.bulan = :bulan AND p.tahun = :tahun",
                    PayrollEntity.class)
                    .setParameter("bulan", bulan)
                    .setParameter("tahun", tahun)
                    .getResultList();

            List<Map<String, Object>> result = new ArrayList<>();
            for (PayrollEntity p : list) {
                PayrollMasterEntity master = PayrollMasterEntity
                        .find("employee = ?1", p.employee).firstResult();
                PayrollDeductionEntity ded = PayrollDeductionEntity
                        .find("payrollMaster = ?1", p).firstResult();

                Map<String, Object> map = new HashMap<>();
                map.put("payroll", p);
                map.put("master", master);
                map.put("deduction", ded);
                result.add(map);
            }

            return Response.ok().entity(ResponseHandler.ok("Get Payroll Berhasil", result)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    @DELETE
    @Path("/delete-payroll-master")
    @Transactional
    public Response deletePayrollMaster(@QueryParam("id_payroll_master") String id_payroll_master) {
        try {
            PayrollMasterEntity master = PayrollMasterEntity.findById(id_payroll_master);
            if (master == null)
                throw new BadRequestException("Payroll master tidak ditemukan");

            PayrollDeductionMasterEntity.delete("payrollMaster = ?1", master);
            master.delete();

            return Response.ok().entity(ResponseHandler.ok("Hapus Payroll Master Berhasil", null)).build();
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    // ── Delete Payroll by Bulan/Tahun ─────────────────────────────────────────
    @DELETE
    @Path("/delete-payroll")
    @Transactional
    public Response deletePayroll(
            @QueryParam("bulan") String bulan,
            @QueryParam("tahun") String tahun) {
        try {
            List<PayrollEntity> list = PayrollEntity.find(
                    "bulan = ?1 AND tahun = ?2", bulan, tahun).list();

            for (PayrollEntity p : list) {
                PayrollDeductionEntity.delete("payrollMaster = ?1", p);
            }
            long deleted = PayrollEntity.delete("bulan = ?1 AND tahun = ?2", bulan, tahun);

            return Response.ok().entity(ResponseHandler.ok("Hapus Payroll Berhasil", deleted)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}