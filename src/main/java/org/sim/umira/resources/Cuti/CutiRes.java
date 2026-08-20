package org.sim.umira.resources.Cuti;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.Cuti.CreateCutiDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.Cuti.CutiEntity;
import org.sim.umira.entities.Cuti.SaldoCutiEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.HumanResources.MasterCounterCutiEntity;
import org.sim.umira.entities.Reimbursement.ReimbursementEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/Cuti")
@Secured
public class CutiRes {

    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/dokumen-cuti");

    @POST
    @Path("/create-cuti")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createCuti(
            @Valid @MultipartForm CreateCutiDto create, @Context SecurityContext ctx) {

        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        // EmployeeEntity emp = EmployeeEntity.find("id_user = ?1",
        // ue.id_user).firstResult();
        
        EmployeeEntity emp = EmployeeEntity.find("user = ?1", ue).firstResult();
        String years = String.valueOf(Year.now().getValue());

        MasterCounterCutiEntity getCount = MasterCounterCutiEntity.find("year = ?1 AND jenis_cuti = ?2",years, create.kode_cuti).firstResult();
        String id_cuti = "";
        if(getCount != null){
            getCount.counter = getCount.counter + 1;
            id_cuti = create.kode_cuti+"-"+years+String.format("%5s", getCount.counter + 1).replace(' ', '0');
        }else{
            MasterCounterCutiEntity countMaster = new MasterCounterCutiEntity();
            countMaster.jenis_cuti = create.kode_cuti;
            countMaster.year = years;
            countMaster.counter = 1;
            countMaster.persist();
            id_cuti = create.kode_cuti+"-"+years+String.format("%5s",  1).replace(' ', '0');
        }
        

        if ("ANNUAL_LEAVE".equals(create.jenis_cuti)) {
            int tahun = java.time.LocalDate.now().getYear();

            SaldoCutiEntity balance = SaldoCutiEntity.findByUserAndTahun(ue.id_user, tahun);
            if (balance == null) {
                balance = new SaldoCutiEntity();
                balance.id_user = ue.id_user;
                // balance.employee_pengajuan = emp;
                balance.tahun = tahun;
                balance.sisa_cuti = 12;
                balance.used_cuti = 0;
                balance.created_at = LocalDateTime.now();
                balance.persist();
            }

            long totalDays = create.tanggal_mulai.datesUntil(create.tanggal_selesai.plusDays(1))
                    .filter(d -> d.getDayOfWeek() != java.time.DayOfWeek.SATURDAY
                            && d.getDayOfWeek() != java.time.DayOfWeek.SUNDAY)
                    .count();

            if (balance.sisa_cuti < totalDays) {
                throw new BadRequestException(
                        "Sisa cuti tidak mencukupi. Sisa: " + balance.sisa_cuti + " hari, Dibutuhkan: " + totalDays
                                + " hari");
            }
        }
        try {
            CutiEntity cuti = new CutiEntity();

            if (create.dokumen_upload != null && create.dokumen_upload.size() > 0) {
                String ext = create.dokumen_upload.fileName()
                        .substring(create.dokumen_upload.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                        create.dokumen_upload.uploadedFile(),
                        target,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                cuti.dokumen_cuti = target.toString();
            }

            cuti.employee_pengajuan = emp;
            cuti.jenis_cuti = create.jenis_cuti;
            cuti.tanggal_mulai = create.tanggal_mulai;
            cuti.tanggal_selesai = create.tanggal_selesai;
            cuti.alasan_cuti = create.alasan_cuti;
            cuti.id_delegasi = create.id_delegasi;
            cuti.status_cuti = "PENDING";
            cuti.created_at = LocalDateTime.now();
            cuti.created_by = ue.id_user;
            cuti.kode_cuti = id_cuti;
            // cuti.id_approver = create.id_approver;
            cuti.persist();

            return Response.ok().entity(ResponseHandler.ok("Create Cuti Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-cuti-balance")
    // @Transactional
    public Response getCutiBalance(@Context SecurityContext ctx) {
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            int tahun = java.time.LocalDate.now().getYear();
            SaldoCutiEntity balance = SaldoCutiEntity.findByUserAndTahun(ue.id_user, tahun);
            if (balance == null) {
                return Response.ok().entity(ResponseHandler.ok("Get Balance Berhasil",
                        java.util.Map.of("sisa_cuti", 12, "used_cuti", 0, "tahun", tahun))).build();
            }
            return Response.ok().entity(ResponseHandler.ok("Get Balance Berhasil", balance)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-cuti-by-user")
    // @Transactional
    public Response getCutiByUser(@Context SecurityContext ctx) {
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            EmployeeEntity emp = EmployeeEntity.find("user = ?1", ue).firstResult();
            List<CutiEntity> cutiList = CutiEntity.find("employee_pengajuan = ?1", emp).list();
            // List<CutiEntity> cutiList = CutiEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Get Cuti Berhasil", cutiList)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-cuti-by-id")
    @Transactional
    public Response getCutiById(@QueryParam("id") String id) {
        try {
            CutiEntity cuti = CutiEntity.findById(id);
            return Response.ok().entity(ResponseHandler.ok("Get Cuti by Id Berhasil", cuti)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    // @GET
    // @Path("/get-all-cuti")
    // @Transactional
    // public Response getAllCuti() {
    // try {
    // var cutiList = CutiEntity.listAll();
    // return Response.ok().entity(ResponseHandler.ok("Get All Cuti Berhasil",
    // cutiList)).build();
    // } catch (Exception e) {
    // throw new InternalServerErrorException(e.getMessage());
    // }
    // }

    @GET
    @Path("/get-all-cuti")
    @Transactional
    public Response getAllCuti() {
        try {
            List<CutiEntity> list = CutiEntity
                    .find("SELECT r FROM CutiEntity r LEFT JOIN FETCH r.employee_pengajuan")
                    .list();
            return Response.ok().entity(ResponseHandler.ok("Get All Cuti Berhasil", list)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @POST
    @Path("/update-cuti")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateCuti(@Valid @MultipartForm CreateCutiDto create) {
        try {
            CutiEntity cuti = CutiEntity.findById(create.id_cuti);

            cuti.jenis_cuti = create.jenis_cuti;
            cuti.tanggal_mulai = create.tanggal_mulai;
            cuti.tanggal_selesai = create.tanggal_selesai;
            cuti.alasan_cuti = create.alasan_cuti;
            cuti.id_delegasi = create.id_delegasi;

            if (create.dokumen_upload != null && create.dokumen_upload.size() > 0) {
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                Files.deleteIfExists(java.nio.file.Path.of(cuti.dokumen_cuti));
                String ext = create.dokumen_upload.fileName()
                        .substring(create.dokumen_upload.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(create.dokumen_upload.uploadedFile(), target,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                cuti.dokumen_cuti = target.toString();
            }

            return Response.ok().entity(ResponseHandler.ok("Update Cuti Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @POST
    @Path("/approve-cuti")
    @Transactional
    public Response approveCuti(
            @QueryParam("id_cuti") String id_cuti,
            @QueryParam("status_cuti") String status_cuti,
            @QueryParam("alasan_penolakan") String alasan_penolakan) {
        try {
            CutiEntity cuti = CutiEntity.findById(id_cuti);
            if (cuti == null) {
                throw new NotFoundException("Data cuti tidak ditemukan");
            }

            // deduct balance only for ANNUAL_LEAVE when approved
            if ("APPROVED".equals(status_cuti) && "ANNUAL_LEAVE".equals(cuti.jenis_cuti)) {
                int tahun = cuti.tanggal_mulai.getYear();
                SaldoCutiEntity balance = SaldoCutiEntity.findByUserAndTahun(cuti.created_by, tahun);
                if (balance != null) {
                    long totalDays = cuti.tanggal_mulai.datesUntil(cuti.tanggal_selesai.plusDays(1))
                            .filter(d -> d.getDayOfWeek() != java.time.DayOfWeek.SATURDAY
                                    && d.getDayOfWeek() != java.time.DayOfWeek.SUNDAY)
                            .count();
                    balance.sisa_cuti -= (int) totalDays;
                    balance.used_cuti += (int) totalDays;
                }
            }

            cuti.status_cuti = status_cuti;
            if (alasan_penolakan != null && !alasan_penolakan.isBlank()) {
                cuti.alasan_penolakan = alasan_penolakan;
            }

            return Response.ok().entity(ResponseHandler.ok("Status cuti berhasil diupdate", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @DELETE
    @Path("/delete-cuti")
    @Transactional
    public Response deleteCuti(@QueryParam("id") String id) {
        try {
            boolean deleted = CutiEntity.deleteById(id);
            return Response.ok().entity(ResponseHandler.ok("Hapus Cuti Berhasil", deleted)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/dokumen-file")
    @Produces("application/pdf")
    public Response getDokumen(@QueryParam("id") String id) {
        try {
            CutiEntity cuti = CutiEntity.findById(id);
            InputStream fileStream = Files.newInputStream(Paths.get(cuti.dokumen_cuti));
            return Response.ok(fileStream).build();
        } catch (Exception e) {
            throw new InternalServerErrorException("Cant get file");
        }
    }

    @GET
    @Path("/jenis-cuti")
    public Response getJenisCuti() {
        List<LeaveType> leaveTypes = List.of(
                new LeaveType("ANNUAL_LEAVE", "Cuti Tahunan", "ANL"),
                new LeaveType("IZIN", "Izin", "IZN"),
                new LeaveType("ROSTER_LEAVE", "Cuti Roster", "RST"),
                new LeaveType("SICK_LEAVE", "Cuti Sakit", "SKT"),
                new LeaveType("MATERNITY_LEAVE", "Cuti Melahirkan", "BRN"),
                new LeaveType("BAPTISM_LEAVE", "Cuti Baptis Anak", "BPT"),
                new LeaveType("MARRIAGE_LEAVE", "Cuti Menikah", "MRD"),
                new LeaveType("CHILD_WEDDING_LEAVE", "Cuti Menikahkan Anak", "MRC"),
                new LeaveType("BEREAVEMENT_LEAVE", "Cuti Keluarga Meninggal", "FDT"),
                new LeaveType("BREAVEMENT1_LEAVE", "Cuti Anggota Keluarga Dalam Satu Rumah Meninggal", "FDA"),
                new LeaveType("HAJJ_LEAVE", "Cuti Haji", "HAJ"));
        return Response.ok().entity(ResponseHandler.ok("Hapus Cuti Berhasil", leaveTypes)).build();
    }

    public record LeaveType(String value, String label, String kode) {
    }
}