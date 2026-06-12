package org.sim.umira.resources.SIMAsset;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.SimAsset.CreateAssetDto;
import org.sim.umira.dtos.SimAsset.CreateDisposalAssetDto;
import org.sim.umira.dtos.SimAsset.CreateMaintenanceAssetDto;
import org.sim.umira.dtos.SimAsset.CreateMutasiAssetDto;
import org.sim.umira.dtos.SimAsset.UpdateAssetDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.ChecklistTransaksi.CountTransaksiEntity;
import org.sim.umira.entities.SIMAsset.SimAssetCounterEntity;
import org.sim.umira.entities.SIMAsset.SimAssetDisposalEntity;
import org.sim.umira.entities.SIMAsset.SimAssetEntity;
import org.sim.umira.entities.SIMAsset.SimAssetKategoriEntity;
import org.sim.umira.entities.SIMAsset.SimAssetMaintenanceEntity;
import org.sim.umira.entities.SIMAsset.SimAssetMutasiEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;
import org.sim.umira.kafka.KafkaProducers;
import org.sim.umira.kafka.DTO.UploadEventDto;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/asset-manajemen")
@Secured
public class AssetManajemenRes {
    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/dokumen-asset");

    @Inject
    KafkaProducers kafkaProducers;

    @POST
    @Path("/create-asset")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createAsset(
            @Valid @MultipartForm CreateAssetDto create, @Context SecurityContext ctx) {

        UserEntity ue = UserEntity.findById(create.id_user);
        String year = String.valueOf(LocalDate.now().getYear());
        SimAssetCounterEntity kodeAsset = SimAssetCounterEntity.find("kode = ?1 AND year = ?2", create.kode_asset, year).firstResult();
        String kode;
        if (kodeAsset == null) {
            SimAssetCounterEntity countSave = new SimAssetCounterEntity();
            countSave.kode = create.kode_asset;
            countSave.year = year;
            countSave.count = 1;
            countSave.persist();
            kode = create.kode_asset + year + "001";
        } else {
            // countSave.kode_transaksi = form.kode_transaksi;
            kodeAsset.count = kodeAsset.count + 1;
            kodeAsset.persist();
            kode = create.kode_asset + year
                    + String.format("%03d", kodeAsset.count);
        }
        try {
            // ReimbursementEntity reimbursement = new ReimbursementEntity();

            SimAssetEntity asset = new SimAssetEntity();
            asset.user_pemilik = ue;
            asset.deskripsi_asset = create.deskripsi_asset;
            asset.kategori = create.kategori;
            asset.kode_asset = kode;
            asset.kondisi = create.kondisi;
            asset.lokasi = create.lokasi;
            asset.nama_asset = create.nama_asset;
            asset.nilai_perolehan = create.nilai_perolehan;
            asset.nilai_saat_ini = create.nilai_saat_ini;
            asset.status_asset = create.status_asset;
            asset.tanggal_perolehan = create.tanggal_perolehan;
            asset.umur_ekonomis = create.umur_ekonomis;

            if (create.foto != null && create.foto.size() > 0) {
                String ext = create.foto.fileName()
                        .substring(create.foto.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                        create.foto.uploadedFile(),
                        target,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // kafkaProducers.uploadDoc(new UploadEventDto("uploads/dokumen-asset", fileName, target.toString()));
                asset.foto_url = target.toString();
                // reimbursement.dokumen_reimbursement = target.toString();
            }

            asset.persist();

            return Response.ok().entity(ResponseHandler.ok("Create Asset Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @POST
    @Path("/update-asset")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateAsset(
            @Valid @MultipartForm UpdateAssetDto create, @Context SecurityContext ctx) {

        UserEntity ue = UserEntity.findById(create.id_user);
        int lengthCode = create.kode_asset.length();
        String kode;
        // System.out.println(lengthCode);
        if(lengthCode == 3){
              String year = String.valueOf(LocalDate.now().getYear());
            SimAssetCounterEntity kodeAsset = SimAssetCounterEntity.find("kode = ?1 AND year = ?2", create.kode_asset, year).firstResult();
            
            if (kodeAsset == null) {
                SimAssetCounterEntity countSave = new SimAssetCounterEntity();
                countSave.kode = create.kode_asset;
                countSave.year = year;
                countSave.count = 1;
                countSave.persist();
                kode = create.kode_asset + year + "001";
            } else {
                // countSave.kode_transaksi = form.kode_transaksi;
                kodeAsset.count = kodeAsset.count + 1;
                kodeAsset.persist();
                kode = create.kode_asset + year
                        + String.format("%03d", kodeAsset.count);
            }
        }else{
            kode = create.kode_asset;
        }
      
        try {
            // ReimbursementEntity reimbursement = new ReimbursementEntity();

            SimAssetEntity asset = SimAssetEntity.findById(create.id_asset);
            asset.user_pemilik = ue;
            asset.deskripsi_asset = create.deskripsi_asset;
            asset.kategori = create.kategori;
            asset.kode_asset = kode;
            asset.kondisi = create.kondisi;
            asset.lokasi = create.lokasi;
            asset.nama_asset = create.nama_asset;
            asset.nilai_perolehan = create.nilai_perolehan;
            asset.nilai_saat_ini = create.nilai_saat_ini;
            asset.status_asset = create.status_asset;
            asset.tanggal_perolehan = create.tanggal_perolehan;
            asset.umur_ekonomis = create.umur_ekonomis;

            if (create.foto != null && create.foto.size() > 0) {
                String ext = create.foto.fileName()
                        .substring(create.foto.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                        create.foto.uploadedFile(),
                        target,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // kafkaProducers.uploadDoc(new UploadEventDto("uploads/dokumen-asset", fileName, target.toString()));
                asset.foto_url = target.toString();
                // reimbursement.dokumen_reimbursement = target.toString();
            }

            return Response.ok().entity(ResponseHandler.ok("Update Asset Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-asset")
    public Response getAsset() {
        try {
            List<SimAssetEntity> asset = SimAssetEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Get All Asset", asset)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-kategori")
    public Response getKategori() {
        try {
            // List<String> ls = List.of("elektronik", "furniture", "kendaraan", "bangunan", "peralatan", "lainnya");
            // List<String> ls = SimAssetKategoriEntity.find("SELECT nama_kategori FROM SimAssetKategoriEntity").project(String.class).list();
            List<SimAssetKategoriEntity> ls = SimAssetKategoriEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Get Kategori", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-kondisi")
    public Response getKondisi() {
        try {
            List<String> ls = List.of("baik", "rusak_ringan", "rusak_berat", "tidak_layak");
            return Response.ok().entity(ResponseHandler.ok("Get Kondisi", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-status")
    public Response getStatus() {
        try {
            List<String> ls = List.of("aktif", "dalam_pemeliharaan", "dalam_mutasi", "diusulkan_hapus", "dihapus");
            return Response.ok().entity(ResponseHandler.ok("Get Kondisi", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/foto-asset")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({
            "image/jpeg",
            "image/png"
    })
    public Response getFile(
            @QueryParam("id") String id) {
        try { // direktori saat jar dijalankan
            SimAssetEntity asset = SimAssetEntity.findById(id);
            System.out.println(asset.foto_url);
            InputStream imageStream = Files.newInputStream(Paths.get(asset.foto_url));
            return Response.ok(imageStream).build();
        } catch (Exception e) {
            throw new InternalError("Cant get file");
        }

    }

    @GET
    @Path("/get-my-asset")
    public Response getMyAsset(@Context SecurityContext ctx) {
        try {
            UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<SimAssetEntity> asset;
            if(userSession.role.kode_role == "99"){
                asset = SimAssetEntity.find("status_asset = ?1", "aktif").list();
            }else{
                asset = SimAssetEntity.find("user_pemilik = ?1 AND status_asset = ?2", userSession, "aktif").list();
            }
             
            return Response.ok().entity(ResponseHandler.ok("Get My Asset", asset)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }



    // Mutasi Asset begin

    @POST
    @Path("/create-mutasi-asset")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createMutasi(
            @Valid @MultipartForm CreateMutasiAssetDto create, @Context SecurityContext ctx) {

        UserEntity userTujuan = UserEntity.findById(create.pic_tujuan);
        UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        try {
            SimAssetEntity asset = SimAssetEntity.findById(create.id_asset);
            asset.status_asset = "dalam_mutasi";
            SimAssetMutasiEntity mutasiAsset = new SimAssetMutasiEntity();
            mutasiAsset.asset = asset;
            mutasiAsset.alasan_mutasi = create.alasan_mutasi;
            mutasiAsset.lokasi_asal = create.lokasi_asal;
            mutasiAsset.lokasi_tujuan = create.lokasi_tujuan;
            mutasiAsset.pic_sebelum = userSession;
            mutasiAsset.pic_tujuan = userTujuan;
            mutasiAsset.tanggal_mutasi = LocalDateTime.now();
            

            if (create.dokumen_referensi != null && create.dokumen_referensi.size() > 0) {
                String ext = create.dokumen_referensi.fileName()
                        .substring(create.dokumen_referensi.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                        create.dokumen_referensi.uploadedFile(),
                        target,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // kafkaProducers.uploadDoc(new UploadEventDto("uploads/dokumen-asset", fileName, target.toString()));
                // asset.foto_url = target.toString();
                mutasiAsset.dokumen_referensi = target.toString();
                // reimbursement.dokumen_reimbursement = target.toString();
            }
            mutasiAsset.persist();

            // asset.persist();

            return Response.ok().entity(ResponseHandler.ok("Pengajuan Mutasi Asset Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-mutasi-asset")
    public Response getMutasiAsset(@Context SecurityContext ctx) {
        try {
            UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<SimAssetMutasiEntity> ls = SimAssetMutasiEntity.find("pic_sebelum = ?1", userSession).list();
            return Response.ok().entity(ResponseHandler.ok("Get mutasi asset", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-approval-mutasi-asset")
    public Response getApprovalMutasiAsset(@Context SecurityContext ctx) {
        try {
            UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<SimAssetMutasiEntity> ls = SimAssetMutasiEntity.find("pic_tujuan = ?1 AND tanggal_penerimaan IS NULL", userSession).list();
            return Response.ok().entity(ResponseHandler.ok("Get Approval mutasi asset", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    @PATCH
    @Path("/update-approval-mutasi-asset")
    @Transactional
    public Response updateApprovalMutasiAsset(@QueryParam("id") String id, @QueryParam("status_approval") String status_approval, @Context SecurityContext ctx) {
        try {
            SimAssetMutasiEntity mutasi = SimAssetMutasiEntity.findById(id);
            SimAssetEntity assetUpdate = SimAssetEntity.findById(mutasi.asset.id_asset);

            switch (status_approval) {
                case "approve":
                    mutasi.tanggal_penerimaan = LocalDateTime.now();
                    mutasi.status_mutasi = status_approval;
                    assetUpdate.user_pemilik = mutasi.pic_tujuan;
                    assetUpdate.lokasi = mutasi.lokasi_tujuan;
                    assetUpdate.status_asset = "aktif";
                    break;
                case "reject":
                    mutasi.tanggal_penerimaan = LocalDateTime.now();
                    mutasi.status_mutasi = status_approval;
                    assetUpdate.status_asset = "aktif";
                    break;
                default:

                    break;
            }
            return Response.ok().entity(ResponseHandler.ok("update Approval mutasi asset", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    // end mutasi asset


    //begin maintenance asset

    @POST
    @Path("/create-maintenance-asset")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createMaintenanceAsset(
            @Valid @MultipartForm CreateMaintenanceAssetDto create, @Context SecurityContext ctx) {

        // UserEntity ue = UserEntity.findById(create.id_user);
        try {
            // ReimbursementEntity reimbursement = new ReimbursementEntity();
            SimAssetEntity asset = SimAssetEntity.findById(create.id_asset);

            SimAssetMaintenanceEntity assetMaintenance = new SimAssetMaintenanceEntity();
            assetMaintenance.asset = asset;
            assetMaintenance.biaya = create.biaya;
            assetMaintenance.deskripsi = create.deskripsi;
            assetMaintenance.kondisi_setelah = create.kondisi_setelah;
            assetMaintenance.status_maintenance = create.status_maintenance;
            assetMaintenance.tanggal_maintenance = create.tanggal_maintenance;
            assetMaintenance.tanggal_selesai = create.tanggal_selesai;
            assetMaintenance.tipe_maintenance = create.tipe_maintenance;
            assetMaintenance.persist();

            // if (create.foto != null && create.foto.size() > 0) {
            //     String ext = create.foto.fileName()
            //             .substring(create.foto.fileName().lastIndexOf("."));
            //     String fileName = java.util.UUID.randomUUID() + ext;
            //     if (!Files.exists(UPLOAD_DIR)) {
            //         Files.createDirectories(UPLOAD_DIR);
            //     }
            //     java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
            //     Files.copy(
            //             create.foto.uploadedFile(),
            //             target,
            //             java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            //     // kafkaProducers.uploadDoc(new UploadEventDto("uploads/dokumen-asset", fileName, target.toString()));
            //     asset.foto_url = target.toString();
            //     // reimbursement.dokumen_reimbursement = target.toString();
            // }

            asset.persist();

            return Response.ok().entity(ResponseHandler.ok("Create Asset Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @POST
    @Path("/update-maintenance-asset")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateMaintenanceAsset(
            @Valid @MultipartForm CreateMaintenanceAssetDto create, @Context SecurityContext ctx) {

        // UserEntity ue = UserEntity.findById(create.id_user);
        try {
            // ReimbursementEntity reimbursement = new ReimbursementEntity();
            SimAssetEntity asset = SimAssetEntity.findById(create.id_asset);
            switch (create.status_maintenance) {
                case "Dijadwalkan":
                    asset.status_asset = "dalam_pemeliharaan";
                    break;
                case "dalam_proses":
                    asset.status_asset = "dalam_pemeliharaan";
                    break;
                default:
                    asset.status_asset = "aktif";
                    break;
            }
            

            SimAssetMaintenanceEntity assetMaintenance = SimAssetMaintenanceEntity.findById(create.id_maintenance_asset);
            assetMaintenance.asset = asset;
            assetMaintenance.biaya = create.biaya;
            assetMaintenance.deskripsi = create.deskripsi;
            assetMaintenance.kondisi_setelah = create.kondisi_setelah;
            assetMaintenance.status_maintenance = create.status_maintenance;
            assetMaintenance.tanggal_maintenance = create.tanggal_maintenance;
            assetMaintenance.tanggal_selesai = create.tanggal_selesai;
            assetMaintenance.tipe_maintenance = create.tipe_maintenance;
            
            // assetMaintenance.persist();

            // if (create.foto != null && create.foto.size() > 0) {
            //     String ext = create.foto.fileName()
            //             .substring(create.foto.fileName().lastIndexOf("."));
            //     String fileName = java.util.UUID.randomUUID() + ext;
            //     if (!Files.exists(UPLOAD_DIR)) {
            //         Files.createDirectories(UPLOAD_DIR);
            //     }
            //     java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
            //     Files.copy(
            //             create.foto.uploadedFile(),
            //             target,
            //             java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            //     // kafkaProducers.uploadDoc(new UploadEventDto("uploads/dokumen-asset", fileName, target.toString()));
            //     asset.foto_url = target.toString();
            //     // reimbursement.dokumen_reimbursement = target.toString();
            // }

            // asset.persist();

            return Response.ok().entity(ResponseHandler.ok("Update Maintenance Asset Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-maintenance-asset")
    public Response getMaintenanceAsset(@Context SecurityContext ctx) {
        try {
            // UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<SimAssetMaintenanceEntity> ls = SimAssetMaintenanceEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Get maintenance asset", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-maintenance-kondisi")
    public Response getMaintenanceKondisi(@Context SecurityContext ctx) {
        try {
            // UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<String> ls = List.of("baik","rusak_ringan","rusak_berat","tidak_layak");
            return Response.ok().entity(ResponseHandler.ok("Get maintenance kondisi asset", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-maintenance-tipe")
    public Response getMaintenanceTipe(@Context SecurityContext ctx) {
        try {
            // UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<String> ls = List.of("preventif","korektif");
            return Response.ok().entity(ResponseHandler.ok("Get maintenance tipe asset", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-maintenance-status")
    public Response getMaintenanceStatus(@Context SecurityContext ctx) {
        try {
            // UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<String> ls = List.of( "dijadwalkan","dalam_proses","selesai","dibatalkan");
            return Response.ok().entity(ResponseHandler.ok("Get maintenance status asset", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    // end maintenance 


    // begin disposal

    @POST
    @Path("/create-disposal-asset")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createDisposalAsset(
            @Valid @MultipartForm CreateDisposalAssetDto create, @Context SecurityContext ctx) {

        UserEntity ue = UserEntity.findById(create.id_user_approval);
        UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        try {
            // ReimbursementEntity reimbursement = new ReimbursementEntity();
            SimAssetEntity asset = SimAssetEntity.findById(create.id_asset);
            asset.status_asset = "diusulkan_hapus";
            

            SimAssetDisposalEntity disposal = new SimAssetDisposalEntity();

            disposal.alasan = create.alasan;
            disposal.asset = asset;
            disposal.keterangan = create.keterangan;
            disposal.metode_penghapusan = create.metode_penghapusan;
            disposal.nilai_sisa = create.nilai_sisa;
            disposal.status_disposal = create.status_disposal;
            disposal.tanggal_pengajuan = LocalDate.now();
            disposal.user_pengajuan = userSession;
            disposal.user_approval = ue;

            disposal.persist();
            
            // assetMaintenance.persist();

            // if (create.foto != null && create.foto.size() > 0) {
            //     String ext = create.foto.fileName()
            //             .substring(create.foto.fileName().lastIndexOf("."));
            //     String fileName = java.util.UUID.randomUUID() + ext;
            //     if (!Files.exists(UPLOAD_DIR)) {
            //         Files.createDirectories(UPLOAD_DIR);
            //     }
            //     java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
            //     Files.copy(
            //             create.foto.uploadedFile(),
            //             target,
            //             java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            //     // kafkaProducers.uploadDoc(new UploadEventDto("uploads/dokumen-asset", fileName, target.toString()));
            //     asset.foto_url = target.toString();
            //     // reimbursement.dokumen_reimbursement = target.toString();
            // }

            // asset.persist();

            return Response.ok().entity(ResponseHandler.ok("Create pengajuan disposal berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    @GET
    @Path("/get-disposal-alasan")
    public Response getDisposalAlasan(@Context SecurityContext ctx) {
        try {
            // UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<String> ls = List.of("rusak_total","hilang","usang","tidak_efisien","lainnya");
            return Response.ok().entity(ResponseHandler.ok("Get Disposal Alasan", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-disposal-metode")
    public Response getDisposalMetode(@Context SecurityContext ctx) {
        try {
            // UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<String> ls = List.of( "lelang","pemusnahan","hibah","tukar_tambah");
            return Response.ok().entity(ResponseHandler.ok("Get Disposal metode", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

     @GET
    @Path("/get-disposal-status")
    public Response getDisposalStatus(@Context SecurityContext ctx) {
        try {
            // UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<String> ls = List.of("menunggu_kajian","disetujui","ditolak","selesai");
            return Response.ok().entity(ResponseHandler.ok("Get Disposal metode", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    @GET
    @Path("/get-disposal-asset")
    public Response getDisposalAsset(@Context SecurityContext ctx) {
        try {
            // UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<SimAssetDisposalEntity> ls = SimAssetDisposalEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Get disposal asset", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    @GET
    @Path("/get-approval-disposal-asset")
    public Response getApprovalDisposalAsset(@Context SecurityContext ctx) {
        try {
            UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            List<SimAssetDisposalEntity> ls = SimAssetDisposalEntity.find("user_approval = ?1 AND tanggal_approval IS NULL", userSession).list();
            return Response.ok().entity(ResponseHandler.ok("Get approval disposal asset", ls)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @PATCH
    @Path("/update-approval-disposal-asset")
    @Transactional
    public Response updateApprovalDisposalAsset(@QueryParam("id") String id, @QueryParam("status_approval") String status_approval, @Context SecurityContext ctx) {
        try {
            UserEntity userSession = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            SimAssetDisposalEntity disposal = SimAssetDisposalEntity.findById(id);
            SimAssetEntity asset = SimAssetEntity.findById(disposal.asset.id_asset);
            switch (status_approval) {
                case "approve":
                    disposal.tanggal_approval = LocalDate.now();
                    disposal.status_disposal = "Disetujui";
                    disposal.user_approval = userSession;
                    asset.status_asset = "dihapus";
                    break;
                case "reject":
                    disposal.tanggal_approval = LocalDate.now();
                    disposal.status_disposal = "DiTolak";
                    disposal.user_approval = userSession;
                    asset.status_asset = "aktif";
                    break;
                default:
                    disposal.tanggal_approval = LocalDate.now();
                    disposal.status_disposal = "-";
                    break;
            }
            // List<SimAssetDisposalEntity> ls = SimAssetDisposalEntity.find("user_approval = ?1 AND tanggal_approval IS NULL", userSession).list();
            return Response.ok().entity(ResponseHandler.ok("Get approval disposal asset", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

}
