package org.sim.umira.resources.HumanResources;

import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.configs.GoogleCalendarConfig;
import org.sim.umira.dtos.HumanResources.OvertimeDto;
import org.sim.umira.dtos.HumanResources.PengajuanOvertimeDto;
import org.sim.umira.dtos.HumanResources.PengajuanOvertimeMultipartDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.HumanResources.AttendanceEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.HumanResources.OvertimeEntity;
import org.sim.umira.entities.HumanResources.PengajuanApprovalAttendanceEntity;
import org.sim.umira.entities.HumanResources.PengajuanApprovalOvertimeEntity;
import org.sim.umira.entities.HumanResources.PengajuanAttendanceEntity;
import org.sim.umira.entities.HumanResources.PengajuanOvertimeEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;
import org.sim.umira.services.YearCalendarService;

import com.google.api.services.calendar.Calendar;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/HR-Overtime")
@Secured
public class OvertimeRes {

    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/overtime");

    @ConfigProperty(name = "date-close-book")
    String tanggal_pembukuan;

    @ConfigProperty(name = "overtime-max")
    String overtime_max;

    @POST
    @Path("/create-overtime")
    @Transactional
    public Response createOvertime(@Valid @RequestBody OvertimeDto overtime) {

        EmployeeEntity emp = EmployeeEntity.findById(overtime.id_employee);
        if (emp == null) {
            throw new BadRequestException("Employee tidak terdaftar");
        }
        Duration duration = Duration.between(LocalTime.parse(overtime.jam_mulai),
                LocalTime.parse(overtime.jam_selesai));
        Long durationWork = duration.toMinutes();
        int hoursNow = Integer.parseInt(String.valueOf(durationWork)) / 60;

        LocalDate tanggal = overtime.tanggal;

        YearMonth periode = getPeriode(tanggal, Integer.parseInt(tanggal_pembukuan));

        // System.out.println("periode "+overtime.tanggal);
        int monthInt = periode.getMonthValue();
        Month monthM = Month.of(monthInt);
        YearMonth ym = YearMonth.of(tanggal.getYear(), monthM);

        // LocalDate startDate = ym.atDay(1);
        LocalDate startDate = ym.minusMonths(1).atDay(Integer.parseInt(tanggal_pembukuan) + 1);
        LocalDate endDate = ym.atDay(Math.min(Integer.parseInt(tanggal_pembukuan), ym.lengthOfMonth()));

        Integer totalOvertime = 0;
        System.out.println(startDate);
        System.out.println(endDate);

        List<OvertimeEntity> overtimeEmp = OvertimeEntity
                .find("employee = ?1 AND tanggal BETWEEN ?2 AND ?3", emp, startDate, endDate).list();
        for (OvertimeEntity ov : overtimeEmp) {
            totalOvertime += Integer.parseInt(ov.durasi);
        }
        int hours = totalOvertime / 60;

        if ((hours + hoursNow) > Integer.parseInt(overtime_max)) {
            throw new BadRequestException("Overtime Melebihi Limit");
        }

        try {

            OvertimeEntity ov = new OvertimeEntity();
            ov.employee = emp;
            ov.durasi = String.valueOf(durationWork);
            ov.jam_mulai = overtime.jam_mulai;
            ov.jam_selesai = overtime.jam_selesai;
            ov.alasan = overtime.alasan;
            ov.tanggal = overtime.tanggal;
            ov.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Overtime Success", null)).build();

        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
    public YearMonth getPeriode(LocalDate tanggal, int tanggalPembukuan) {
        System.out.println("tanggal "+tanggal.getDayOfMonth());
        if (tanggal.getDayOfMonth() > tanggalPembukuan) {
            return YearMonth.from(tanggal).plusMonths(1);
        }

        return YearMonth.from(tanggal);
    }

    @POST
    @Path("/create-pengajuan-overtime")
    @Transactional
    public Response createPengajuanOvertime(@Valid @RequestBody PengajuanOvertimeDto pengajuan,
            @Context SecurityContext ctx) {

        Set<String> set = new HashSet<>();

        for (String val : pengajuan.level_approval) {
            if (!set.add(val)) {
                throw new BadRequestException("level approval tidak boleh ada yang sama");
                // System.out.println("Duplikat ditemukan: " + val);

            }
        }
        EmployeeEntity emp = EmployeeEntity.findById(pengajuan.id_employee);
        if (emp == null) {
            throw new BadRequestException("Employee tidak terdaftar");
        }
         Duration duration = Duration.between(LocalTime.parse(pengajuan.jam_mulai),
                LocalTime.parse(pengajuan.jam_selesai));
        Long durationWork = duration.toMinutes();
        int hoursNow = Integer.parseInt(String.valueOf(durationWork)) / 60;

        LocalDate tanggal = pengajuan.tanggal;

        YearMonth periode = getPeriode(tanggal, Integer.parseInt(tanggal_pembukuan));

        // System.out.println("periode "+overtime.tanggal);
        int monthInt = periode.getMonthValue();
        Month monthM = Month.of(monthInt);
        YearMonth ym = YearMonth.of(tanggal.getYear(), monthM);

        // LocalDate startDate = ym.atDay(1);
        LocalDate startDate = ym.minusMonths(1).atDay(Integer.parseInt(tanggal_pembukuan) + 1);
        LocalDate endDate = ym.atDay(Math.min(Integer.parseInt(tanggal_pembukuan), ym.lengthOfMonth()));

        Integer totalOvertime = 0;
        System.out.println(startDate);
        System.out.println(endDate);

        List<OvertimeEntity> overtimeEmp = OvertimeEntity
                .find("employee = ?1 AND tanggal BETWEEN ?2 AND ?3", emp, startDate, endDate).list();
        for (OvertimeEntity ov : overtimeEmp) {
            totalOvertime += Integer.parseInt(ov.durasi);
        }
        int hours = totalOvertime / 60;

        if ((hours + hoursNow) > Integer.parseInt(overtime_max)) {
            throw new BadRequestException("Overtime Melebihi Limit");
        }

        try {
           
            // Duration duration = Duration.between(LocalTime.parse(pengajuan.jam_mulai),
            //         LocalTime.parse(pengajuan.jam_selesai));
            // Long durationWork = duration.toMinutes();
            PengajuanOvertimeEntity pengajuanOvertime = new PengajuanOvertimeEntity();
            pengajuanOvertime.employee = emp;
            pengajuanOvertime.jam_mulai = pengajuan.jam_mulai;
            pengajuanOvertime.jam_selesai = pengajuan.jam_selesai;
            pengajuanOvertime.tanggal = pengajuan.tanggal;
            pengajuanOvertime.durasi = String.valueOf(durationWork);
            pengajuanOvertime.alasan = pengajuan.alasan;

            pengajuanOvertime.persist();
            PengajuanApprovalOvertimeEntity pengajuanApprovalOvertimeMaker = new PengajuanApprovalOvertimeEntity();
            pengajuanApprovalOvertimeMaker.employee = emp;
            pengajuanApprovalOvertimeMaker.pengajuanOvertime = pengajuanOvertime;
            pengajuanApprovalOvertimeMaker.level_approval = "Maker";
            pengajuanApprovalOvertimeMaker.status_approval = "Pengajuan";
            pengajuanApprovalOvertimeMaker.urutan = 0;
            pengajuanApprovalOvertimeMaker.tanggal_approval = LocalDateTime.now();
            pengajuanApprovalOvertimeMaker.persist();
            for (int i = 0; i < pengajuan.id_employee_approval.size(); i++) {
                EmployeeEntity empApproval = EmployeeEntity.findById(pengajuan.id_employee_approval.get(i));
                PengajuanApprovalOvertimeEntity pengajuanApprovalOvertime = new PengajuanApprovalOvertimeEntity();
                pengajuanApprovalOvertime.employee = empApproval;
                pengajuanApprovalOvertime.pengajuanOvertime = pengajuanOvertime;
                pengajuanApprovalOvertime.level_approval = pengajuan.level_approval.get(i);
                pengajuanApprovalOvertime.urutan = pengajuan.urutan.get(i);
                pengajuanApprovalOvertime.persist();
            }
            return Response.ok().entity(ResponseHandler.ok("Create Overtime Success", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @POST
    @Path("/create-pengajuan-overtime-multipart")
    @Transactional
    public Response createPengajuanOvertimeMultipart(
            @MultipartForm @Valid @RequestBody PengajuanOvertimeMultipartDto pengajuan, @Context SecurityContext ctx) {

        Set<String> set = new HashSet<>();

        for (String val : pengajuan.level_approval) {
            if (!set.add(val)) {
                throw new BadRequestException("level approval tidak boleh ada yang sama");
                // System.out.println("Duplikat ditemukan: " + val);

            }
        }
        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            EmployeeEntity emp = EmployeeEntity.find("user = ?1", ue).firstResult();

        Duration duration = Duration.between(LocalTime.parse(pengajuan.jam_mulai),
                LocalTime.parse(pengajuan.jam_selesai));
        Long durationWork = duration.toMinutes();
        int hoursNow = Integer.parseInt(String.valueOf(durationWork)) / 60;

        LocalDate tanggal = pengajuan.tanggal;

        YearMonth periode = getPeriode(tanggal, Integer.parseInt(tanggal_pembukuan));

        // System.out.println("periode "+overtime.tanggal);
        int monthInt = periode.getMonthValue();
        Month monthM = Month.of(monthInt);
        YearMonth ym = YearMonth.of(tanggal.getYear(), monthM);

        // LocalDate startDate = ym.atDay(1);
        LocalDate startDate = ym.minusMonths(1).atDay(Integer.parseInt(tanggal_pembukuan) + 1);
        LocalDate endDate = ym.atDay(Math.min(Integer.parseInt(tanggal_pembukuan), ym.lengthOfMonth()));

        Integer totalOvertime = 0;
        System.out.println(startDate);
        System.out.println(endDate);

        List<OvertimeEntity> overtimeEmp = OvertimeEntity
                .find("employee = ?1 AND tanggal BETWEEN ?2 AND ?3", emp, startDate, endDate).list();
        for (OvertimeEntity ov : overtimeEmp) {
            totalOvertime += Integer.parseInt(ov.durasi);
        }
        int hours = totalOvertime / 60;

        if ((hours + hoursNow) > Integer.parseInt(overtime_max)) {
            throw new BadRequestException("Overtime Melebihi Limit");
        }

        try {
            // UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            // EmployeeEntity emp = EmployeeEntity.find("user = ?1", ue).firstResult();
            // Duration duration = Duration.between(LocalTime.parse(pengajuan.jam_mulai),
            // LocalTime.parse(pengajuan.jam_selesai));
            // Long durationWork = duration.toMinutes();
            PengajuanOvertimeEntity pengajuanOvertime = new PengajuanOvertimeEntity();
            pengajuanOvertime.employee = emp;
            pengajuanOvertime.jam_mulai = pengajuan.jam_mulai;
            pengajuanOvertime.jam_selesai = pengajuan.jam_selesai;
            pengajuanOvertime.tanggal = pengajuan.tanggal;
            pengajuanOvertime.durasi = pengajuan.durasi;
            if (pengajuan.dokumen != null) {
                String ext = pengajuan.dokumen.fileName().substring(pengajuan.dokumen.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                        pengajuan.dokumen.uploadedFile(),
                        target,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                pengajuanOvertime.dokumen = target.toString();
            }
            pengajuanOvertime.alasan = pengajuan.alasan;
            pengajuanOvertime.persist();
            PengajuanApprovalOvertimeEntity pengajuanApprovalOvertimeMaker = new PengajuanApprovalOvertimeEntity();
            pengajuanApprovalOvertimeMaker.employee = emp;
            pengajuanApprovalOvertimeMaker.pengajuanOvertime = pengajuanOvertime;
            pengajuanApprovalOvertimeMaker.level_approval = "Maker";
            pengajuanApprovalOvertimeMaker.status_approval = "Pengajuan";
            pengajuanApprovalOvertimeMaker.urutan = 0;
            pengajuanApprovalOvertimeMaker.tanggal_approval = LocalDateTime.now();
            pengajuanApprovalOvertimeMaker.persist();
            for (int i = 0; i < pengajuan.id_employee_approval.size(); i++) {
                EmployeeEntity empApproval = EmployeeEntity.findById(pengajuan.id_employee_approval.get(i));
                PengajuanApprovalOvertimeEntity pengajuanApprovalOvertime = new PengajuanApprovalOvertimeEntity();
                pengajuanApprovalOvertime.employee = empApproval;
                pengajuanApprovalOvertime.pengajuanOvertime = pengajuanOvertime;
                pengajuanApprovalOvertime.level_approval = pengajuan.level_approval.get(i);
                pengajuanApprovalOvertime.urutan = pengajuan.urutan.get(i);
                pengajuanApprovalOvertime.persist();
            }
            return Response.ok().entity(ResponseHandler.ok("Create Overtime Success", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-approval-overtime")
    public Response getApprovalOvertime(@Context SecurityContext ctx) {
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            EmployeeEntity employeeApproval = EmployeeEntity.find("user = ?1", ue).firstResult();
            // System.out.println(ue.id_user);
            // PengajuanBiayaKonstruksiPersetujuanEntity pengajuan =
            // PengajuanBiayaKonstruksiPersetujuanEntity.find("id_user = ?1 AND
            // tanggal_persetujuan IS NULL ORDER BY urutan ASC ", ue.id_user).firstResult();
            List<PengajuanOvertimeEntity> listPengajuan = PengajuanOvertimeEntity.find("""
                        SELECT DISTINCT p
                        FROM PengajuanOvertimeEntity p
                        WHERE EXISTS (
                            SELECT 1
                            FROM PengajuanApprovalOvertimeEntity ps
                            WHERE ps.pengajuanOvertime = p
                            AND ps.employee = ?1
                            AND ps.tanggal_approval IS NULL
                            AND ps.urutan = (
                                SELECT MIN(ps2.urutan)
                                FROM PengajuanApprovalOvertimeEntity ps2
                                WHERE ps2.pengajuanOvertime = p
                                    AND ps2.tanggal_approval IS NULL
                            )
                        )
                    """, employeeApproval).list();
            // List<PengajuanBiayaKonstruksiEntity> listPengajuan =
            // PengajuanBiayaKonstruksiEntity.listAll();

            return Response.ok().entity(ResponseHandler.ok("Data Tersedia", listPengajuan)).build();

        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-montoring-approval-overtime")
    public Response getMonitoringOvertime(@Context SecurityContext ctx) {
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            EmployeeEntity employeeApproval = EmployeeEntity.find("user = ?1", ue).firstResult();
            // System.out.println(ue.id_user);
            // PengajuanBiayaKonstruksiPersetujuanEntity pengajuan =
            // PengajuanBiayaKonstruksiPersetujuanEntity.find("id_user = ?1 AND
            // tanggal_persetujuan IS NULL ORDER BY urutan ASC ", ue.id_user).firstResult();
            List<PengajuanOvertimeEntity> listPengajuan;
            // if (ue.role.kode_role == "99") {
            //     listPengajuan = PengajuanOvertimeEntity
            //             .find("SELECT DISTINCT p FROM PengajuanOvertimeEntity p JOIN p.approval r JOIN p.employee pr")
            //             .list();
            // } else {
            //     listPengajuan = PengajuanOvertimeEntity.find("""
            //                 SELECT DISTINCT p
            //                 FROM PengajuanOvertimeEntity p
            //                 WHERE EXISTS (
            //                     SELECT 1
            //                     FROM PengajuanApprovalOvertimeEntity ps
            //                     WHERE ps.pengajuanOvertime = p
            //                     AND ps.employee = ?1
            //                 )
            //             """, employeeApproval).list();
            // }

            listPengajuan = PengajuanOvertimeEntity.find("""
                            SELECT DISTINCT p
                            FROM PengajuanOvertimeEntity p
                            WHERE EXISTS (
                                SELECT 1
                                FROM PengajuanApprovalOvertimeEntity ps
                                WHERE ps.pengajuanOvertime = p
                                AND ps.employee = ?1
                            )
                        """, employeeApproval).list();
            // List<PengajuanBiayaKonstruksiEntity> listPengajuan =
            // PengajuanBiayaKonstruksiEntity.listAll();

            return Response.ok().entity(ResponseHandler.ok("Data Tersedia", listPengajuan)).build();

        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/approval-overtime")
    @Transactional
    public Response approvalAttendance(@QueryParam("id_pengajuan_lembur") String id_pengajuan_lembur,
            @QueryParam("status_approval") String status_approval, @Context SecurityContext ctx,
            @QueryParam("catatan") String catatan) {
        if (id_pengajuan_lembur == null || id_pengajuan_lembur == "") {
            throw new BadRequestException("id_pengajuan_lembur harus Di Isi");
        }
        if (status_approval == null || status_approval == "") {
            throw new BadRequestException("status_approval harus Di Isi");
        }
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            EmployeeEntity emp = EmployeeEntity.find("user = ?1", ue).firstResult();
            PengajuanOvertimeEntity pengajuanOvertime = PengajuanOvertimeEntity.findById(id_pengajuan_lembur);
            PengajuanApprovalOvertimeEntity getPersetujuanOvertime = PengajuanApprovalOvertimeEntity
                    .find("pengajuanOvertime = ?1 AND employee = ?2 AND tanggal_approval IS NULL ORDER BY urutan ASC",
                            pengajuanOvertime, emp)
                    .firstResult();
            if (getPersetujuanOvertime != null) {
                getPersetujuanOvertime.status_approval = status_approval;
                getPersetujuanOvertime.tanggal_approval = LocalDateTime.now();
                getPersetujuanOvertime.keterangan = (catatan != "" || catatan != null) ? catatan : "";

                if (status_approval.equals("Approve")) {
                    // System.out.println(status_approver);
                    List<PengajuanApprovalOvertimeEntity> pengajuanList = PengajuanApprovalOvertimeEntity
                            .find("tanggal_approval IS NULL AND pengajuanOvertime = ?1", pengajuanOvertime).list();
                    if (pengajuanList.size() == 0) {
                        OvertimeEntity ov = new OvertimeEntity();
                        ov.employee = pengajuanOvertime.employee;
                        ov.tanggal = pengajuanOvertime.tanggal;
                        ov.jam_selesai = pengajuanOvertime.jam_selesai;
                        ov.jam_mulai = pengajuanOvertime.jam_mulai;
                        ov.alasan = pengajuanOvertime.alasan;
                        ov.durasi = pengajuanOvertime.durasi;
                        ov.persist();

                    }
                } else if (status_approval.equals("Reject")) {
                    List<PengajuanApprovalOvertimeEntity> getPersetujuanReject = PengajuanApprovalOvertimeEntity
                            .find("pengajuanOvertime = ?1 AND tanggal_approval IS NULL ORDER BY urutan ASC",
                                    pengajuanOvertime)
                            .list();
                    for (PengajuanApprovalOvertimeEntity pengajuanReject : getPersetujuanReject) {
                        pengajuanReject.tanggal_approval = LocalDateTime.now();
                        pengajuanReject.status_approval = "Reject";
                        pengajuanReject.keterangan = "Rejected By " + ue.username;
                    }

                }

                return Response.ok().entity(ResponseHandler.ok("Approver Berhasil", null)).build();
            } else {
                return Response.ok().entity(ResponseHandler.error("Data Persetujuan tidak ada")).build();
            }

        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-overtime")
    public Response getOvertime() {
        try {
            // List<OvertimeEntity> list = OvertimeEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Create Overtime Success", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @DELETE
    @Path("/delete-overtime")
    @Transactional
    public Response deleteOvertime(@QueryParam("id") String id) {
        try {
            Boolean delete = OvertimeEntity.deleteById(id);
            return Response.ok().entity(ResponseHandler.ok("Delete Overtime Success", delete)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
}
