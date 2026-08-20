package org.sim.umira.resources.HumanResources;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.configs.GoogleCalendarConfig;
import org.sim.umira.dtos.HumanResources.AttendanceDto;
import org.sim.umira.dtos.HumanResources.ClockInOutDto;
import org.sim.umira.dtos.HumanResources.PengajuanAttendanceDto;
import org.sim.umira.dtos.HumanResources.ResponseAttendanceDto;
import org.sim.umira.entities.UserEntity;

import org.sim.umira.entities.HumanResources.AttendanceEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.HumanResources.PengajuanApprovalAttendanceEntity;
import org.sim.umira.entities.HumanResources.PengajuanAttendanceEntity;
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

@Path("/HR-Attendance")
@Secured
public class AttendanceRes {

    @POST
    @Path("/create-attendance-manual")
    @Transactional
    public Response createAttendanceManual(@Valid @RequestBody AttendanceDto attendance) {

        EmployeeEntity employee = EmployeeEntity.findById(attendance.id_employee);
        AttendanceEntity checkAttendance = AttendanceEntity
                .find("employee = ?1 AND tanggal = ?2", employee, attendance.tanggal).firstResult();
        if (checkAttendance != null) {
            if (checkAttendance.jam_masuk != null && checkAttendance.jam_keluar != null) {
                throw new BadRequestException("Sudah melakukan Absensi");
            }
        }

        if (attendance.jam_keluar != null) {

            // AttendanceEntity checkAttendance = AttendanceEntity.find("employee = ?1 AND
            // tanggal = ?2", employee, attendance.tanggal).firstResult();
            Duration duration = Duration.between(attendance.jam_masuk, attendance.jam_keluar);
            Long durationWork = duration.toHours();
            if (durationWork < 8) {
                throw new BadRequestException("Jam kerja kurang");
            }
        }

        try {
            AttendanceEntity ae = new AttendanceEntity();
            ae.employee = employee;
            ae.jam_keluar = String.valueOf(attendance.jam_keluar);
            ae.jam_masuk = String.valueOf(attendance.jam_masuk);
            ae.keterangan = attendance.keterangan;
            ae.tanggal = attendance.tanggal;
            ae.status = attendance.status;
            ae.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Absensi berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @POST
    @Path("/clock-in")
    @Transactional
    public Response createAttendanceClockIn(@Valid @RequestBody ClockInOutDto attendance,
            @Context SecurityContext ctx) {

        // EmployeeEntity employee = EmployeeEntity.findById(attendance.id_employee);
        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        EmployeeEntity employee = EmployeeEntity.find("user = ?1", ue).firstResult();
        AttendanceEntity checkAttendance = AttendanceEntity
                .find("employee = ?1 AND tanggal = ?2", employee, attendance.tanggal).firstResult();
        if (checkAttendance != null) {
            if (checkAttendance.jam_masuk != null) {
                throw new BadRequestException("Sudah melakukan Clock In");
            }
        }

        AttendanceEntity ae = new AttendanceEntity();
        ae.employee = employee;
        // ae.jam_keluar = String.valueOf(attendance.jam_keluar);
        ae.jam_masuk = String.valueOf(attendance.jam_masuk);
        // ae.keterangan = attendance.keterangan;
        ae.tanggal = attendance.tanggal;
        ae.status = "Hadir";
        ae.persist();

        return Response.ok().entity(ResponseHandler.ok("Clock In berhasil", null)).build();

    }

    @POST
    @Path("/clock-out")
    @Transactional
    public Response createAttendanceClockOut(@Valid @RequestBody ClockInOutDto attendance,
            @Context SecurityContext ctx) {

        // EmployeeEntity employee = EmployeeEntity.findById(attendance.id_employee);
        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        EmployeeEntity employee = EmployeeEntity.find("user = ?1", ue).firstResult();
        AttendanceEntity checkAttendance = AttendanceEntity
                .find("employee = ?1 AND tanggal = ?2", employee, attendance.tanggal).firstResult();

        if (checkAttendance != null) {
            if (checkAttendance.jam_masuk != null && checkAttendance.jam_keluar != null) {
                throw new BadRequestException("Sudah melakukan Presensi Hari Ini");
            }
            Duration duration = Duration.between(LocalTime.parse(checkAttendance.jam_masuk), attendance.jam_keluar);
            Long durationWork = duration.toHours();
            if (durationWork < 8) {
                throw new BadRequestException("Jam kerja kurang");
            }
            checkAttendance.jam_keluar = String.valueOf(attendance.jam_keluar);
            return Response.ok().entity(ResponseHandler.ok("Clock Out berhasil", null)).build();
        } else {
            throw new BadRequestException("Belum Melakukan Clock In");
        }

    }

    @POST
    @Path("/create-pengajuan-attendance")
    @Transactional
    public Response createPengajuanAttendance(@Valid @RequestBody PengajuanAttendanceDto pengajuan,
            @Context SecurityContext ctx) {

        Set<String> set = new HashSet<>();

        for (String val : pengajuan.level_approval) {
            if (!set.add(val)) {
                throw new BadRequestException("level approval tidak boleh ada yang sama");
                // System.out.println("Duplikat ditemukan: " + val);

            }
        }

        try {
            // List<AttendanceEntity> attendance = AttendanceEntity.listAll();
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            EmployeeEntity employeeE = EmployeeEntity.find("user = ?1", ue).firstResult();
            PengajuanAttendanceEntity pengajuanAttendance = new PengajuanAttendanceEntity();
            pengajuanAttendance.employee = employeeE;
            pengajuanAttendance.jam_keluar = pengajuan.jam_keluar;
            pengajuanAttendance.jam_masuk = pengajuan.jam_masuk;
            pengajuanAttendance.status_absensi = pengajuan.status;
            pengajuanAttendance.keterangan = pengajuan.keterangan;
            pengajuanAttendance.tanggal = pengajuan.tanggal;
            pengajuanAttendance.persist();
            PengajuanApprovalAttendanceEntity pengajuanApprovalMaker = new PengajuanApprovalAttendanceEntity();
            pengajuanApprovalMaker.employee = employeeE;
            pengajuanApprovalMaker.level_approval = "Maker";
            pengajuanApprovalMaker.urutan = 0;
            pengajuanApprovalMaker.status_approval = "Pengajuan";
            pengajuanApprovalMaker.tanggal_approval = LocalDateTime.now();
            pengajuanApprovalMaker.persist();
            for (int i = 0; i < pengajuan.id_employee_approval.size(); i++) {
                EmployeeEntity employeeApproval = EmployeeEntity.findById(pengajuan.id_employee_approval.get(i));
                PengajuanApprovalAttendanceEntity pengajuanApproval = new PengajuanApprovalAttendanceEntity();
                pengajuanApproval.pengajuanAbsensi = pengajuanAttendance;
                pengajuanApproval.employee = employeeApproval;
                pengajuanApproval.level_approval = pengajuan.level_approval.get(i);
                pengajuanApproval.urutan = pengajuan.urutan.get(i);
                pengajuanApproval.persist();
            }

            return Response.ok().entity(ResponseHandler.ok("Inquiry attendance Done", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-approval-attendance")
    public Response getApprovalAttendance(@Context SecurityContext ctx) {
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            EmployeeEntity employeeApproval = EmployeeEntity.find("user = ?1", ue).firstResult();
            // System.out.println(ue.id_user);
            // PengajuanBiayaKonstruksiPersetujuanEntity pengajuan =
            // PengajuanBiayaKonstruksiPersetujuanEntity.find("id_user = ?1 AND
            // tanggal_persetujuan IS NULL ORDER BY urutan ASC ", ue.id_user).firstResult();
            List<PengajuanAttendanceEntity> listPengajuan = PengajuanAttendanceEntity.find("""
                        SELECT DISTINCT p
                        FROM PengajuanAttendanceEntity p
                        WHERE EXISTS (
                            SELECT 1
                            FROM PengajuanApprovalAttendanceEntity ps
                            WHERE ps.pengajuanAbsensi = p
                            AND ps.employee = ?1
                            AND ps.tanggal_approval IS NULL
                            AND ps.urutan = (
                                SELECT MIN(ps2.urutan)
                                FROM PengajuanApprovalAttendanceEntity ps2
                                WHERE ps2.pengajuanAbsensi = p
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
    @Path("/approval-attendance")
    @Transactional
    public Response approvalAttendance(@QueryParam("id_pengajuan_absensi") String id_pengajuan_absensi,
            @QueryParam("status_approval") String status_approval, @Context SecurityContext ctx,
            @QueryParam("catatan") String catatan) {
        if (id_pengajuan_absensi == null || id_pengajuan_absensi == "") {
            throw new BadRequestException("id_pengajuan_absensi harus Di Isi");
        }
        if (status_approval == null || status_approval == "") {
            throw new BadRequestException("status_approval harus Di Isi");
        }
        System.out.println(id_pengajuan_absensi);
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            EmployeeEntity emp = EmployeeEntity.find("user = ?1", ue).firstResult();
            PengajuanAttendanceEntity pengajuanAttendance = PengajuanAttendanceEntity.findById(id_pengajuan_absensi);
            PengajuanApprovalAttendanceEntity getPersetujuanAttendance = PengajuanApprovalAttendanceEntity
                    .find("pengajuanAbsensi = ?1 AND employee = ?2 AND tanggal_approval IS NULL ORDER BY urutan ASC",
                            pengajuanAttendance, emp)
                    .firstResult();
            if (getPersetujuanAttendance != null) {
                getPersetujuanAttendance.status_approval = status_approval;
                getPersetujuanAttendance.tanggal_approval = LocalDateTime.now();
                getPersetujuanAttendance.keterangan = (catatan != "" || catatan != null) ? catatan : "";

                if (status_approval.equals("Approve")) {
                    // System.out.println(status_approver);
                    List<PengajuanApprovalAttendanceEntity> pengajuanList = PengajuanApprovalAttendanceEntity
                            .find("tanggal_approval IS NULL AND pengajuanAbsensi = ?1", pengajuanAttendance).list();
                    if (pengajuanList.size() == 0) {
                        AttendanceEntity checkAttendanceEmployee = AttendanceEntity.find("tanggal = ?1 AND employee = ?2", pengajuanAttendance.tanggal, pengajuanAttendance.employee).firstResult();
                        if(checkAttendanceEmployee != null){
                            checkAttendanceEmployee.jam_keluar = pengajuanAttendance.jam_keluar;
                            checkAttendanceEmployee.jam_masuk = pengajuanAttendance.jam_masuk;
                            checkAttendanceEmployee.keterangan = pengajuanAttendance.keterangan;
                            checkAttendanceEmployee.status = pengajuanAttendance.status_absensi;
                        }else{
                            AttendanceEntity attendance = new AttendanceEntity();
                            attendance.employee = pengajuanAttendance.employee;
                            attendance.tanggal = pengajuanAttendance.tanggal;
                            attendance.jam_keluar = pengajuanAttendance.jam_keluar;
                            attendance.jam_masuk = pengajuanAttendance.jam_masuk;
                            attendance.keterangan = pengajuanAttendance.keterangan;
                            attendance.status = pengajuanAttendance.status_absensi;
                            attendance.persist();
                        }
                        

                    }
                } else if (status_approval.equals("Reject")) {
                    List<PengajuanApprovalAttendanceEntity> getPersetujuanReject = PengajuanApprovalAttendanceEntity
                            .find("pengajuanAbsensi = ?1 AND tanggal_approval IS NULL ORDER BY urutan ASC",
                                    pengajuanAttendance)
                            .list();
                    for (PengajuanApprovalAttendanceEntity pengajuanReject : getPersetujuanReject) {
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
    @Path("/get-monitoring-approval-attendance")
    public Response getMonitoringApprovalAttendance(@Context SecurityContext ctx) {
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            EmployeeEntity employeeApproval = EmployeeEntity.find("user = ?1", ue).firstResult();
            List<PengajuanAttendanceEntity> listPengajuan;
            // System.out.println(ue.id_user);
            // PengajuanBiayaKonstruksiPersetujuanEntity pengajuan =
            // PengajuanBiayaKonstruksiPersetujuanEntity.find("id_user = ?1 AND
            // tanggal_persetujuan IS NULL ORDER BY urutan ASC ", ue.id_user).firstResult();
            if (ue.role.kode_role == "99") {
                listPengajuan = PengajuanAttendanceEntity
                        .find("SELECT DISTINCT p FROM PengajuanAttendanceEntity p JOIN p.approval r JOIN p.employee pr")
                        .list();
            } else {
                listPengajuan = PengajuanAttendanceEntity.find("""
                            SELECT DISTINCT p
                            FROM PengajuanAttendanceEntity p
                            WHERE EXISTS (
                                SELECT 1
                                FROM PengajuanApprovalAttendanceEntity ps
                                WHERE ps.pengajuanAbsensi = p
                                AND ps.employee = ?1
                            )
                        """, employeeApproval).list();
            }

            // List<PengajuanBiayaKonstruksiEntity> listPengajuan =
            // PengajuanBiayaKonstruksiEntity.listAll();

            return Response.ok().entity(ResponseHandler.ok("Data Tersedia", listPengajuan)).build();

        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-attendance")
    public Response getAttendance() {
        try {
            List<AttendanceEntity> attendance = AttendanceEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Inquiry attendance Done", attendance)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @ConfigProperty(name = "date-close-book")
    String tanggal_pembukuan;

    @GET
    @Path("/get-attendance-user")
    public Response getAttendanceLastMonth(@QueryParam("month") String month, @QueryParam("year") String year,
            @Context SecurityContext ctx) {
        String holidayCalendarId = "id.indonesian#holiday@group.v.calendar.google.com";
        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        EmployeeEntity emp = EmployeeEntity.find("user = ?1", ue).firstResult();
        try {
           
            int monthInt = Integer.parseInt(month);
            Month monthM = Month.of(monthInt);
            YearMonth ym = YearMonth.of(Integer.parseInt(year), monthM);
            Boolean saturdayOff = true;
            Integer is_office = emp.klasifikasi_works.is_office;
            if(is_office == 1){
                saturdayOff = false;
            }

            // LocalDate startDate = ym.atDay(1);
            LocalDate startDate = ym.minusMonths(1).atDay(Integer.parseInt(tanggal_pembukuan) + 1);
            LocalDate endDate = ym.atDay(Math.min(Integer.parseInt(tanggal_pembukuan), ym.lengthOfMonth()));
            

            Calendar service = GoogleCalendarConfig.getService();

            // 1. ambil libur nasional
            Set<LocalDate> holidays = YearCalendarService.getHolidaysByParams(service, holidayCalendarId,
                    startDate.toString(), endDate.toString());

            // 2. generate 1 tahun
            List<YearCalendarService.DayInfo> calendar = YearCalendarService.generatedDay(Integer.parseInt(year), holidays,
                    startDate.toString(), endDate.toString(), saturdayOff);
            List<ResponseAttendanceDto> result = new ArrayList<>();
            for (YearCalendarService.DayInfo g : calendar) {
                AttendanceEntity ae = AttendanceEntity.find("tanggal = ?1 AND employee = ?2", g.date, emp)
                        .firstResult();
                result.add(new ResponseAttendanceDto(g.date, g.type, g.status, ae));
            }

            return Response.ok().entity(ResponseHandler.ok("Inquiry attendance Done", result)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @DELETE
    @Path("/delete-attendance")
    @Transactional
    public Response deleteAttendance(@QueryParam("id") String id) {
        try {
            Boolean attendance = AttendanceEntity.deleteById(id);
            return Response.ok().entity(ResponseHandler.ok("Delete attendance Done", attendance)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
}
