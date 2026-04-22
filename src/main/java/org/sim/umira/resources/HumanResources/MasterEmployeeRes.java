package org.sim.umira.resources.HumanResources;

import java.util.List;

import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.HumanResources.MasterGradeKelasEntity;
import org.sim.umira.entities.HumanResources.MasterJenisKelaminEntity;
import org.sim.umira.entities.HumanResources.MasterMaritalStatusEntity;
import org.sim.umira.entities.HumanResources.MasterPtkpEntity;
import org.sim.umira.entities.HumanResources.MasterStatusKaryawanEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/Master-employee")
@Secured
public class MasterEmployeeRes {
    @GET
    @Path("/get-master-grade-class")
    public Response getMasterGradeKelas(){
        try {
            List<MasterGradeKelasEntity> datas = MasterGradeKelasEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Inquiry Grade Done", datas)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-master-jenis-kelamin")
    public Response getJenisKelamin(){
        try {
            List<MasterJenisKelaminEntity> datas = MasterJenisKelaminEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Inquiry Jenis Kelamin Done", datas)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-master-marital-status")
    public Response getMaritalStatus(){
        try {
            List<MasterMaritalStatusEntity> datas = MasterMaritalStatusEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Inquiry Marital Status Done", datas)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-master-ptkp")
    public Response getPtkpStatus(){
        try {
            List<MasterPtkpEntity> datas = MasterPtkpEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Inquiry ptkp Done", datas)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-master-status-karyawan")
    public Response getStatusKaryawan(){
        try {
            List<MasterStatusKaryawanEntity> datas = MasterStatusKaryawanEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Inquiry status karyawan Done", datas)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

     @GET
    @Path("/get-master-status-absensi")
    public Response getStatusAbsensi(){
        try {
            List<String> datas = List.of("Hadir", "Izin", "Sakit", "Alpha");
            return Response.ok().entity(ResponseHandler.ok("Inquiry status Absensi", datas)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
    @GET
    @Path("/get-master-level-approval")
    public Response getLevelApproval(){
        try {
            List<String> datas = List.of("1|Checker", "2|Signer");
            return Response.ok().entity(ResponseHandler.ok("Inquiry level approval", datas)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
}
