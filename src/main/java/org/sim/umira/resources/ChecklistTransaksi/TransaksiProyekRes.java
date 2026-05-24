package org.sim.umira.resources.ChecklistTransaksi;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.ChecklistTransaksi.CreateApprovalTransaksiDto;
import org.sim.umira.dtos.ChecklistTransaksi.CreateApprovalTransaksiProyekDto;
import org.sim.umira.dtos.ChecklistTransaksi.CreateTransaksiDto;
import org.sim.umira.dtos.ChecklistTransaksi.CreateTransaksiProyekDto;
import org.sim.umira.dtos.ChecklistTransaksi.ResponseTransaksiProyekDto;
import org.sim.umira.dtos.ChecklistTransaksi.UpdatePengajuanDetailTransaksiDto;
import org.sim.umira.dtos.ChecklistTransaksi.UpdatePengajuanDetailTransaksiProyekDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.ChecklistTransaksi.CountTransaksiEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiDetailEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiDetailProyekEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiPaymentEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiProyekDetailPersetujuanEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiProyekEntity;
import org.sim.umira.entities.CostControl.BiayaKontruksiEntity;
import org.sim.umira.entities.CostControl.PengajuanBiayaKonstruksiDetailEntity;
import org.sim.umira.entities.CostControl.PengajuanBiayaKonstruksiEntity;
import org.sim.umira.entities.CostControl.PengajuanBiayaKonstruksiPersetujuanEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;
import org.sim.umira.services.QRCodeService;
import org.sim.umira.services.SuperappsExecutor;

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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("ChecklistTransaksi/transaksi/Proyek")
@Secured
public class TransaksiProyekRes {
    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/ChecklistPembayaran/Proyek");

    @Inject
    @SuperappsExecutor
    ExecutorService executor;

    @Inject
    QRCodeService qrCodeService;

