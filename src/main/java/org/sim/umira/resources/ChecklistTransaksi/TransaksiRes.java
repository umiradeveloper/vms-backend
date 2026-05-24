package org.sim.umira.resources.ChecklistTransaksi;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import org.sim.umira.configs.ConfigHttpService;
import org.sim.umira.dtos.ChecklistTransaksi.CreateApprovalTransaksiDto;
import org.sim.umira.dtos.ChecklistTransaksi.CreateTransaksiDto;
import org.sim.umira.dtos.ChecklistTransaksi.ResponseApprovalTransaksiDto;
import org.sim.umira.dtos.ChecklistTransaksi.UpdatePengajuanDetailTransaksiDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.ChecklistTransaksi.CountTransaksiEntity;
import org.sim.umira.entities.ChecklistTransaksi.JenisTransaksiEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiDetailEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiDetailProyekEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiPaymentEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiProyekDetailPersetujuanEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiProyekEntity;
import org.sim.umira.entities.Reimbursement.ReimbursementEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;
import org.sim.umira.services.PDFMerge;
import org.sim.umira.services.PdfService;
import org.sim.umira.services.SuperappsExecutor;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.StreamingOutput;

@Path("ChecklistTransaksi/transaksi")
@Secured
public class TransaksiRes {
    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/ChecklistPembayaran");

    @Inject
    @SuperappsExecutor
    ExecutorService executor;

    @Inject
    PdfService pdfService;

    @Inject
    ConfigHttpService configHttpService;



