package org.sim.umira.resources.HumanResources;

import java.awt.PageAttributes.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
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
import org.sim.umira.dtos.HumanResources.VerifyPayslipDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.Cuti.CutiEntity;
import org.sim.umira.entities.HumanResources.AttendanceEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.HumanResources.LoanDetailEntity;
import org.sim.umira.entities.HumanResources.LoanEntity;
import org.sim.umira.entities.HumanResources.LockPayrollEntity;
import org.sim.umira.entities.HumanResources.OvertimeEntity;
import org.sim.umira.entities.HumanResources.PayrollDeductionEntity;
import org.sim.umira.entities.HumanResources.PayrollDeductionMasterEntity;
import org.sim.umira.entities.HumanResources.PayrollEntity;
import org.sim.umira.entities.HumanResources.PayrollMasterEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;
import org.sim.umira.kafka.KafkaProducers;
import org.sim.umira.kafka.DTO.EmailEventDto;
import org.sim.umira.services.PayrollData;
import org.sim.umira.services.PdfPayrollService;
import org.sim.umira.services.YearCalendarService;

import com.google.api.services.calendar.Calendar;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/HR-Payroll")
@Secured
public class PayrollMasterRes {

        @Inject
        EntityManager em;

        @Inject
        KafkaProducers kafkaproduce;

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
                System.out.println(payroll.tarif_bpjstk);