    @POST
    @Path("/create-transaksi")
    @Transactional
    public Response createTransaksi(@Valid @MultipartForm CreateTransaksiProyekDto form, @Context SecurityContext ctx) {
        CountTransaksiEntity countTrx = CountTransaksiEntity.find("kode_transaksi", form.kode_transaksi).firstResult();
        String kode_trx;
        if(countTrx == null){
            CountTransaksiEntity countSave = new CountTransaksiEntity();
            countSave.kode_transaksi = form.kode_transaksi;
            countSave.jumlah = 1;
            countSave.persist();
            kode_trx = form.kode_transaksi+"PRY"+ LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"))+"001";
        }else{
            // countSave.kode_transaksi = form.kode_transaksi;
            countTrx.jumlah = countTrx.jumlah + 1;
            countTrx.persist();
            kode_trx = form.kode_transaksi+"PRY"+ LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"))+String.format("%03d", countTrx.jumlah);
        }
        try {
            List<CompletableFuture<TransaksiDetailProyekEntity>> tasks = new ArrayList<>();

            TransaksiProyekEntity transaksiParent = new TransaksiProyekEntity();
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
            transaksiParent.nomor_invoice = form.nomor_invoice;
            transaksiParent.kategori = form.kategori;
            transaksiParent.nama_vendor = form.nama_vendor;
            transaksiParent.biaya_potongan_lainnya = form.biaya_potongan_lainnya;
            transaksiParent.last_updated = LocalDateTime.now();
            transaksiParent.status_pengajuan = "Pengajuan";
            transaksiParent.proyek = form.proyek;
            transaksiParent.no_po_kontrak = form.no_po_kontrak;
            transaksiParent.tanggal_invoice = form.tanggal_invoice;
            // transaksiParent.transaksi_via = "HO";
            transaksiParent.persist();

            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            TransaksiProyekDetailPersetujuanEntity persetujuanPengaju = new TransaksiProyekDetailPersetujuanEntity();
            persetujuanPengaju.id_user = ue.id_user;
            persetujuanPengaju.transaksiPersetujuanProyek = transaksiParent;
            persetujuanPengaju.status_approver = "Pengajuan";
            persetujuanPengaju.tanggal_persetujuan = LocalDateTime.now();
            persetujuanPengaju.urutan = 0;
            persetujuanPengaju.nama_persetujuan = ue.nama;
            persetujuanPengaju.jabatan_persetujuan = ue.role.nama_role;
            persetujuanPengaju.persist();
            

            for (int i = 0; i < form.files.size(); i++) {
                final int idx = i;
                System.out.println(form.nama_transaksi.get(idx));
                CompletableFuture<TransaksiDetailProyekEntity> task = CompletableFuture.supplyAsync(() -> {
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
                        TransaksiDetailProyekEntity entity = new TransaksiDetailProyekEntity();
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
            for (int y = 0; y < form.approval.size(); y++) {
                final int idx = y;
                // System.out.println(form.nama_transaksi.get(idx));
                // CompletableFuture<TransaksiProyekDetailPersetujuanEntity> taskPersetujuan = CompletableFuture.supplyAsync(() -> {
                 try {

                       
                        // ap.url_dokumen = target.toString();
                        // buat entity (belum persist)
                        UserEntity userId = UserEntity.findById(form.approval.get(idx));
                        TransaksiProyekDetailPersetujuanEntity entity = new TransaksiProyekDetailPersetujuanEntity();
                        entity.id_user = form.approval.get(idx);
                        entity.transaksiPersetujuanProyek = transaksiParent;
                        entity.status_approver = "Waiting";
                        entity.urutan = form.urutan.get(idx);
                        entity.nama_persetujuan = userId.nama;
                        entity.jabatan_persetujuan = userId.role.nama_role;
                        entity.persist();
                        

                        // entity.nilai = form.nilai_value.get(idx);
                        // return entity;

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                // }, executor);

                // tasks2.add(taskPersetujuan);
            }

            // ⏳ Tunggu semua upload selesai
            List<TransaksiDetailProyekEntity> entities = tasks.stream()
                    .map(CompletableFuture::join)
                    .toList();

           

             // 💾 Simpan ke database (HARUS di thread utama)
            for (TransaksiDetailProyekEntity entity : entities) {
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
            List<TransaksiProyekEntity> trx;
            if(user.role.kode_role == "99"){
                trx = TransaksiProyekEntity.listAll();
            }else{
                trx = TransaksiProyekEntity.find("""
                SELECT DISTINCT p
                FROM TransaksiProyekEntity p
                WHERE EXISTS (
                    SELECT 1
                    FROM TransaksiProyekDetailPersetujuanEntity ps
                    WHERE ps.transaksiPersetujuanProyek = p
                    AND ps.id_user = ?1
                )
            """, user.id_user).list();
            }
            List<ResponseTransaksiProyekDto> transaksi_proyek = new ArrayList<>();
            for(TransaksiProyekEntity trxP: trx){
                List<TransaksiPaymentEntity> trxPayment = TransaksiPaymentEntity.find("reference_id_transaksi = ?1", trxP.id_transaksi).list();
                transaksi_proyek.add(new ResponseTransaksiProyekDto(trxP.id_transaksi, trxP.jenis_transaksi, trxP.tanggal_pengajuan, trxP.keterangan, trxP.proyek, trxP.layak_bayar, trxP.status_pengajuan, trxP.last_updated, trxP.upload_bukti_pembayaran, trxP.catatan_verified, trxP.kode_transaksi, trxP.payment_at, trxP.approved_at, trxP.catatan_payment, trxP.tempo_pembayaran_after_verified, trxP.tanggal_jatuh_tempo_after_verified, trxP.nilai_invoice, trxP.pph, trxP.ppn, trxP.retensi, trxP.kasbon, trxP.nilai_invoice_bersih, trxP.nomor_invoice, trxP.kategori, trxP.nama_vendor, trxP.biaya_potongan_lainnya,trxP.paymentBy, trxP.user_pengajuan, trxP.approvedBy, trxP.updatedBy, trxP.detailTransaksi, trxP.pengajuanTransaksi, trxPayment, trxP.tanggal_invoice, trxP.no_po_kontrak));
            }
            
            return Response.ok().entity(ResponseHandler.ok("Get Transaksi", transaksi_proyek)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
    @GET
    @Path("/get-transaksi-approval")
    public Response getTransaksiByStatus( @Context SecurityContext ctx) {
        try {
            UserEntity user = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<TransaksiProyekEntity> transaksi = TransaksiProyekEntity.find("""
                SELECT DISTINCT p
                FROM TransaksiProyekEntity p
                WHERE EXISTS (
                    SELECT 1
                    FROM TransaksiProyekDetailPersetujuanEntity ps
                    WHERE ps.transaksiPersetujuanProyek = p
                    AND ps.id_user = ?1
                    AND ps.tanggal_persetujuan IS NULL
                    AND ps.urutan = (
                        SELECT MIN(ps2.urutan)
                        FROM TransaksiProyekDetailPersetujuanEntity ps2
                        WHERE ps2.transaksiPersetujuanProyek = p
                            AND ps2.tanggal_persetujuan IS NULL
                    )
                )
            """, user.id_user).list();
             List<ResponseTransaksiProyekDto> transaksi_proyek = new ArrayList<>();
            for(TransaksiProyekEntity trxP: transaksi){
                List<TransaksiPaymentEntity> trxPayment = TransaksiPaymentEntity.find("reference_id_transaksi = ?1", trxP.id_transaksi).list();
                transaksi_proyek.add(new ResponseTransaksiProyekDto(trxP.id_transaksi, trxP.jenis_transaksi, trxP.tanggal_pengajuan, trxP.keterangan, trxP.proyek, trxP.layak_bayar, trxP.status_pengajuan, trxP.last_updated, trxP.upload_bukti_pembayaran, trxP.catatan_verified, trxP.kode_transaksi, trxP.payment_at, trxP.approved_at, trxP.catatan_payment, trxP.tempo_pembayaran_after_verified, trxP.tanggal_jatuh_tempo_after_verified, trxP.nilai_invoice, trxP.pph, trxP.ppn, trxP.retensi, trxP.kasbon, trxP.nilai_invoice_bersih, trxP.nomor_invoice, trxP.kategori, trxP.nama_vendor, trxP.biaya_potongan_lainnya,trxP.paymentBy, trxP.user_pengajuan, trxP.approvedBy, trxP.updatedBy, trxP.detailTransaksi, trxP.pengajuanTransaksi, trxPayment, trxP.tanggal_invoice, trxP.no_po_kontrak));
            }
            
            return Response.ok().entity(ResponseHandler.ok("Get Transaksi By Status", transaksi_proyek)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-transaksi-by-id")
    public Response getTransaksiById(@QueryParam("id") String id) {
        try {
            TransaksiProyekEntity trxP = TransaksiProyekEntity.findById(id);
            List<TransaksiPaymentEntity> detailPayment = TransaksiPaymentEntity.find("reference_id_transaksi = ?1",trxP.id_transaksi).list();
            return Response.ok().entity(ResponseHandler.ok("Get Transaksi By Id", new ResponseTransaksiProyekDto(trxP.id_transaksi, trxP.jenis_transaksi, trxP.tanggal_pengajuan, trxP.keterangan, trxP.proyek, trxP.layak_bayar, trxP.status_pengajuan, trxP.last_updated, trxP.upload_bukti_pembayaran, trxP.catatan_verified, trxP.kode_transaksi, trxP.payment_at, trxP.approved_at, trxP.catatan_payment, trxP.tempo_pembayaran_after_verified, trxP.tanggal_jatuh_tempo_after_verified, trxP.nilai_invoice, trxP.pph, trxP.ppn, trxP.retensi, trxP.kasbon, trxP.nilai_invoice_bersih, trxP.nomor_invoice, trxP.kategori, trxP.nama_vendor, trxP.biaya_potongan_lainnya,trxP.paymentBy, trxP.user_pengajuan, trxP.approvedBy, trxP.updatedBy, trxP.detailTransaksi, trxP.pengajuanTransaksi, detailPayment, trxP.tanggal_invoice, trxP.no_po_kontrak))).build();
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
            TransaksiDetailProyekEntity trx = TransaksiDetailProyekEntity.findById(id_detail);
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
            TransaksiDetailProyekEntity detail = TransaksiDetailProyekEntity.findById(id);
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
    public Response updateDetailTransaksiPengajuan(@Valid @MultipartForm UpdatePengajuanDetailTransaksiProyekDto update, @QueryParam("id") String id, @Context SecurityContext ctx) {
        try {
            UserEntity userVerified = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            // List<TransaksiEntity> transaksi = TransaksiEntity.listAll();
            // TransaksiDetailPdetail = TransaksiDetailEntity.findById(id);
            TransaksiDetailProyekEntity detail = TransaksiDetailProyekEntity.findById(id);
            if(update.upload_dokumen_transaksi != null){
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
                TransaksiDetailEntity trxDetail = TransaksiDetailEntity.find("reference_id_detail = ?1", detail.id_detail_transaksi).firstResult();
                if(trxDetail!= null){
                    trxDetail.jawaban = target.toString();
                    trxDetail.checklist = null;
                    
                }
            }
            // if(update.nilai_transaksi != null){
            //     detail.nilai = update.nilai_transaksi;
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
    public Response updateStatusPengajuan(@QueryParam("id_transaksi") String id_transaksi, @Context SecurityContext ctx, @QueryParam("catatan") String catatan, @QueryParam("status_approval") String status_approval) {
        if(id_transaksi == null || id_transaksi == ""){
            throw new BadRequestException("id_transaksi harus Di Isi");
        }   
        if(status_approval == null || status_approval == ""){
            throw new BadRequestException("status_approval harus Di Isi");
        }
        // System.out.println(id_pengajuan_bk);
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            TransaksiProyekEntity pengajuanTransaksi = TransaksiProyekEntity.findById(id_transaksi);
            TransaksiProyekDetailPersetujuanEntity getPersetujuan = TransaksiProyekDetailPersetujuanEntity.find("transaksiPersetujuanProyek = ?1 AND id_user = ?2 AND tanggal_persetujuan IS NULL ORDER BY urutan ASC", pengajuanTransaksi, ue.id_user).firstResult();
            if(getPersetujuan != null){
                getPersetujuan.status_approver = status_approval;
                getPersetujuan.tanggal_persetujuan = LocalDateTime.now();
                getPersetujuan.catatan_persetujuan = (catatan != "" || catatan != null)?catatan:"";
                
                if(status_approval.equals("Approve")){
                    // System.out.println(status_approver);
                    List<TransaksiProyekDetailPersetujuanEntity> pengajuanList = TransaksiProyekDetailPersetujuanEntity.find("tanggal_persetujuan IS NULL AND transaksiPersetujuanProyek = ?1", pengajuanTransaksi).list();
                    if(pengajuanList.size() == 0){
                        List<TransaksiDetailProyekEntity> pDetail = TransaksiDetailProyekEntity.find("transaksi = ?1", pengajuanTransaksi).list();
                        TransaksiEntity transaksiParent = new TransaksiEntity();
                        transaksiParent.jenis_transaksi = pengajuanTransaksi.jenis_transaksi;
                        transaksiParent.kode_transaksi = pengajuanTransaksi.kode_transaksi;
                        transaksiParent.tanggal_pengajuan = LocalDate.now();
                        transaksiParent.tempo_pembayaran_after_verified = pengajuanTransaksi.tempo_pembayaran_after_verified;
                        transaksiParent.keterangan = pengajuanTransaksi.keterangan;
                        transaksiParent.user_pengajuan = pengajuanTransaksi.user_pengajuan;
                        transaksiParent.updatedBy = ue;
                        transaksiParent.nilai_invoice = pengajuanTransaksi.nilai_invoice;
                        transaksiParent.ppn = pengajuanTransaksi.ppn;
                        transaksiParent.pph = pengajuanTransaksi.pph;
                        transaksiParent.retensi = pengajuanTransaksi.retensi;
                        transaksiParent.kasbon = pengajuanTransaksi.kasbon;
                        transaksiParent.nilai_invoice_bersih = pengajuanTransaksi.nilai_invoice_bersih;
                        transaksiParent.biaya_potongan_lainnya = pengajuanTransaksi.biaya_potongan_lainnya;
                        transaksiParent.last_updated = LocalDateTime.now();
                        transaksiParent.status_pengajuan = "Pengajuan";
                        transaksiParent.proyek = pengajuanTransaksi.proyek;
                        transaksiParent.transaksi_via = "Proyek";
                        transaksiParent.status_pengajuan = "Pengajuan";
                        transaksiParent.nama_vendor = pengajuanTransaksi.nama_vendor;
                        transaksiParent.kategori = pengajuanTransaksi.kategori;
                        transaksiParent.nomor_invoice = pengajuanTransaksi.nomor_invoice;
                        transaksiParent.no_po_kontrak = pengajuanTransaksi.no_po_kontrak;
                        transaksiParent.tanggal_invoice = pengajuanTransaksi.tanggal_invoice;
                        transaksiParent.reference_id_transaksi_proyek = pengajuanTransaksi.id_transaksi;
                        transaksiParent.persist();
                        for(TransaksiDetailProyekEntity pengajuanDetail: pDetail){
                            TransaksiDetailEntity td = new TransaksiDetailEntity();
                            td.transaksi = transaksiParent;
                            td.pertanyaan = pengajuanDetail.pertanyaan;
                            td.jawaban = pengajuanDetail.jawaban;
                            td.reference_id_detail = pengajuanDetail.id_detail_transaksi;
                            td.persist();
                        }
                        pengajuanTransaksi.status_pengajuan = "Di Kirim Ke HO";
                    }
                }else if(status_approval.equals("Reject")){
                    List<TransaksiProyekDetailPersetujuanEntity> getPersetujuanReject = TransaksiProyekDetailPersetujuanEntity.find("pengajuan_bk = ?1 AND tanggal_persetujuan IS NULL ORDER BY urutan ASC", pengajuanTransaksi).list();
                    for(TransaksiProyekDetailPersetujuanEntity pengajuanReject: getPersetujuanReject){
                        pengajuanReject.tanggal_persetujuan = LocalDateTime.now();
                        pengajuanReject.status_approver = "Reject";
                        pengajuanReject.catatan_persetujuan = "Rejected By "+ue.username;
                    }
               
                }
                
            
                return Response.ok().entity(ResponseHandler.ok("Approver Berhasil", null)).build();
            }else{
                return Response.ok().entity(ResponseHandler.error("Data Persetujuan tidak ada")).build();
            }
        }catch(Exception e){
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/dokumen-file")
    @Produces("application/pdf")
    public Response getDokumen(@QueryParam("id") String id) {
        try {
            TransaksiDetailProyekEntity trx = TransaksiDetailProyekEntity.findById(id);
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
    @Path("/digital-sign")
    public Response digitalSign(@QueryParam("text") String id ){
        try {

            byte[] qrCode =
                    qrCodeService.generateQRCodeImage(
                            id,
                            300,
                            300
                    );

            return Response.ok(qrCode)
                    .type(MediaType.valueOf("image/png"))
                    .build();

        } catch (Exception e) {

            // return Response.serverError().build();
            throw new InternalServerErrorException("cant generate qr code");
        }
    }
}
