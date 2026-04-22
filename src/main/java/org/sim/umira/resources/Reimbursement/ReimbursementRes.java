package org.sim.umira.resources.Reimbursement;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.Reimbursement.CreateReimbursementDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.HumanResources.EmployeeEntity;
import org.sim.umira.entities.Reimbursement.ReimbursementEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/Reimbursement")
@Secured
public class ReimbursementRes {

    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/dokumen-reimbursement");

    @POST
    @Path("/create-reimbursement")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createReimbursement(
            @Valid @MultipartForm CreateReimbursementDto create, @Context SecurityContext ctx) {

        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        EmployeeEntity emp = EmployeeEntity.find("user = ?1", ue).firstResult();

        try {
            ReimbursementEntity reimbursement = new ReimbursementEntity();

            if (create.dokumen_reimbursement != null && create.dokumen_reimbursement.size() > 0) {
                String ext = create.dokumen_reimbursement.fileName()
                        .substring(create.dokumen_reimbursement.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                        create.dokumen_reimbursement.uploadedFile(),
                        target,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                reimbursement.dokumen_reimbursement = target.toString();
            }

            reimbursement.employee_pengajuan = emp;
            reimbursement.jenis_reimbursement = create.jenis_reimbursement;
            reimbursement.tanggal_reimbursement = create.tanggal_reimbursement;
            reimbursement.jumlah = create.jumlah;
            reimbursement.keterangan = create.keterangan;
            // reimbursement.id_approver = create.id_approver;
            reimbursement.status_reimbursement = "PENDING";
            reimbursement.created_at = LocalDateTime.now();
            reimbursement.created_by = ue.id_user;
            reimbursement.persist();

            return Response.ok().entity(ResponseHandler.ok("Create Reimbursement Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    // @GET
    // @Path("/get-all-reimbursement")
    // @Transactional
    // public Response getAllReimbursement() {
    // try {
    // List<ReimbursementEntity> list = ReimbursementEntity.listAll();
    // return Response.ok().entity(ResponseHandler.ok("Get All Reimbursement
    // Berhasil", list)).build();
    // } catch (Exception e) {
    // throw new InternalServerErrorException(e.getMessage());
    // }
    // }

    @GET
    @Path("/get-all-reimbursement")
    @Transactional
    public Response getAllReimbursement() {
        try {
            List<ReimbursementEntity> list = ReimbursementEntity
                    .find("SELECT r FROM ReimbursementEntity r LEFT JOIN FETCH r.employee_pengajuan")
                    .list();
            return Response.ok().entity(ResponseHandler.ok("Get All Reimbursement Berhasil", list)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-reimbursement-by-user")
    @Transactional
    public Response getReimbursementByUser(@Context SecurityContext ctx) {
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();

            var list = ReimbursementEntity.find("user = ?1", ue).list();
            return Response.ok().entity(ResponseHandler.ok("Get Reimbursement Berhasil", list)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-reimbursement-by-id")
    @Transactional
    public Response getReimbursementById(@QueryParam("id") String id) {
        try {
            ReimbursementEntity reimbursement = ReimbursementEntity.findById(id);
            return Response.ok().entity(ResponseHandler.ok("Get Reimbursement by Id Berhasil", reimbursement)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @POST
    @Path("/update-reimbursement")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateReimbursement(@Valid @MultipartForm CreateReimbursementDto create) {
        try {
            ReimbursementEntity reimbursement = ReimbursementEntity.findById(create.id_reimbursement);

            reimbursement.jenis_reimbursement = create.jenis_reimbursement;
            reimbursement.tanggal_reimbursement = create.tanggal_reimbursement;
            reimbursement.jumlah = create.jumlah;
            reimbursement.keterangan = create.keterangan;
            // reimbursement.id_approver = create.id_approver;

            if (create.dokumen_reimbursement != null && create.dokumen_reimbursement.size() > 0) {
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                if (reimbursement.dokumen_reimbursement != null) {
                    Files.deleteIfExists(java.nio.file.Path.of(reimbursement.dokumen_reimbursement));
                }
                String ext = create.dokumen_reimbursement.fileName()
                        .substring(create.dokumen_reimbursement.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(create.dokumen_reimbursement.uploadedFile(), target,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                reimbursement.dokumen_reimbursement = target.toString();
            }

            return Response.ok().entity(ResponseHandler.ok("Update Reimbursement Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @DELETE
    @Path("/delete-reimbursement")
    @Transactional
    public Response deleteReimbursement(@QueryParam("id") String id) {
        try {
            boolean deleted = ReimbursementEntity.deleteById(id);
            return Response.ok().entity(ResponseHandler.ok("Hapus Reimbursement Berhasil", deleted)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/dokumen-file")
    @Produces("application/pdf")
    public Response getDokumen(@QueryParam("id") String id) {
        try {
            ReimbursementEntity reimbursement = ReimbursementEntity.findById(id);
            InputStream fileStream = Files.newInputStream(Paths.get(reimbursement.dokumen_reimbursement));
            return Response.ok(fileStream).build();
        } catch (Exception e) {
            throw new InternalServerErrorException("Cant get file");
        }
    }

    @POST
    @Path("/approve-reimbursement")
    @Transactional
    public Response approveReimbursement(
            @QueryParam("id_reimbursement") String id_reimbursement,
            @QueryParam("status_reimbursement") String status_reimbursement,
            @QueryParam("alasan_penolakan") String alasan_penolakan) {
        try {
            ReimbursementEntity reimbursement = ReimbursementEntity.findById(id_reimbursement);
            if (reimbursement == null) {
                throw new NotFoundException("Data reimbursement tidak ditemukan");
            }
            reimbursement.status_reimbursement = status_reimbursement;
            if (alasan_penolakan != null && !alasan_penolakan.isBlank()) {
                reimbursement.alasan_penolakan = alasan_penolakan;
            }
            return Response.ok().entity(ResponseHandler.ok("Status reimbursement berhasil diupdate", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}