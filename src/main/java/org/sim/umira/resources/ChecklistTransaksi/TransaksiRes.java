package org.sim.umira.resources.ChecklistTransaksi;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.sim.umira.dtos.ChecklistTransaksi.CreateApprovalTransaksiDto;
import org.sim.umira.dtos.ChecklistTransaksi.CreateTransaksiDto;
import org.sim.umira.dtos.ChecklistTransaksi.UpdatePengajuanDetailTransaksiDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiDetailEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiEntity;
import org.sim.umira.entities.Reimbursement.ReimbursementEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;
import org.sim.umira.services.SuperappsExecutor;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("ChecklistTransaksi/transaksi")
@Secured
public class TransaksiRes {
    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/ChecklistPembayaran");

    @Inject
    @SuperappsExecutor
    ExecutorService executor;

    @POST
    @Path("/create-transaksi")
    @Transactional
    public Response createTransaksi(@MultipartForm CreateTransaksiDto form, @Context SecurityContext ctx) {
        try {
            List<CompletableFuture<TransaksiDetailEntity>> tasks = new ArrayList<>();
            TransaksiEntity transaksiParent = new TransaksiEntity();
            UserEntity userPengaju = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            transaksiParent.jenis_transaksi = form.jenis_transaksi;
            transaksiParent.tanggal_pengajuan = LocalDate.now();
            transaksiParent.keterangan = form.catatan;
            transaksiParent.user_pengajuan = userPengaju;
            transaksiParent.updatedBy = userPengaju;
            transaksiParent.last_updated = LocalDateTime.now();
            transaksiParent.status_pengajuan = "Pengajuan";
            transaksiParent.proyek = form.proyek;
            transaksiParent.persist();
            for (int i = 0; i < form.files.size(); i++) {
                final int idx = i;
                System.out.println(form.nama_transaksi.get(idx));
                CompletableFuture<TransaksiDetailEntity> task = CompletableFuture.supplyAsync(() -> {
                    try {

                        String ext = form.files.get(idx).fileName()
                                .substring(form.files.get(idx).fileName().lastIndexOf("."));
                        String fileName = java.util.UUID.randomUUID() + ext;
                        if (!Files.exists(UPLOAD_DIR)) {
                            Files.createDirectories(UPLOAD_DIR);
                        }
                        java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                        Files.copy(
                                form.files.get(idx).uploadedFile(),
                                target,
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        // ap.url_dokumen = target.toString();
                        // buat entity (belum persist)
                        TransaksiDetailEntity entity = new TransaksiDetailEntity();
                        entity.transaksi = transaksiParent;
                        entity.pertanyaan = form.nama_transaksi.get(idx);
                        entity.jawaban = target.toString();
                        return entity;

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, executor);

                tasks.add(task);
            }

            // ⏳ Tunggu semua upload selesai
            List<TransaksiDetailEntity> entities = tasks.stream()
                    .map(CompletableFuture::join)
                    .toList();

            // // 💾 Simpan ke database (HARUS di thread utama)
            for (TransaksiDetailEntity entity : entities) {
                entity.persist();
            }
            return Response.ok().entity(ResponseHandler.ok("Create Jenis Transaksi", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-transaksi")
    public Response getTransaksi() {
        try {
            List<TransaksiEntity> transaksi = TransaksiEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Get Transaksi", transaksi)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-transaksi-by-id")
    public Response getTransaksiById(@QueryParam("id") String id) {
        try {
            TransaksiEntity transaksi = TransaksiEntity.findById(id);
            return Response.ok().entity(ResponseHandler.ok("Get Transaksi By Id", transaksi)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-master-status-approval")
    public Response getMasterStatusPengajuan() {
        try {
            List<String> masterPengajuan = List.of("Approved", "Reject");
            return Response.ok().entity(ResponseHandler.ok("Get Master Status", masterPengajuan)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
    @GET
    @Path("/get-master-status-layak-bayar")
    public Response getMasterStatusLayakBayar() {
        try {
            List<String> list = List.of("Layak Bayar", "Tidak Layak Bayar");
            return Response.ok().entity(ResponseHandler.ok("Get Data", list)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-detail-transaksi-by-id")
    public Response getDetailTransaksi(@QueryParam("id") String id_detail) {
        try {
            // List<Tr> transaksi = TransaksiEntity.listAll();
            TransaksiDetailEntity trx = TransaksiDetailEntity.findById(id_detail);
            return Response.ok().entity(ResponseHandler.ok("Get Detail Transaksi", trx)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/update-detail-transaksi")
    @Transactional
    public Response updateDetailTransaksi(@QueryParam("id") String id,
            @QueryParam("status_verified") String status_verified, @QueryParam("catatan") String catatan,
            @Context SecurityContext ctx) {
        try {
            UserEntity userVerified = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            // List<TransaksiEntity> transaksi = TransaksiEntity.listAll();
            TransaksiDetailEntity detail = TransaksiDetailEntity.findById(id);
            detail.user_verified = userVerified;
            detail.catatan = (catatan != "") ? catatan : "";
            detail.verified_at = LocalDateTime.now();
            if (status_verified.equals("verified")) {
                detail.checklist = 1;
            } else if (status_verified.equals("not_verified")) {
                detail.checklist = 2;
            }
            return Response.ok().entity(ResponseHandler.ok("Update Detail Transaksi", detail)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @POST
    @Path("/update-detail-transaksi-pengajuan")
    @Transactional
    public Response updateDetailTransaksiPengajuan(@MultipartForm UpdatePengajuanDetailTransaksiDto update, @QueryParam("id") String id, @Context SecurityContext ctx) {
        try {
            UserEntity userVerified = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            // List<TransaksiEntity> transaksi = TransaksiEntity.listAll();
            TransaksiDetailEntity detail = TransaksiDetailEntity.findById(id);
            if(update.upload_dokumen_transaksi != null){
                String ext = update.upload_dokumen_transaksi.fileName()
                        .substring(update.upload_dokumen_transaksi.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                        update.upload_dokumen_transaksi.uploadedFile(),
                        target,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                detail.jawaban = target.toString();
            }
            detail.checklist = null;
            
            return Response.ok().entity(ResponseHandler.ok("Update Detail Transaksi", detail)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @POST
    @Path("/update-status-pengajuan")
    @Transactional
    public Response updateStatusPengajuan(@MultipartForm CreateApprovalTransaksiDto create, @QueryParam("id") String id, @Context SecurityContext ctx) {
        try {
            TransaksiEntity trx = TransaksiEntity.findById(id);
            UserEntity userApproved = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            if (create.bukti_bayar != null) {
                String ext = create.bukti_bayar.fileName()
                        .substring(create.bukti_bayar.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                        create.bukti_bayar.uploadedFile(),
                        target,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                trx.upload_bukti_pembayaran = target.toString();
            }
            trx.status_pengajuan = create.status_approval;
            trx.approvedBy = userApproved;
            trx.updatedBy = userApproved;
            trx.last_updated = LocalDateTime.now();
            trx.layak_bayar = create.layak_bayar;
        
            return Response.ok().entity(ResponseHandler.ok("Update Detail Transaksi", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/dokumen-file")
    @Produces("application/pdf")
    public Response getDokumen(@QueryParam("id") String id) {
        try {
            TransaksiDetailEntity trx = TransaksiDetailEntity.findById(id);
            InputStream fileStream = Files.newInputStream(Paths.get(trx.jawaban));
            return Response.ok(fileStream).build();
        } catch (Exception e) {
            throw new InternalServerErrorException("Cant get file");
        }
    }

    @GET
    @Path("/dokumen-bukti-bayar")
    @Produces("application/pdf")
    public Response getDokumenBuktiBayar(@QueryParam("id") String id) {
        try {
            TransaksiEntity trx = TransaksiEntity.findById(id);
            InputStream fileStream = Files.newInputStream(Paths.get(trx.upload_bukti_pembayaran));
            return Response.ok(fileStream).build();
        } catch (Exception e) {
            throw new InternalServerErrorException("Cant get file");
        }
    }
}