    @POST
    @Path("/create-transaksi")
    @Transactional
    public Response createTransaksi(@Valid @MultipartForm CreateTransaksiDto form, @Context SecurityContext ctx) {
        CountTransaksiEntity countTrx = CountTransaksiEntity.find("kode_transaksi", form.kode_transaksi).firstResult();
        String kode_trx;
        if (countTrx == null) {
            CountTransaksiEntity countSave = new CountTransaksiEntity();
            countSave.kode_transaksi = form.kode_transaksi;
            countSave.jumlah = 1;
            countSave.persist();
            kode_trx = form.kode_transaksi + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")) + "001";
        } else {
            // countSave.kode_transaksi = form.kode_transaksi;
            countTrx.jumlah = countTrx.jumlah + 1;
            countTrx.persist();
            kode_trx = form.kode_transaksi + LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"))
                    + String.format("%03d", countTrx.jumlah);
        }
        try {
            List<CompletableFuture<TransaksiDetailEntity>> tasks = new ArrayList<>();

            TransaksiEntity transaksiParent = new TransaksiEntity();
            UserEntity userPengaju = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            transaksiParent.jenis_transaksi = form.jenis_transaksi;
            transaksiParent.kode_transaksi = kode_trx;
            transaksiParent.tanggal_pengajuan = LocalDate.now();
            transaksiParent.tempo_pembayaran_after_verified = form.tempo_pembayaran_after_verified;
            transaksiParent.keterangan = form.catatan;
            transaksiParent.user_pengajuan = userPengaju;
            transaksiParent.updatedBy = userPengaju;
            transaksiParent.nilai_invoice = form.nilai_invoice;
            transaksiParent.ppn = form.ppn;
            transaksiParent.pph = form.pph;
            transaksiParent.retensi = form.retensi;
            transaksiParent.kasbon = form.kasbon;
            transaksiParent.nilai_invoice_bersih = form.nilai_invoice_bersih;
            transaksiParent.biaya_potongan_lainnya = form.biaya_potongan_lainnya;
            transaksiParent.last_updated = LocalDateTime.now();
            transaksiParent.status_pengajuan = "Pengajuan";
            transaksiParent.proyek = form.proyek;
            transaksiParent.transaksi_via = "HO";
            transaksiParent.nama_vendor = form.nama_vendor;
            transaksiParent.kategori = form.kategori;
            transaksiParent.nomor_invoice = form.nomor_invoice;
            transaksiParent.no_po_kontrak = form.no_po_kontrak;
            transaksiParent.tanggal_invoice = form.tanggal_invoice;
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
                        // entity.nilai = form.nilai_value.get(idx);
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
    public Response getTransaksi(@Context SecurityContext ctx) {
        UserEntity user = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        try {
            List<TransaksiEntity> trxE;
            if (user.role.kode_role == "99") {
                trxE = TransaksiEntity.listAll();
            } else {
                trxE = TransaksiEntity.find("(user_pengajuan = ?1 OR approvedBy = ?1 OR paymentBy = ?1) AND transaksi_via = ?2", user, "HO").list();
            }

            List<ResponseApprovalTransaksiDto> result = new ArrayList<>();
            for (TransaksiEntity trx : trxE) {
                List<TransaksiPaymentEntity> trxPayment = TransaksiPaymentEntity
                        .find("id_transaksi = ?1", trx.id_transaksi).list();
                // List<TransaksiProyekDetailPersetujuanEntity> trxPersetujuan =
                // TransaksiProyekDetailPersetujuanEntity.find("id_transaksi = ?1",
                // trx.reference_id_transaksi_proyek).list();
                List<TransaksiProyekDetailPersetujuanEntity> trxPersetujuan;

                if (trx.reference_id_transaksi_proyek != null) {
                    TransaksiProyekEntity trxProyek = TransaksiProyekEntity.findById(trx.reference_id_transaksi_proyek);
                    if (trxProyek != null) {
                        trxPersetujuan = trxProyek.pengajuanTransaksi;
                    } else {
                        trxPersetujuan = null;
                    }
                } else {
                    trxPersetujuan = null;
                }

                result.add(new ResponseApprovalTransaksiDto(trx.id_transaksi, trx.jenis_transaksi,
                        trx.tanggal_pengajuan, trx.keterangan, trx.proyek, trx.layak_bayar, trx.status_pengajuan,
                        trx.last_updated, trx.upload_bukti_pembayaran, trx.catatan_verified, trx.kode_transaksi,
                        trx.payment_at, trx.approved_at, trx.catatan_payment, trx.tempo_pembayaran_after_verified,
                        trx.tanggal_jatuh_tempo_after_verified, trx.transaksi_via, trx.nilai_invoice, trx.pph, trx.ppn,
                        trx.retensi, trx.kasbon, trx.nilai_invoice_bersih, trx.biaya_potongan_lainnya,
                        trx.reference_id_transaksi_proyek, trx.paymentBy, trx.user_pengajuan, trx.approvedBy,
                        trx.updatedBy, trx.detailTransaksi, trxPayment, trx.nama_vendor, trx.kategori,
                        trx.nomor_invoice, trxPersetujuan, trx.tanggal_invoice, trx.no_po_kontrak));
            }
            return Response.ok().entity(ResponseHandler.ok("Get Transaksi", result)).build();
       

            
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-transaksi-by-status")
    public Response getTransaksiByStatus(@QueryParam("status") String status) {
        try {
            List<TransaksiEntity> transaksi = TransaksiEntity.find("status_pengajuan = ?1", status).list();
            List<ResponseApprovalTransaksiDto> result = new ArrayList<>();
            for (TransaksiEntity trx : transaksi) {
                List<TransaksiPaymentEntity> trxPayment = TransaksiPaymentEntity
                        .find("id_transaksi = ?1", trx.id_transaksi).list();
                // List<TransaksiProyekDetailPersetujuanEntity> trxPersetujuan =
                // TransaksiProyekDetailPersetujuanEntity.find("id_transaksi = ?1",
                // trx.reference_id_transaksi_proyek).list();
                List<TransaksiProyekDetailPersetujuanEntity> trxPersetujuan;

                if (trx.reference_id_transaksi_proyek != null) {
                    TransaksiProyekEntity trxProyek = TransaksiProyekEntity.findById(trx.reference_id_transaksi_proyek);
                    if (trxProyek != null) {
                        trxPersetujuan = trxProyek.pengajuanTransaksi;
                    } else {
                        trxPersetujuan = null;
                    }
                } else {
                    trxPersetujuan = null;
                }

                result.add(new ResponseApprovalTransaksiDto(trx.id_transaksi, trx.jenis_transaksi,
                        trx.tanggal_pengajuan, trx.keterangan, trx.proyek, trx.layak_bayar, trx.status_pengajuan,
                        trx.last_updated, trx.upload_bukti_pembayaran, trx.catatan_verified, trx.kode_transaksi,
                        trx.payment_at, trx.approved_at, trx.catatan_payment, trx.tempo_pembayaran_after_verified,
                        trx.tanggal_jatuh_tempo_after_verified, trx.transaksi_via, trx.nilai_invoice, trx.pph, trx.ppn,
                        trx.retensi, trx.kasbon, trx.nilai_invoice_bersih, trx.biaya_potongan_lainnya,
                        trx.reference_id_transaksi_proyek, trx.paymentBy, trx.user_pengajuan, trx.approvedBy,
                        trx.updatedBy, trx.detailTransaksi, trxPayment, trx.nama_vendor, trx.kategori,
                        trx.nomor_invoice, trxPersetujuan, trx.tanggal_invoice, trx.no_po_kontrak));
            }
            return Response.ok().entity(ResponseHandler.ok("Get Transaksi By Status", result)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-transaksi-by-id")
    public Response getTransaksiById(@QueryParam("id") String id) {
        try {
            TransaksiEntity trx = TransaksiEntity.findById(id);
            List<TransaksiPaymentEntity> trxPayment = TransaksiPaymentEntity.find("id_transaksi = ?1", trx.id_transaksi)
                    .list();
            List<TransaksiProyekDetailPersetujuanEntity> trxPersetujuan;

            if (trx.reference_id_transaksi_proyek != null) {
                TransaksiProyekEntity trxProyek = TransaksiProyekEntity.findById(trx.reference_id_transaksi_proyek);
                if (trxProyek != null) {
                    trxPersetujuan = trxProyek.pengajuanTransaksi;
                } else {
                    trxPersetujuan = null;
                }
            } else {
                trxPersetujuan = null;
            }
            return Response.ok()
                    .entity(ResponseHandler.ok("Get Transaksi By Id", new ResponseApprovalTransaksiDto(trx.id_transaksi,
                            trx.jenis_transaksi, trx.tanggal_pengajuan, trx.keterangan, trx.proyek, trx.layak_bayar,
                            trx.status_pengajuan, trx.last_updated, trx.upload_bukti_pembayaran, trx.catatan_verified,
                            trx.kode_transaksi, trx.payment_at, trx.approved_at, trx.catatan_payment,
                            trx.tempo_pembayaran_after_verified, trx.tanggal_jatuh_tempo_after_verified,
                            trx.transaksi_via, trx.nilai_invoice, trx.pph, trx.ppn, trx.retensi, trx.kasbon,
                            trx.nilai_invoice_bersih, trx.biaya_potongan_lainnya, trx.reference_id_transaksi_proyek,
                            trx.paymentBy, trx.user_pengajuan, trx.approvedBy, trx.updatedBy, trx.detailTransaksi,
                            trxPayment, trx.nama_vendor, trx.kategori, trx.nomor_invoice, trxPersetujuan, trx.tanggal_invoice, trx.no_po_kontrak)))
                    .build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-master-status-approval")
    public Response getMasterStatusPengajuan() {
        try {
            List<String> masterPengajuan = List.of("Verified", "Reject");
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
            if (detail.reference_id_detail != null) {
                TransaksiDetailProyekEntity trxDetail = TransaksiDetailProyekEntity
                        .findById(detail.reference_id_detail);
                if (status_verified.equals("verified")) {
                    trxDetail.checklist = 1;
                } else if (status_verified.equals("not_verified")) {
                    trxDetail.checklist = 2;
                }
            }

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
    public Response updateDetailTransaksiPengajuan(@Valid @MultipartForm UpdatePengajuanDetailTransaksiDto update,
            @QueryParam("id") String id, @Context SecurityContext ctx) {
        try {
            UserEntity userVerified = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            // List<TransaksiEntity> transaksi = TransaksiEntity.listAll();
            TransaksiDetailEntity detail = TransaksiDetailEntity.findById(id);
            if (update.upload_dokumen_transaksi != null) {
                Files.deleteIfExists(java.nio.file.Path.of(detail.jawaban));
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
            // if(update.nilai_transaksi != null){
            // detail.nilai = update.nilai_transaksi;
            // }

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
    public Response updateStatusPengajuan(@Valid @MultipartForm CreateApprovalTransaksiDto create,
            @QueryParam("id") String id, @Context SecurityContext ctx,
            @QueryParam("catatan_verified") String catatan_verified,
            @QueryParam("catatan_payment") String catatan_payment) {
        TransaksiEntity trx = TransaksiEntity.findById(id);
        if (create.status_approval.equals("Payment")) {
            List<TransaksiPaymentEntity> paymentTrx = TransaksiPaymentEntity.find("id_transaksi = ?1", id).list();
            BigInteger checkNilaiBayar = BigInteger.ZERO;
            if (paymentTrx.size() > 0) {
                for (TransaksiPaymentEntity trxP : paymentTrx) {
                    if (trxP.bukti_bayar != null) {
                        checkNilaiBayar = checkNilaiBayar.add(trxP.nominal_bayar);
                    }
                }

            }
            BigInteger total_bayar = checkNilaiBayar.add(create.nilai_bayar);
            if (total_bayar.compareTo(trx.nilai_invoice_bersih) > 0) {
                throw new BadRequestException("Nominal Bayar Melebihi Invoice Yang Di Tagih");
            }
        }

        try {

            UserEntity userApproved = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            String bb = "";
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
                // trx.upload_bukti_pembayaran = target.toString();
                bb = target.toString();

            }
            if (create.status_approval.equals("Payment")) {
                BigInteger checkNilaiBayar = BigInteger.ZERO;
                TransaksiPaymentEntity trxPayment = new TransaksiPaymentEntity();
                if (create.bukti_bayar != null) {
                    trxPayment.bukti_bayar = bb;
                }
                trxPayment.id_transaksi = trx.id_transaksi;
                trxPayment.nominal_bayar = create.nilai_bayar;
                trxPayment.reference_id_transaksi = trx.reference_id_transaksi_proyek;
                trxPayment.nominal_bayar = create.nilai_bayar;
                trxPayment.persist();

                List<TransaksiPaymentEntity> paymentTrx = TransaksiPaymentEntity
                        .find("id_transaksi = ?1", trx.id_transaksi).list();
                if (paymentTrx.size() > 0) {
                    for (TransaksiPaymentEntity trxP : paymentTrx) {
                        if (trxP.nominal_bayar != null) {
                            checkNilaiBayar = checkNilaiBayar.add(trxP.nominal_bayar);
                        }
                    }

                }
                if (checkNilaiBayar.compareTo(trx.nilai_invoice_bersih) == 0) {

                    trx.status_pengajuan = "Paid";
                    if (trx.transaksi_via.equals("Proyek")) {
                        if (trx.reference_id_transaksi_proyek != null || trx.reference_id_transaksi_proyek != "") {
                            TransaksiProyekEntity trxProyek = TransaksiProyekEntity
                                    .find("id_transaksi = ?1", trx.reference_id_transaksi_proyek).firstResult();
                            if (trxProyek != null) {
                                trxProyek.paymentBy = userApproved;
                                trxProyek.payment_at = LocalDateTime.now();
                                trxProyek.status_pengajuan = "Paid Full";
                            }

                        }
                    }
                }

            } else {
                trx.status_pengajuan = create.status_approval;
            }

            switch (create.status_approval) {
                case "Verified":
                    trx.approvedBy = userApproved;
                    trx.approved_at = LocalDateTime.now();
                    if (trx.transaksi_via.equals("Proyek")) {
                        if (trx.reference_id_transaksi_proyek != null || trx.reference_id_transaksi_proyek != "") {
                            TransaksiProyekEntity trxProyek = TransaksiProyekEntity
                                    .find("id_transaksi = ?1", trx.reference_id_transaksi_proyek).firstResult();
                            trxProyek.approvedBy = userApproved;
                            trxProyek.approved_at = LocalDateTime.now();
                            trxProyek.status_pengajuan = "Telah Di Verified HO";
                            trxProyek.tanggal_jatuh_tempo_after_verified = LocalDate.now()
                                    .plusDays(Long.valueOf(trx.tempo_pembayaran_after_verified));
                        }
                    }
                    trx.tanggal_jatuh_tempo_after_verified = LocalDate.now()
                            .plusDays(Long.valueOf(trx.tempo_pembayaran_after_verified));


                    
                   
                    break;
                case "Reject":
                    if (trx.transaksi_via.equals("Proyek")) {
                        if (trx.reference_id_transaksi_proyek != null || trx.reference_id_transaksi_proyek != "") {
                            TransaksiProyekEntity trxProyek = TransaksiProyekEntity
                                    .find("id_transaksi = ?1", trx.reference_id_transaksi_proyek).firstResult();
                            trxProyek.approvedBy = userApproved;
                            trxProyek.status_pengajuan = "Telah Di Reject HO";
                        }
                    }
                    trx.approvedBy = userApproved;
                    break;
                // case "Payment":
                // if(trx.transaksi_via.equals("Proyek")){
                // if(trx.reference_id_transaksi_proyek != null ||
                // trx.reference_id_transaksi_proyek != ""){
                // TransaksiProyekEntity trxProyek = TransaksiProyekEntity.find("id_transaksi =
                // ?1", trx.reference_id_transaksi_proyek).firstResult();
                // trxProyek.paymentBy = userApproved;
                // trxProyek.payment_at = LocalDateTime.now();
                // trxProyek.status_pengajuan = "Telah Di Payment HO";
                // }
                // }
                // trx.paymentBy = userApproved;
                // trx.payment_at = LocalDateTime.now();
                // break;
                default:
                    trx.approvedBy = null;
                    trx.paymentBy = null;
                    break;
            }

            trx.updatedBy = userApproved;
            trx.last_updated = LocalDateTime.now();
            if (create.layak_bayar != null || create.layak_bayar != "") {
                trx.layak_bayar = create.layak_bayar;
                if (trx.transaksi_via.equals("Proyek")) {
                    if (trx.reference_id_transaksi_proyek != null || trx.reference_id_transaksi_proyek != "") {
                        TransaksiProyekEntity trxProyek = TransaksiProyekEntity
                                .find("id_transaksi = ?1", trx.reference_id_transaksi_proyek).firstResult();
                        if (trxProyek != null) {
                            trxProyek.layak_bayar = create.layak_bayar;
                        }

                    }
                }
            }

            if (catatan_verified != null || catatan_verified != "") {
                trx.catatan_verified = catatan_verified;
                if (trx.transaksi_via.equals("Proyek")) {
                    if (trx.reference_id_transaksi_proyek != null || trx.reference_id_transaksi_proyek != "") {
                        TransaksiProyekEntity trxProyek = TransaksiProyekEntity
                                .find("id_transaksi = ?1", trx.reference_id_transaksi_proyek).firstResult();
                        // trxProyek.catatan_verified = catatan_verified;
                        if (trxProyek != null) {
                            trxProyek.catatan_verified = catatan_verified;
                        }
                    }
                }
            }
            if (catatan_payment != null || catatan_payment != "") {
                trx.catatan_payment = catatan_payment;
                if (trx.transaksi_via.equals("Proyek")) {
                    if (trx.reference_id_transaksi_proyek != null || trx.reference_id_transaksi_proyek != "") {
                        TransaksiProyekEntity trxProyek = TransaksiProyekEntity
                                .find("id_transaksi = ?1", trx.reference_id_transaksi_proyek).firstResult();
                        if (trxProyek != null) {
                            trxProyek.catatan_payment = catatan_payment;
                        }

                    }
                }
            }
            if(create.status_approval.equals("Verified")){
                byte[] DokumenMerge = getDokumenDisposisi(trx.id_transaksi);
                // List<String> role = List.of("");
                // List<UserEntity> userApproval = UserEntity.find("role.kode_role IN ")
                configHttpService.sendWhatsappDokumenNew("+6287826246455", trx.kode_transaksi, DokumenMerge, trx.kode_transaksi).subscribe().with(
                        response -> {
                            System.out.println(response);
                        },
                        failure -> {
                            failure.printStackTrace();
                        }
                    );
                // System.out.println(test);
                configHttpService.sendEmailWithAttach("hafidhanafid@gmail.com", "Dokumen", "Dokumen Transaksi", "transaksi-"+trx.kode_transaksi, DokumenMerge);
            }
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
            TransaksiPaymentEntity trx = TransaksiPaymentEntity.findById(id);
            InputStream fileStream = Files.newInputStream(Paths.get(trx.bukti_bayar));
            return Response.ok(fileStream).build();
        } catch (Exception e) {
            throw new InternalServerErrorException("Cant get file");
        }
    }

    @GET
    @Path("/dokumen-transaksi-merge")
    @PermitAll
    // @Produces("application/pdf")
    public Response getDokumenTransaksiMerge(@QueryParam("id") String id) {
        try {
            TransaksiEntity trx = TransaksiEntity.findById(id);
            List<TransaksiDetailEntity> trxDetail = TransaksiDetailEntity.find("transaksi = ?1", trx).list();
            List<JenisTransaksiEntity> jenisTrx = JenisTransaksiEntity.find("jenis_transaksi = ?1", trx.jenis_transaksi).list();
            TransaksiProyekEntity trxProyek = null;
            if(trx.reference_id_transaksi_proyek != null && !trx.reference_id_transaksi_proyek.isBlank()){
                trxProyek = TransaksiProyekEntity.findById(trx.reference_id_transaksi_proyek);
            }
            // TransaksiProyekEntity trxProyek = TransaksiProyekEntity.findById(trx.reference_id_transaksi_proyek);
            List<String> urls = trxDetail.stream().map(x -> x.jawaban).toList();
            byte[] cover = pdfService.generateFormDisposisi(trx, jenisTrx, trxProyek);

            PDFMerge mergePdf = new PDFMerge();

            byte[] merge = mergePdf.mergeUploadedPdf(urls, cover);

            StreamingOutput stream = output -> {

                try (
                        InputStream inputStream = new ByteArrayInputStream(merge)) {

                    byte[] buffer = new byte[8192];

                    int length;

                    while ((length = inputStream.read(buffer)) != -1) {

                        output.write(
                                buffer,
                                0,
                                length);
                    }

                    output.flush();
                }
            };

            return Response.ok(stream)
                    .type("application/pdf")
                    .header(
                            "Content-Disposition",
                            "inline; filename=\"merged.pdf\"")
                    .header(
                            "Content-Length",
                            merge.length)
                    .build();

        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    private byte[] getDokumenDisposisi(String id){
        try {
             TransaksiEntity trx = TransaksiEntity.findById(id);
            List<TransaksiDetailEntity> trxDetail = TransaksiDetailEntity.find("transaksi = ?1", trx).list();
            List<JenisTransaksiEntity> jenisTrx = JenisTransaksiEntity.find("jenis_transaksi = ?1", trx.jenis_transaksi).list();
            TransaksiProyekEntity trxProyek = null;
            if(trx.reference_id_transaksi_proyek != null && !trx.reference_id_transaksi_proyek.isBlank()){
                trxProyek = TransaksiProyekEntity.findById(trx.reference_id_transaksi_proyek);
            }
            
            List<String> urls = trxDetail.stream().map(x -> x.jawaban).toList();
            byte[] cover = pdfService.generateFormDisposisi(trx, jenisTrx, trxProyek);

            PDFMerge mergePdf = new PDFMerge();

            byte[] merge = mergePdf.mergeUploadedPdf(urls, cover);
            return merge;
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
            // TODO: handle exception
        }
    }
}
