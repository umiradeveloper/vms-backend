package org.sim.umira.resources.HumanResources;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.HumanResources.AttendanceDto;
import org.sim.umira.entities.HumanResources.AttendanceEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/HR-Attendance")
@Secured
public class AttendanceRes {

    @POST
    @Path("/create-attendance-manual")
    @Transactional
    public Response createAttendanceManual(@Valid @RequestBody AttendanceDto attendance){
        
        EmployeeEntity employee = EmployeeEntity.findById(attendance.id_employee);
        AttendanceEntity checkAttendance = AttendanceEntity.find("employee = ?1 AND tanggal = ?2", employee, attendance.tanggal).firstResult();
        if(checkAttendance != null){
            if(checkAttendance.jam_masuk != null && checkAttendance.jam_keluar != null){
                throw new BadRequestException("Sudah melakukan Absensi");
            }
        }
        
        if(attendance.jam_keluar != null){
           
            
            // AttendanceEntity checkAttendance = AttendanceEntity.find("employee = ?1 AND tanggal = ?2", employee, attendance.tanggal).firstResult();
            Duration duration = Duration.between(attendance.jam_masuk, attendance.jam_keluar);
            Long durationWork = duration.toHours();
            if(durationWork < 8){
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

    @GET
    @Path("/get-attendance")
    public Response getAttendance(){
        try {
            List<AttendanceEntity> attendance = AttendanceEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Inquiry attendance Done", attendance)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @DELETE
    @Path("/delete-attendance")
    @Transactional
    public Response deleteAttendance(@QueryParam("id") String id){
        try {
            Boolean attendance = AttendanceEntity.deleteById(id);
            return Response.ok().entity(ResponseHandler.ok("Delete attendance Done", attendance)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
}