                try {
                        PayrollMasterEntity payrollMaster = new PayrollMasterEntity();
                        payrollMaster.employee = employee;
                        payrollMaster.gaji_pokok = payroll.gaji_pokok;
                        payrollMaster.tunjangan_jabatan = payroll.tunjangan_jabatan;
                        payrollMaster.tunjangan_transport = payroll.tunjangan_transport;
                        payrollMaster.tunjangan_pulsa = payroll.tunjangan_pulsa;
                        payrollMaster.tunjangan_makan = payroll.tunjangan_makan;
                        payrollMaster.tunjangan_operasional = payroll.tunjangan_operasional;
                        payrollMaster.tunjangan_lembur = generatedLembur(payroll.tunjangan_lembur, payroll.gaji_pokok);
                        payrollMaster.tunjangan_lainnya = payroll.tunjangan_lainnya;
                        payrollMaster.bpjs_kesehatan = payroll.bpjs_kesehatan;
                        payrollMaster.bpjs_ketenagakerjaan = payroll.bpjs_ketenagakerjaan;
                        payrollMaster.tarif_bpjs_kesehatan = payroll.tarif_bpjs_kesehatan;
                        payrollMaster.tarif_bpjs_ketenagakerjaan = payroll.tarif_bpjs_ketenagakerjaan;
                        payrollMaster.kode_lembur = payroll.tunjangan_lembur;
                        payrollMaster.persist();

                        PayrollDeductionMasterEntity payrollMasterDeduction = new PayrollDeductionMasterEntity();
                        payrollMasterDeduction.payrollMaster = payrollMaster;
                        payrollMasterDeduction.tarif_bpjskes = payroll.tarif_bpjskes;
                        payrollMasterDeduction.tarif_bpjstk = payroll.tarif_bpjstk;
                        payrollMasterDeduction.pph21 = payroll.pph21;
                        payrollMasterDeduction.potongan_lainnya = payroll.potongan_lainnya;
                        payrollMasterDeduction.persist();

                        return Response.ok().entity(ResponseHandler.ok("Create Payroll Master Berhasil", null)).build();
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new InternalServerErrorException(e.getMessage());
                }
        }

        private Integer generatedLembur(String lembur, Integer gapok) {
                Integer nominal = 0;
                String[] splitLembur = lembur.split("\\|");
                System.out.println(splitLembur);
                if (splitLembur[0].equals("-")) {
                        nominal = 0;
                } else if (splitLembur[0].equals("0")) {
                        nominal = gapok / 173;
                } else {
                        // System.out.println(splitLembur[2]);
                        nominal = Integer.parseInt(splitLembur[1].replace(".", ""));
                }
                return nominal;
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

                LockPayrollEntity lockPayrollCheck = LockPayrollEntity.find("tahun = ?1 AND bulan = ?2", tahun, bulan).firstResult();
                if (bulan == null || tahun == null){
                        throw new BadRequestException("Bulan dan tahun wajib diisi");
                }
                if(lockPayrollCheck != null){
                        if(lockPayrollCheck.status.equals("on")){
                                throw new BadRequestException("Payslip sudah di kunci");
                        }
                }

                        

                try {
                        List<EmployeeEntity> employees = EmployeeEntity.listAll();
                        int generated = 0;

                        for (EmployeeEntity emp : employees) {
                                // Skip if already generated
                                if (emp.status_employee == 1) {
                                        PayrollMasterEntity master = PayrollMasterEntity.find("employee = ?1", emp)
                                                        .firstResult();
                                        if (master != null) {
                                                PayrollEntity existing = PayrollEntity.find(
                                                                "employee = ?1 AND bulan = ?2 AND tahun = ?3", emp,
                                                                bulan, tahun).firstResult();
                                                if (existing != null)
                                                        continue;

                                                int monthInt = Integer.parseInt(bulan);
                                                Month monthM = Month.of(monthInt);
                                                YearMonth ym = YearMonth.of(Integer.parseInt(tahun), monthM);
                                                LocalDate startDate = ym.minusMonths(1)
                                                                .atDay(Integer.parseInt(tanggal_pembukuan) + 1);
                                                LocalDate endDate = ym
                                                                .atDay(Math.min(Integer.parseInt(tanggal_pembukuan),
                                                                                ym.lengthOfMonth()));
                                                Calendar service = GoogleCalendarConfig.getService();
                                                Boolean saturdayOff = true;
                                                Integer is_office = emp.klasifikasi_works.is_office;
                                                if (is_office == 1) {
                                                        saturdayOff = false;
                                                }

                                                // 1. ambil libur nasional
                                                Set<LocalDate> holidays = YearCalendarService.getHolidaysByParams(
                                                                service, holidayCalendarId,
                                                                startDate.toString(), endDate.toString());

                                                // 2. generate 1 tahun
                                                List<YearCalendarService.DayInfo> calendar = YearCalendarService
                                                                .generatedDay(
                                                                                Integer.parseInt(tahun),
                                                                                holidays,
                                                                                startDate.toString(),
                                                                                endDate.toString(), saturdayOff);

                                                Long total_hari_kerja = calendar.stream()
                                                                .filter(a -> "Work".equals(a.status)).count();
                                                Integer totalHadir = 0;
                                                Integer totalIzin = 0;
                                                Integer totalSakit = 0;
                                                Integer totalAlpha = 0;
                                                // for (YearCalendarService.DayInfo date : calendar) {
                                                // if (date.status.equals("Work")) {
                                                // List<CutiEntity> izinList = CutiEntity.list(
                                                // "tanggal_mulai <= ?1 AND tanggal_selesai >= ?2 AND jenis_cuti != ?3
                                                // AND status_cuti = ?4 AND employee_pengajuan = ?5 AND tanggal_manager
                                                // IS NOT NULL",
                                                // date.date, date.date, "SICK_LEAVE",
                                                // "APPROVED",
                                                // emp);
                                                // if (izinList.size() > 0) {
                                                // totalIzin++;
                                                // } else {
                                                // List<CutiEntity> sakitList = CutiEntity.list(
                                                // "tanggal_mulai <= ?1 AND tanggal_selesai >= ?2 AND jenis_cuti = ?3
                                                // AND status_cuti = ?4 AND employee_pengajuan = ?5 AND tanggal_manager
                                                // IS NOT NULL",
                                                // date.date, date.date,
                                                // "SICK_LEAVE",
                                                // "APPROVED", emp);
                                                // if (sakitList.size() > 0) {
                                                // totalSakit++;
                                                // } else {
                                                // List<AttendanceEntity> hadirList = AttendanceEntity
                                                // .list(
                                                // "tanggal = ?1 AND employee = ?2 AND status = ?3",
                                                // date.date,
                                                // emp,
                                                // "Hadir");
                                                // if (hadirList.size() > 0) {
                                                // totalHadir++;
                                                // } else {
                                                // totalAlpha++;
                                                // }
                                                // }
                                                // }
                                                // }

                                                // }

                                                for (YearCalendarService.DayInfo g : calendar) {
                                                        // String status;

                                                        if ("Work".equals(g.status)) {
                                                                List<String> excludeJenisCuti = List.of(
                                                                                "SICK_LEAVE",
                                                                                "IZIN");
                                                                AttendanceEntity ae = AttendanceEntity.find(
                                                                                "tanggal = ?1 AND employee = ?2",
                                                                                g.date,
                                                                                emp).firstResult();

                                                                boolean isCuti = CutiEntity.count(
                                                                                "tanggal_mulai <= ?1 " +
                                                                                                "AND tanggal_selesai >= ?1 "
                                                                                                +
                                                                                                "AND employee_pengajuan = ?2 AND jenis_cuti NOT IN ?3",
                                                                                g.date,
                                                                                emp, excludeJenisCuti) > 0;

                                                                boolean Izin = CutiEntity.count(
                                                                                "tanggal_mulai <= ?1 " +
                                                                                                "AND tanggal_selesai >= ?1 "
                                                                                                +
                                                                                                "AND employee_pengajuan = ?2 AND jenis_cuti = ?3",
                                                                                g.date,
                                                                                emp, "IZIN") > 0;

                                                                boolean Sakit = CutiEntity.count(
                                                                                "tanggal_mulai <= ?1 " +
                                                                                                "AND tanggal_selesai >= ?1 "
                                                                                                +
                                                                                                "AND employee_pengajuan = ?2 AND jenis_cuti = ?3",
                                                                                g.date,
                                                                                emp, "SICK_LEAVE") > 0;

                                                                /*
                                                                 * Tentukan status
                                                                 */

                                                                if (isCuti) {
                                                                        totalIzin++;
                                                                } else if (Izin) {
                                                                        totalIzin++;
                                                                } else if (Sakit) {
                                                                        totalSakit++;
                                                                } else if (ae != null) {
                                                                        if ("Hadir".equals(ae.status)) {
                                                                                totalHadir++;
                                                                                // attendance.put(
                                                                                // g.date.toString(),
                                                                                // "H \n " + ae.jam_masuk
                                                                                // + "-"
                                                                                // + ae.jam_keluar);

                                                                        } else if ("WFH".equals(ae.status)) {
                                                                                totalHadir++;
                                                                                // attendance.put(
                                                                                // g.date.toString(),
                                                                                // "H \n " + ae.jam_masuk
                                                                                // + "-"
                                                                                // + ae.jam_keluar);

                                                                        }

                                                                        // Sesuaikan dengan field entity
                                                                        // AttendanceEntity
                                                                        // status = "P";

                                                                } else {

                                                                        totalAlpha++;
                                                                        // attendance.put(
                                                                        // g.date.toString(),
                                                                        // "A");
                                                                }

                                                        }

                                                        // attendance.put(
                                                        // g.date.toString(),
                                                        // status
                                                        // );
                                                }

                                                // List<AttendanceEntity> hadirList = AttendanceEntity.list(
                                                // "tanggal BETWEEN ?1 AND ?2 AND employee = ?3 AND status = ?4",
                                                // startDate,
                                                // endDate,
                                                // emp,
                                                // "Hadir");

                                                // Long totalHadir = hadirList.stream()
                                                // .map(a -> a.tanggal)
                                                // .distinct()
                                                // .count();

                                                // List<CutiEntity> sakitList = CutiEntity.list("tanggal_mulai >= ?1 AND
                                                // tanggal_selesai <= ?2 AND jenis_cuti = ?3 AND status_cuti = ?4 AND
                                                // employee_pengajuan = ?5 AND tanggal_manager IS NOT NULL", startDate,
                                                // endDate, "SICK_LEAVE", "APPROVED", emp);
                                                // Set<LocalDate> sakitDates = sakitList.stream()
                                                // .flatMap(cuti -> {
                                                // LocalDate mulai = cuti.tanggal_mulai;
                                                // LocalDate selesai = cuti.tanggal_selesai;

                                                // return mulai.datesUntil(selesai.plusDays(1));
                                                // })
                                                // .collect(Collectors.toSet());

                                                // Long totalSakit = calendar.stream()
                                                // .filter(a -> sakitDates.contains(a.date))
                                                // .count();
                                                // long totalSakit = sakitList.stream()
                                                // .mapToLong(a -> {
                                                // LocalDate mulai = a.tanggal_mulai.isBefore(startDate)
                                                // ? startDate
                                                // : a.tanggal_mulai;

                                                // LocalDate selesai = a.tanggal_selesai.isAfter(endDate)
                                                // ? endDate
                                                // : a.tanggal_selesai;

                                                // return ChronoUnit.DAYS.between(mulai, selesai) + 1;
                                                // })
                                                // .sum();

                                                // List<CutiEntity> izinList = CutiEntity.list("tanggal_mulai >= ?1 AND
                                                // tanggal_selesai <= ?2 AND jenis_cuti != ?3 AND status_cuti = ?4 AND
                                                // employee_pengajuan = ?5 AND tanggal_manager IS NOT NULL", startDate,
                                                // endDate, "SICK_LEAVE", "APPROVED", emp);
                                                // Set<LocalDate> izinDates = izinList.stream()
                                                // .flatMap(cuti -> {
                                                // LocalDate mulai = cuti.tanggal_mulai;
                                                // LocalDate selesai = cuti.tanggal_selesai;

                                                // return mulai.datesUntil(selesai.plusDays(1));
                                                // })
                                                // .collect(Collectors.toSet());

                                                // Long totalIzin = calendar.stream()
                                                // .filter(a -> izinDates.contains(a.date))
                                                // .count();
                                                // long totalIzin = izinList.stream()
                                                // .mapToLong(a ->

                                                // {
                                                // LocalDate mulai = a.tanggal_mulai.isBefore(startDate)
                                                // ? startDate
                                                // : a.tanggal_mulai;

                                                // LocalDate selesai = a.tanggal_selesai.isAfter(endDate)
                                                // ? endDate
                                                // : a.tanggal_selesai;

                                                // return ChronoUnit.DAYS.between(mulai, selesai) + 1;
                                                // }
                                                // )
                                                // .sum();

                                                // List<AttendanceEntity> AlphaList = AttendanceEntity.list(
                                                // "tanggal BETWEEN ?1 AND ?2 AND employee = ?3",
                                                // startDate,
                                                // endDate,
                                                // emp);

                                                // Set<LocalDate> alphaDates = AlphaList.stream()
                                                // .map(a -> a.tanggal)
                                                // .collect(Collectors.toSet());

                                                // Long totalAlpha = calendar.stream()
                                                // .filter(a -> "Work".equals(a.status))
                                                // .filter(a -> !alphaDates.contains(a.date))
                                                // .count();

                                                // Long alphaTot = total_hari_kerja - totalHadir - totalIzin -
                                                // totalSakit;
                                                // System.out.println("total Izin "+totalIzin);
                                                // LocalDate startDate =
                                                // ym.minusMonths(1).atDay(Integer.parseInt(tanggal_pembukuan) + 1);
                                                // LocalDate endDate =
                                                // ym.atDay(Math.min(Integer.parseInt(tanggal_pembukuan),
                                                // ym.lengthOfMonth()));
                                                List<OvertimeEntity> listOvertime = OvertimeEntity
                                                                .find("tanggal BETWEEN ?1 AND ?2 AND employee = ?3 ",
                                                                                startDate, endDate, emp)
                                                                .list();
                                                long totalMinutes = listOvertime.stream()
                                                                .filter(overtime -> overtime.durasi != null)
                                                                .filter(overtime -> !overtime.durasi.trim().isEmpty())
                                                                .mapToLong(overtime -> Long.parseLong(overtime.durasi))
                                                                .sum();

                                                long totalHours = (totalMinutes / 60)
                                                                + (totalMinutes % 60 > 30 ? 1 : 0);

                                                BigDecimal bpjs = new BigDecimal(
                                                                (master.bpjs_kesehatan != null) ? master.bpjs_kesehatan
                                                                                : 0);
                                                BigDecimal tarif_bpjs = new BigDecimal(
                                                                (master.tarif_bpjs_kesehatan != null)
                                                                                ? master.tarif_bpjs_kesehatan
                                                                                : "0");
                                                BigDecimal bpjstk = new BigDecimal((master.bpjs_ketenagakerjaan != null)
                                                                ? master.bpjs_ketenagakerjaan
                                                                : 0);
                                                BigDecimal tarif_bpjstk = new BigDecimal(
                                                                (master.tarif_bpjs_ketenagakerjaan != null)
                                                                                ? master.tarif_bpjs_ketenagakerjaan
                                                                                : "0");

                                                PayrollEntity payroll = new PayrollEntity();
                                                payroll.employee = emp;
                                                payroll.bulan = bulan;
                                                payroll.tahun = tahun;
                                                payroll.hari_kerja = String.valueOf(totalHadir);
                                                payroll.hari_izin = String.valueOf(totalIzin);
                                                payroll.hari_sakit = String.valueOf(totalSakit);
                                                payroll.hari_alpha = String.valueOf(totalAlpha);
                                                payroll.gaji_pokok = (master != null) ? master.gaji_pokok : 0;
                                                payroll.tunjangan_transport = (master != null)
                                                                ? master.tunjangan_transport
                                                                : 0;
                                                payroll.tunjangan_operasional = (master != null)
                                                                ? master.tunjangan_operasional
                                                                : 0;
                                                payroll.tunjangan_pulsa = (master != null) ? master.tunjangan_pulsa : 0;
                                                payroll.tunjangan_makan = (master != null)
                                                                ? master.tunjangan_makan * Math.toIntExact(totalHadir)
                                                                : 0;
                                                payroll.tunjangan_lembur = (master != null)
                                                                ? master.tunjangan_lembur * Math.toIntExact(totalHours)
                                                                : 0;
                                                payroll.tunjangan_lainnya = (master != null) ? master.tunjangan_lainnya
                                                                : 0;
                                                payroll.bpjs_kesehatan = (master != null)
                                                                ? (int) bpjs.multiply(tarif_bpjs)
                                                                                .setScale(0, RoundingMode.HALF_UP)
                                                                                .intValue()
                                                                : 0;
                                                payroll.bpjs_ketenagakerjaan = (master != null)
                                                                ? (int) bpjstk.multiply(tarif_bpjstk)
                                                                                .setScale(0, RoundingMode.HALF_UP)
                                                                                .intValue()
                                                                : 0;
                                                payroll.tunjangan_jabatan = (master != null) ? master.tunjangan_jabatan
                                                                : 0;
                                                payroll.persist();

                                                // Copy deductions from master
                                                // System.out.println(master.gaji_pokok / total_hari_kerja *
                                                // totalAlpha);
                                                Long potongan_gaji = (master != null)
                                                                ? master.gaji_pokok / total_hari_kerja * totalAlpha
                                                                : 0;
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
                                                                .find("idPinjaman.employee = ?1 AND bulan = ?2 AND tahun = ?3",
                                                                                emp, monthName, tahun)
                                                                .list();
                                                Integer loanCicilan = 0;
                                                for (LoanDetailEntity loanD : loan) {
                                                        // System.out.println(loanD.nominal_cicilan);
                                                        loanCicilan += loanD.nominal_cicilan;
                                                        LoanDetailEntity upd = LoanDetailEntity
                                                                        .findById(loanD.id_detail_pinjaman);
                                                        upd.status = "PAID";
                                                }
                                                PayrollDeductionMasterEntity masterDeduction = PayrollDeductionMasterEntity
                                                                .find("payrollMaster = ?1", master).firstResult();
                                                System.out.println(masterDeduction.potongan_lainnya);
                                                BigDecimal dedbpjs = new BigDecimal(master.bpjs_kesehatan);
                                                BigDecimal dedtarif_bpjs = new BigDecimal((masterDeduction != null)
                                                                ? (masterDeduction.tarif_bpjskes != null)
                                                                                ? masterDeduction.tarif_bpjskes
                                                                                : "0"
                                                                : "0");
                                                BigDecimal dedbpjstk = new BigDecimal(master.bpjs_ketenagakerjaan);
                                                BigDecimal dedtarif_bpjstk = new BigDecimal((masterDeduction != null)
                                                                ? (masterDeduction.tarif_bpjstk != null)
                                                                                ? masterDeduction.tarif_bpjstk
                                                                                : "0"
                                                                : "0");
                                                deduction.pinjaman = loanCicilan;
                                                deduction.potongan_kehadiran = Math.toIntExact(potongan_gaji);
                                                deduction.bpjskes = (masterDeduction != null)
                                                                ? (int) dedbpjs.multiply(dedtarif_bpjs)
                                                                                .setScale(0, RoundingMode.HALF_UP)
                                                                                .intValue()
                                                                : 0;
                                                deduction.bpjstk = (masterDeduction != null)
                                                                ? (int) dedbpjstk.multiply(dedtarif_bpjstk)
                                                                                .setScale(0, RoundingMode.HALF_UP)
                                                                                .intValue()
                                                                : 0;
                                                deduction.potongan_lainnya = (masterDeduction != null)
                                                                ? (masterDeduction.potongan_lainnya != null)
                                                                                ? masterDeduction.potongan_lainnya
                                                                                : 0
                                                                : 0;
                                                deduction.persist();
                                                generated++;
                                        }

                                }

                        }

                        return Response.ok().entity(ResponseHandler.ok(
                                        "Generate Payroll " + bulan + "/" + tahun + " Berhasil. Total: " + generated
                                                        + " karyawan",
                                        null))
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
                        return Response.ok().entity(ResponseHandler.ok("Get All Payroll Master Berhasil", result))
                                        .build();
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
                                return Response.ok().entity(ResponseHandler.ok("Master payroll belum diatur", null))
                                                .build();
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
                        // master.tunjangan_lembur = payroll.tunjangan_lembur;
                        master.tunjangan_lainnya = payroll.tunjangan_lainnya;
                        master.bpjs_kesehatan = payroll.bpjs_kesehatan;
                        master.bpjs_ketenagakerjaan = payroll.bpjs_ketenagakerjaan;

                        PayrollDeductionMasterEntity ded = PayrollDeductionMasterEntity
                                        .find("payrollMaster = ?1", master).firstResult();
                        if (ded != null) {

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
        // @Transactional
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
                LockPayrollEntity lockPayrollCheck = LockPayrollEntity.find("tahun = ?1 AND bulan = ?2", tahun, bulan).firstResult();
                if(lockPayrollCheck != null){
                        if(lockPayrollCheck.status.equals("on")){
                                throw new BadRequestException("Payroll sudah di kunci");
                        }
                }
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

        @GET
        @Path("/get-payslip")
        public Response getPayslip(@Context SecurityContext ctx) {
                UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
                EmployeeEntity employee = EmployeeEntity.find("user = ?1", ue).firstResult();
                try {
                        List<PayrollEntity> payroll = PayrollEntity.find("employee = ?1", employee).list();
                        List<PayrollEntity> payrollResult = new ArrayList<>();
                        for(PayrollEntity pay: payroll){
                                LockPayrollEntity lockCheck = LockPayrollEntity.find("tahun = ?1 AND bulan = ?2", pay.tahun, pay.bulan).firstResult();
                                if(lockCheck != null){
                                        if(lockCheck.status.equals("on")){
                                                payrollResult.add(pay);
                                        }
                                }
                                
                        }
                        
                        return Response.ok().entity(ResponseHandler.ok("get Payslip Berhasil", payrollResult)).build();
                } catch (Exception e) {
                        throw new InternalServerErrorException(e.getMessage());
                        // TODO: handle exception
                }

        }

        @GET
        @Path("/lock-payslip")
        @Transactional
        public Response lockPayroll(@QueryParam("tahun") String tahun, @QueryParam("bulan") String bulan, @QueryParam("status") String status){
               try {
                        // List<PayrollEntity> payroll = PayrollEntity.find("tahun = ?1 AND bulan = ?2", tahun, bulan).list();
                        // Integer lockPayroll = PayrollEntity.update("status_lock = ?1 WHERE tahun = ?2 AND bulan = ?3", status, tahun, bulan);
                        LockPayrollEntity checkLock = LockPayrollEntity.find("tahun = ?1 AND bulan = ?2", tahun, bulan).firstResult();
                        if(checkLock != null){
                                checkLock.status = status;
                        }else{
                                LockPayrollEntity lockNew = new LockPayrollEntity();
                                lockNew.bulan = bulan;
                                lockNew.tahun = tahun;
                                lockNew.status = status;
                                lockNew.persist();
                        }
                        return Response.ok().entity(ResponseHandler.ok("Lock Payslip Berhasil", null)).build();
                } catch (Exception e) {
                        throw new InternalServerErrorException(e.getMessage());
                        // TODO: handle exception
                } 
        }

        @GET
        @Path("/get-lock-payslip")
        @Transactional
        public Response getLockPayroll(@QueryParam("tahun") String tahun, @QueryParam("bulan") String bulan, @QueryParam("status") String status){
               try {
                        // List<PayrollEntity> payroll = PayrollEntity.find("tahun = ?1 AND bulan = ?2", tahun, bulan).list();
                        // Integer lockPayroll = PayrollEntity.update("status_lock = ?1 WHERE tahun = ?2 AND bulan = ?3", status, tahun, bulan);
                        LockPayrollEntity checkLock = LockPayrollEntity.find("tahun = ?1 AND bulan = ?2", tahun, bulan).firstResult();
                        
                        return Response.ok().entity(ResponseHandler.ok("get Lock Payslip Berhasil", checkLock)).build();
                } catch (Exception e) {
                        throw new InternalServerErrorException(e.getMessage());
                        // TODO: handle exception
                } 
        }



        public record payslipResponse(PayrollEntity payroll, PayrollDeductionEntity payrollDeduction) {
        }

        @POST
        @Path("/verify-payslip")
        public Response verifyPayslip(@Valid @RequestBody VerifyPayslipDto payslip, @Context SecurityContext ctx) {
                UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
                EmployeeEntity employee = EmployeeEntity.find("user = ?1", ue).firstResult();
                if (!BcryptUtil.matches(payslip.password, ue.password)) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(ResponseHandler.error("Password not match")).build();
                }
                try {
                        PayrollEntity payroll = PayrollEntity.find("employee = ?1 AND tahun = ?2 AND bulan = ?3",
                                        employee, payslip.tahun, payslip.bulan).firstResult();
                        PayrollDeductionEntity payrollDeduction = PayrollDeductionEntity
                                        .find("payrollMaster = ?1", payroll).firstResult();

                        return Response.ok().entity(ResponseHandler.ok("Verifikasi Berhasil",
                                        new payslipResponse(payroll, payrollDeduction))).build();
                } catch (Exception e) {
                        throw new InternalServerErrorException(e.getMessage());
                        // TODO: handle exception
                }
        }

        @Inject
        PdfPayrollService payrollService;

        @GET
        @Path("/send-payslip-bulk")
        public Response sendBulk(@QueryParam("status") String status,
                        @QueryParam("id_employee") List<String> id_employee, @QueryParam("bulan") String bulan,
                        @QueryParam("tahun") String tahun) {
                try {

                        /*
                         * Untuk sementara contoh data.
                         * Nanti bagian ini diambil dari MySQL.
                         */
                        System.out.println(status);
                        if ("ALL".equals(status)) {
                                List<PayrollEntity> payrollSend = PayrollEntity
                                                .find("bulan = ?1 AND tahun = ?2", bulan, tahun).list();
                                for (PayrollEntity payroll : payrollSend) {
                                        PayrollDeductionEntity payrollDed = PayrollDeductionEntity
                                                        .find("payrollMaster", payroll).firstResult();
                                        PayrollData data = new PayrollData();

                                        data.nama = payroll.employee.nama;

                                        data.jabatan = payroll.employee.jabatan;

                                        data.departemen = payroll.employee.departemen;

                                        // data.hariKerja = Integer.parseInt(payroll.hari_kerja);
                                        // data.izin = 0;
                                        // data.sakit = 0;
                                        // data.alpha = 27;

                                        data.gajiPokok = new BigDecimal(payroll.gaji_pokok);

                                        data.tjPulsa = new BigDecimal(payroll.tunjangan_pulsa);
                                        data.tjJabatan = new BigDecimal(payroll.tunjangan_jabatan);
                                        data.tjOperasional = new BigDecimal(payroll.tunjangan_operasional);
                                        data.tjTransport = new BigDecimal(payroll.tunjangan_transport);
                                        data.tjMakan = new BigDecimal(payroll.tunjangan_makan);
                                        data.tjLembur = new BigDecimal(payroll.tunjangan_lembur);
                                        data.tjLainnya = new BigDecimal(payroll.tunjangan_lainnya);

                                        data.bpjsKesehatanPendapatan = new BigDecimal(payroll.bpjs_kesehatan);
                                        data.bpjsKetenagakerjaanPendapatan = new BigDecimal(
                                                        payroll.bpjs_ketenagakerjaan);

                                        data.totalPendapatan = new BigDecimal(payroll.gaji_pokok
                                                        + payroll.tunjangan_jabatan + payroll.tunjangan_lainnya
                                                        + payroll.tunjangan_lembur + payroll.tunjangan_makan
                                                        + payroll.tunjangan_makan + payroll.tunjangan_operasional
                                                        + payroll.tunjangan_pulsa + payroll.tunjangan_transport
                                                        + payroll.bpjs_kesehatan + payroll.bpjs_ketenagakerjaan);

                                        data.potonganKehadiran = new BigDecimal(payrollDed.potongan_kehadiran);
                                        data.bpjsKesehatan = new BigDecimal(payrollDed.bpjskes);

                                        data.bpjsKetenagakerjaan = new BigDecimal(payrollDed.bpjstk);

                                        data.potonganLainnya = new BigDecimal(payrollDed.potongan_lainnya);
                                        data.pinjaman = new BigDecimal(payrollDed.pinjaman);
                                        data.pph21 = new BigDecimal(payrollDed.pph21);

                                        data.totalPotongan = new BigDecimal(payrollDed.potongan_kehadiran
                                                        + payrollDed.bpjskes + payrollDed.bpjstk
                                                        + payrollDed.potongan_lainnya + payrollDed.pinjaman
                                                        + payrollDed.pph21);

                                        data.takeHomePay = new BigDecimal((payroll.gaji_pokok
                                                        + payroll.tunjangan_jabatan + payroll.tunjangan_lainnya
                                                        + payroll.tunjangan_lembur + payroll.tunjangan_makan
                                                        + payroll.tunjangan_makan + payroll.tunjangan_operasional
                                                        + payroll.tunjangan_pulsa + payroll.tunjangan_transport
                                                        + payroll.bpjs_kesehatan + payroll.bpjs_ketenagakerjaan)
                                                        - (payrollDed.potongan_kehadiran + payrollDed.bpjskes
                                                                        + payrollDed.bpjstk
                                                                        + payrollDed.potongan_lainnya
                                                                        + payrollDed.pinjaman + payrollDed.pph21));

                                        byte[] pdf = payrollService.generate(data);
                                        kafkaproduce.sendEmail(new EmailEventDto(payroll.employee.email,
                                                        "Payslip-" + bulan + "-" + tahun, "",
                                                        "Payslip-" + bulan + "-" + tahun, pdf));
                                }
                        } else {
                                List<EmployeeEntity> employeeSend = EmployeeEntity
                                                .find("id_employee IN ?1", id_employee).list();
                                List<PayrollEntity> payrollSend = PayrollEntity
                                                .find("bulan = ?1 AND tahun = ?2 AND employee IN ?3", bulan, tahun,
                                                                employeeSend)
                                                .list();
                                for (PayrollEntity payroll : payrollSend) {
                                        System.out.println(payroll.hari_alpha);
                                        PayrollDeductionEntity payrollDed = PayrollDeductionEntity
                                                        .find("payrollMaster", payroll).firstResult();
                                        PayrollData data = new PayrollData();

                                        data.nama = payroll.employee.nama;

                                        data.jabatan = payroll.employee.jabatan;

                                        data.departemen = payroll.employee.departemen;

                                        // data.hariKerja = Integer.parseInt(payroll.hari_kerja);
                                        // data.izin = 0;
                                        // data.sakit = 0;
                                        // data.alpha = 27;

                                        data.gajiPokok = new BigDecimal(payroll.gaji_pokok);

                                        data.tjPulsa = new BigDecimal(payroll.tunjangan_pulsa);
                                        data.tjJabatan = new BigDecimal(payroll.tunjangan_jabatan);
                                        data.tjOperasional = new BigDecimal(payroll.tunjangan_operasional);
                                        data.tjTransport = new BigDecimal(payroll.tunjangan_transport);
                                        data.tjMakan = new BigDecimal(payroll.tunjangan_makan);
                                        data.tjLembur = new BigDecimal(payroll.tunjangan_lembur);
                                        data.tjLainnya = new BigDecimal(payroll.tunjangan_lainnya);

                                        data.bpjsKesehatanPendapatan = new BigDecimal(payroll.bpjs_kesehatan);
                                        data.bpjsKetenagakerjaanPendapatan = new BigDecimal(
                                                        payroll.bpjs_ketenagakerjaan);

                                        data.totalPendapatan = new BigDecimal(payroll.gaji_pokok
                                                        + payroll.tunjangan_jabatan + payroll.tunjangan_lainnya
                                                        + payroll.tunjangan_lembur + payroll.tunjangan_makan
                                                        + payroll.tunjangan_makan + payroll.tunjangan_operasional
                                                        + payroll.tunjangan_pulsa + payroll.tunjangan_transport
                                                        + payroll.bpjs_kesehatan + payroll.bpjs_ketenagakerjaan);

                                        data.potonganKehadiran = new BigDecimal(payrollDed.potongan_kehadiran);
                                        data.bpjsKesehatan = new BigDecimal(payrollDed.bpjskes);

                                        data.bpjsKetenagakerjaan = new BigDecimal(payrollDed.bpjstk);

                                        data.potonganLainnya = new BigDecimal(payrollDed.potongan_lainnya);
                                        data.pinjaman = new BigDecimal(payrollDed.pinjaman);
                                        data.pph21 = new BigDecimal(payrollDed.pph21);

                                        data.totalPotongan = new BigDecimal(payrollDed.potongan_kehadiran
                                                        + payrollDed.bpjskes + payrollDed.bpjstk
                                                        + payrollDed.potongan_lainnya + payrollDed.pinjaman
                                                        + payrollDed.pph21);

                                        data.takeHomePay = new BigDecimal((payroll.gaji_pokok
                                                        + payroll.tunjangan_jabatan + payroll.tunjangan_lainnya
                                                        + payroll.tunjangan_lembur + payroll.tunjangan_makan
                                                        + payroll.tunjangan_makan + payroll.tunjangan_operasional
                                                        + payroll.tunjangan_pulsa + payroll.tunjangan_transport
                                                        + payroll.bpjs_kesehatan + payroll.bpjs_ketenagakerjaan)
                                                        - (payrollDed.potongan_kehadiran + payrollDed.bpjskes
                                                                        + payrollDed.bpjstk
                                                                        + payrollDed.potongan_lainnya
                                                                        + payrollDed.pinjaman + payrollDed.pph21));

                                        byte[] pdf = payrollService.generate(data);
                                        kafkaproduce.sendEmail(new EmailEventDto(payroll.employee.email,
                                                        "Payslip-" + bulan + "-" + tahun, "",
                                                        "Payslip-" + bulan + "-" + tahun, pdf));
                                }
                        }

                        // PayrollData data =
                        // new PayrollData();

                        // data.nama =
                        // "Rudiat M Yamin";

                        // data.jabatan =
                        // "HSE Officer";

                        // data.departemen =
                        // "Health Safety Environment";

                        // data.hariKerja = 0;
                        // data.izin = 0;
                        // data.sakit = 0;
                        // data.alpha = 27;

                        // data.gajiPokok =
                        // new BigDecimal("6600000");

                        // data.tjPulsa =
                        // new BigDecimal("200000");

                        // data.bpjsKesehatanPendapatan =
                        // new BigDecimal("30000");

                        // data.potonganKehadiran =
                        // new BigDecimal("6599988");

                        // data.bpjsKesehatan =
                        // new BigDecimal("30000");

                        // data.bpjsKetenagakerjaan =
                        // new BigDecimal("90000");

                        // data.totalPendapatan =
                        // new BigDecimal("6830000");

                        // data.totalPotongan =
                        // new BigDecimal("6719988");

                        // data.takeHomePay =
                        // new BigDecimal("110012");

                        // byte[] pdf =
                        // payrollService.generate(data);

                        // return Response.ok(pdf)
                        // .header(
                        // "Content-Disposition",
                        // "inline; filename=\"slip-gaji-" +
                        // id +
                        // ".pdf\""
                        // )
                        // .type("application/pdf")
                        // .build();

                        return Response.ok()
                                        .entity(ResponseHandler.ok("Send Payroll Berhasil tunggu bebrapa saat", null))
                                        .build();
                } catch (Exception e) {
                        throw new InternalServerErrorException(e.getMessage());
                }
        }



        @GET
        @Path("/get-payslip-employee")
        @Consumes("application/json")
        @Produces("application/pdf")
        public Response getPayslipEmployee(@QueryParam("id") String id, @QueryParam("password") String password,
                        @Context SecurityContext ctx) {
                UserEntity ue = UserEntity.find("email = ?1",
                                ctx.getUserPrincipal().getName()).firstResult();
                // EmployeeEntity employee = EmployeeEntity.find("user = ?1", ue).firstResult();
                if (!BcryptUtil.matches(password, ue.password)) {
                        return Response.status(Response.Status.BAD_REQUEST)
                                        .entity(ResponseHandler.error("Password not match")).build();
                }

                try {

                        PayrollEntity payroll = PayrollEntity.findById(id);

                        PayrollDeductionEntity payrollDed = PayrollDeductionEntity
                                        .find("payrollMaster", payroll).firstResult();
                        PayrollData data = new PayrollData();

                        data.nama = payroll.employee.nama;

                        data.jabatan = payroll.employee.jabatan;

                        data.departemen = payroll.employee.departemen;

                        // data.hariKerja = Integer.parseInt(payroll.hari_kerja);
                        // data.izin = 0;
                        // data.sakit = 0;
                        // data.alpha = 27;

                        data.gajiPokok = new BigDecimal((payroll.gaji_pokok != null) ? payroll.gaji_pokok : 0);

                        data.tjPulsa = new BigDecimal((payroll.tunjangan_pulsa != null) ? payroll.tunjangan_pulsa : 0);
                        data.tjJabatan = new BigDecimal(
                                        (payroll.tunjangan_jabatan != null) ? payroll.tunjangan_jabatan : 0);
                        data.tjOperasional = new BigDecimal(
                                        (payroll.tunjangan_operasional != null) ? payroll.tunjangan_operasional : 0);
                        data.tjTransport = new BigDecimal(
                                        (payroll.tunjangan_transport != null) ? payroll.tunjangan_transport : 0);
                        data.tjMakan = new BigDecimal((payroll.tunjangan_makan != null) ? payroll.tunjangan_makan : 0);
                        data.tjLembur = new BigDecimal(
                                        (payroll.tunjangan_lembur != null) ? payroll.tunjangan_lembur : 0);
                        data.tjLainnya = new BigDecimal(
                                        (payroll.tunjangan_lainnya != null) ? payroll.tunjangan_lainnya : 0);

                        data.bpjsKesehatanPendapatan = new BigDecimal(
                                        (payroll.bpjs_kesehatan != null) ? payroll.bpjs_kesehatan : 0);
                        data.bpjsKetenagakerjaanPendapatan = new BigDecimal(
                                        (payroll.bpjs_ketenagakerjaan != null) ? payroll.bpjs_ketenagakerjaan : 0);

                        data.totalPendapatan = new BigDecimal(
                                        (payroll.gaji_pokok == null ? 0 : payroll.gaji_pokok)
                                                        + (payroll.tunjangan_jabatan == null ? 0
                                                                        : payroll.tunjangan_jabatan)
                                                        + (payroll.tunjangan_lainnya == null ? 0
                                                                        : payroll.tunjangan_lainnya)
                                                        + (payroll.tunjangan_lembur == null ? 0
                                                                        : payroll.tunjangan_lembur)
                                                        + (payroll.tunjangan_makan == null ? 0
                                                                        : payroll.tunjangan_makan)
                                                        + (payroll.tunjangan_operasional == null ? 0
                                                                        : payroll.tunjangan_operasional)
                                                        + (payroll.tunjangan_pulsa == null ? 0
                                                                        : payroll.tunjangan_pulsa)
                                                        + (payroll.tunjangan_transport == null ? 0
                                                                        : payroll.tunjangan_transport)
                                                        + (payroll.bpjs_kesehatan == null ? 0 : payroll.bpjs_kesehatan)
                                                        + (payroll.bpjs_ketenagakerjaan == null ? 0
                                                                        : payroll.bpjs_ketenagakerjaan));

                        data.potonganKehadiran = new BigDecimal(
                                        (payrollDed.potongan_kehadiran != null) ? payrollDed.potongan_kehadiran : 0);
                        data.bpjsKesehatan = new BigDecimal((payrollDed.bpjskes != null) ? payrollDed.bpjskes : 0);

                        data.bpjsKetenagakerjaan = new BigDecimal((payrollDed.bpjstk != null) ? payrollDed.bpjstk : 0);

                        data.potonganLainnya = new BigDecimal(
                                        (payrollDed.potongan_lainnya != null) ? payrollDed.potongan_lainnya : 0);
                        data.pinjaman = new BigDecimal((payrollDed.pinjaman != null) ? payrollDed.pinjaman : 0);
                        data.pph21 = new BigDecimal((payrollDed.pph21 != null) ? payrollDed.pph21 : 0);

                        data.totalPotongan = new BigDecimal(
                                        (payrollDed.potongan_kehadiran == null ? 0 : payrollDed.potongan_kehadiran)
                                                        + (payrollDed.bpjskes == null ? 0 : payrollDed.bpjskes)
                                                        + (payrollDed.bpjstk == null ? 0 : payrollDed.bpjstk)
                                                        + (payrollDed.potongan_lainnya == null ? 0
                                                                        : payrollDed.potongan_lainnya)
                                                        + (payrollDed.pinjaman == null ? 0 : payrollDed.pinjaman)
                                                        + (payrollDed.pph21 == null ? 0 : payrollDed.pph21));

                        data.takeHomePay = new BigDecimal(
                                        ((payroll.gaji_pokok == null ? 0 : payroll.gaji_pokok)
                                                        + (payroll.tunjangan_jabatan == null ? 0
                                                                        : payroll.tunjangan_jabatan)
                                                        + (payroll.tunjangan_lainnya == null ? 0
                                                                        : payroll.tunjangan_lainnya)
                                                        + (payroll.tunjangan_lembur == null ? 0
                                                                        : payroll.tunjangan_lembur)
                                                        + (payroll.tunjangan_makan == null ? 0
                                                                        : payroll.tunjangan_makan)
                                                        + (payroll.tunjangan_operasional == null ? 0
                                                                        : payroll.tunjangan_operasional)
                                                        + (payroll.tunjangan_pulsa == null ? 0
                                                                        : payroll.tunjangan_pulsa)
                                                        + (payroll.tunjangan_transport == null ? 0
                                                                        : payroll.tunjangan_transport)
                                                        + (payroll.bpjs_kesehatan == null ? 0 : payroll.bpjs_kesehatan)
                                                        + (payroll.bpjs_ketenagakerjaan == null ? 0
                                                                        : payroll.bpjs_ketenagakerjaan))
                                                        -
                                                        ((payrollDed.potongan_kehadiran == null ? 0
                                                                        : payrollDed.potongan_kehadiran)
                                                                        + (payrollDed.bpjskes == null ? 0
                                                                                        : payrollDed.bpjskes)
                                                                        + (payrollDed.bpjstk == null ? 0
                                                                                        : payrollDed.bpjstk)
                                                                        + (payrollDed.potongan_lainnya == null ? 0
                                                                                        : payrollDed.potongan_lainnya)
                                                                        + (payrollDed.pinjaman == null ? 0
                                                                                        : payrollDed.pinjaman)
                                                                        + (payrollDed.pph21 == null ? 0
                                                                                        : payrollDed.pph21)));

                        byte[] pdf = payrollService.generate(data);

                        // return Response.ok()
                        // .entity(ResponseHandler.ok("Send Payroll Berhasil tunggu bebrapa saat",
                        // null))
                        // .build();
                        System.out.println("download");
                        InputStream imageStream = new ByteArrayInputStream(pdf);
                        return Response.ok(imageStream).build();
                } catch (Exception e) {
                        throw new InternalServerErrorException(e.getMessage());
                }
        }

}