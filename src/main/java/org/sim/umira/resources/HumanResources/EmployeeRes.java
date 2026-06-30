package org.sim.umira.resources.HumanResources;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.HumanResources.EmployeeDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.HumanResources.KlasifikasiWorkEntity;
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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/HR-Employee")
@Secured
public class EmployeeRes {

    @POST
    @Path("/create-employee")
    @Transactional
    public Response createEmployee(@Valid @RequestBody EmployeeDto create, @Context SecurityContext ctx){
        UserEntity ue = UserEntity.findById(create.id_user);
        if(ue == null){
            throw new BadRequestException("User tidak di temukan");
        }
        EmployeeEntity empCheck = EmployeeEntity.find("user = ?1", ue).firstResult();
        if(empCheck != null){
             throw new BadRequestException("User sudah di daftarkan");
        }
        KlasifikasiWorkEntity klasifikasi = KlasifikasiWorkEntity.find("klasifikasi_works = ?1", create.klasifikasi_works).firstResult();
        try {
            EmployeeEntity employee = new EmployeeEntity();
            employee.nama = create.nama;
            employee.user = ue;
            employee.departemen = create.departemen;
            employee.jabatan = create.jabatan;
            employee.nip = create.nip;
            employee.email = create.email;
            employee.no_hp = create.no_hp;
            employee.tmt = create.tmt;
            employee.status_karyawan = create.status_karyawan;
            employee.tanggal_lahir = create.tanggal_lahir;
            employee.tempat_lahir = create.tempat_lahir;
            employee.alamat = create.alamat;
            employee.npwp = create.npwp;
            employee.ptkp_status = create.ptkp_status;
            employee.bank_name = create.bank_name;
            employee.bank_account = create.bank_account;
            employee.bpjs_ketenagakerjaan = create.bpjs_ketenagakerjaan;
            employee.bpjs_kesehatan = create.bpjs_kesehatan;
            employee.nik = create.nik;
            employee.jenis_kelamin = create.jenis_kelamin;
            employee.marital_status = create.marital_status;
            employee.blood_type = create.blood_type;
            employee.grade = create.grade;
            employee.kelas = create.kelas;
            employee.klasifikasi_works = klasifikasi;
            employee.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Employee berhasil", null)).build();
        } catch (Exception e) {
            // System.err.println(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }

     @POST
    @Path("/update-employee")
    @Transactional
    public Response updateEmployee(@Valid @RequestBody EmployeeDto create, @Context SecurityContext ctx){
        UserEntity ue = UserEntity.findById(create.id_user);
        if(ue == null){
            throw new BadRequestException("User tidak di temukan");
        }
        KlasifikasiWorkEntity klasifikasi = KlasifikasiWorkEntity.find("klasifikasi_works = ?1", create.klasifikasi_works).firstResult();
        try {
            EmployeeEntity employee = EmployeeEntity.findById(create.id_employee);
            employee.nama = create.nama;
            employee.user = ue;
            employee.departemen = create.departemen;
            employee.jabatan = create.jabatan;
            employee.nip = create.nip;
            employee.email = create.email;
            employee.no_hp = create.no_hp;
            employee.tmt = create.tmt;
            employee.status_karyawan = create.status_karyawan;
            employee.tanggal_lahir = create.tanggal_lahir;
            employee.tempat_lahir = create.tempat_lahir;
            employee.alamat = create.alamat;
            employee.npwp = create.npwp;
            employee.ptkp_status = create.ptkp_status;
            employee.bank_name = create.bank_name;
            employee.bank_account = create.bank_account;
            employee.bpjs_ketenagakerjaan = create.bpjs_ketenagakerjaan;
            employee.bpjs_kesehatan = create.bpjs_kesehatan;
            employee.nik = create.nik;
            employee.jenis_kelamin = create.jenis_kelamin;
            employee.marital_status = create.marital_status;
            employee.blood_type = create.blood_type;
            employee.grade = create.grade;
            employee.kelas = create.kelas;
            employee.klasifikasi_works = klasifikasi;
            // employee.persist();
            return Response.ok().entity(ResponseHandler.ok("Update Employee berhasil", null)).build();
        } catch (Exception e) {
            // System.err.println(e.getMessage());
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    @GET
    @Path("/get-employee")
    public Response getEmployee(){
        try {
            List<EmployeeEntity> employee = EmployeeEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Inquiry Employee Done", employee)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }


    @DELETE
    @Path("/delete-employee")
    @Transactional
    public Response deleteEmployee(@QueryParam("id") String id){
        try {
            Boolean delete = EmployeeEntity.deleteById(id);
            return Response.ok().entity(ResponseHandler.ok("Delete Employee Done", delete)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
}
