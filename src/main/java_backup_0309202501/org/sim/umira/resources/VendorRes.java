package org.sim.umira.resources;



import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.ApprovalPengajuanDto;
import org.sim.umira.dtos.CreateVendorDto;
import org.sim.umira.dtos.CreateVendorUploadDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.VmsVendorDetailEntity;
import org.sim.umira.entities.VmsVendorEntity;
import org.sim.umira.entities.VmsVendorMstDokumenEntity;
import org.sim.umira.entities.VmsVendorMstKualifikasi;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/vms")
@Secured
// @Consumes(MediaType.MULTIPART_FORM_DATA)
public class VendorRes {

   


    @POST
    @Path("/create-vendor/upload")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVendorUpload(
        @MultipartForm CreateVendorUploadDto create
    ){
       VmsVendorEntity vm = VmsVendorEntity.findById(create.id_vendor);
       VmsVendorMstDokumenEntity vd = VmsVendorMstDokumenEntity.findById(create.id_dokumen);
    //    System.out.println(create.id_dokumen);
        System.out.println(create.files.size());
       create.files.forEach(file -> {
            System.out.println(file.fileName());
            String ext = file.fileName().substring(file.fileName().lastIndexOf("."));
            String randomFileName = UUID.randomUUID().toString() + ext;
            try {
                java.nio.file.Path target = java.nio.file.Path.of("uploads", randomFileName);
                Files.createDirectories(target.getParent());
                Files.copy(file.uploadedFile(), target, StandardCopyOption.REPLACE_EXISTING);
                VmsVendorDetailEntity ve = new VmsVendorDetailEntity();
                ve.vendor = vm;
                // ve.dokumen = vd;
                ve.nama_dokumen = vd.nama_dokumen;
                ve.url_dokumen = target.toString();
                ve.persist();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return Response.ok().entity(ResponseHandler.ok("Create Vendor Berhasil", null)).build();
    }
    @POST
    @Path("/update-vendor/upload")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateVendorUpload(
        @MultipartForm CreateVendorUploadDto create
    ){
       VmsVendorEntity vm = VmsVendorEntity.findById(create.id_vendor);
       VmsVendorMstDokumenEntity vd = VmsVendorMstDokumenEntity.findById(create.id_dokumen);
       List<CompletableFuture<Void>> tasks = new ArrayList<>();
        if(create.files.size() > 0){
            List <VmsVendorDetailEntity> vmsDetail = VmsVendorDetailEntity.find("vendor = ?1", vm).list();
            System.out.println(vmsDetail.size());
            if(vmsDetail.size() > 0){
                vmsDetail.forEach(detail -> {
                    tasks.add(CompletableFuture.runAsync(() -> {
                        try {
                            Files.deleteIfExists(java.nio.file.Path.of("uploads", detail.url_dokumen));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                         
                    }));
                //    detail.delete();
                });
            }
            long delete = VmsVendorDetailEntity.delete("vendor = ?1", vm);
            System.out.println(delete);
            create.files.forEach(file -> {
                 
                    System.out.println(file.fileName());
                    String ext = file.fileName().substring(file.fileName().lastIndexOf("."));
                    String randomFileName = UUID.randomUUID().toString() + ext;
                    
                        java.nio.file.Path target = java.nio.file.Path.of("uploads", randomFileName);
                        tasks.add(CompletableFuture.runAsync(() -> {
                            try {
                                Files.createDirectories(target.getParent());
                                Files.copy(file.uploadedFile(), target, StandardCopyOption.REPLACE_EXISTING);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                         }));
                        VmsVendorDetailEntity ve = new VmsVendorDetailEntity();
                        ve.vendor = vm;
                        // ve.dokumen = vd;
                        ve.nama_dokumen = vd.nama_dokumen;
                        ve.url_dokumen = target.toString();
                        ve.persist();
                    
               
            });
        }
        CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();
       
        return Response.ok().entity(ResponseHandler.ok("Create Vendor Berhasil", null)).build();
    }

    @POST
    @Path("/create-vendor")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVendor(@Valid @RequestBody CreateVendorDto create, @Context SecurityContext ctx){
        VmsVendorEntity vm = new VmsVendorEntity();
        VmsVendorMstKualifikasi ku = VmsVendorMstKualifikasi.findById(create.id_kualifikasi_usaha);
        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        // System.out.println();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        LocalDateTime now = LocalDateTime.now();
        String pengajuan = "VMS.Pengajuan."+now.format(formatter);
        vm.nama_perusahaan = create.nama_perusahaan;
        vm.user = ue;
        vm.id_pengajuan = pengajuan;
        vm.tanggal_pengajuan = LocalDateTime.now();
        vm.kualifikasi_usaha = ku;
        vm.klasifikasi_usaha = create.klasifikasi_usaha;
        vm.alamat_perusahaan = create.alamat_perusahaan;
        vm.kategori = create.kategori;
        vm.spesialisasi = create.spesialis;
        vm.nama_pic = create.nama_pic;
        vm.email_pic = create.email_pic;
        vm.no_hp_pic = create.no_hp_pic;
        vm.nama_direktur = create.nama_direktur;
        vm.email_direktur = create.email_direktur;
        vm.no_hp_direktur = create.no_hp_direktur;
        vm.website = create.website;
        vm.persist();
        return Response.ok().entity(ResponseHandler.ok("Create Vendor Berhasil", vm)).build();
    }


    @GET
    @Path("/pengajuan-vendor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPengajuan(){
        List<VmsVendorEntity> vv = VmsVendorEntity.find("isApproval IS NULL").list();

        return Response.ok().entity(ResponseHandler.ok("Inquiry Berhasil", vv)).build();

    }

    @GET
    @Path("/draft-pengajuan-vendor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDraft(@Context SecurityContext ctx){
        // List<VmsVendorEntity> vv = VmsVendorEntity.find("isApproval IS NULL").list();
        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        System.out.println((ue.role.kode_role));
        List<VmsVendorEntity> vv;
        if(ue.role.kode_role.equals("99")){
            vv = VmsVendorEntity.find("isApproval = ?1 ", 0).list();
        }else{
            vv = VmsVendorEntity.find("user = ?1 AND isApproval = ?2", ue, 0).list();
        }

        return Response.ok().entity(ResponseHandler.ok("Inquiry Berhasil", vv)).build();

    }

    @GET
    @Path("/draft-pengajuan-vendor-id")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDraftById(@QueryParam("id") String id, @Context SecurityContext ctx){
        // List<VmsVendorEntity> vv = VmsVendorEntity.find("isApproval IS NULL").list();
        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        System.out.println((ue.role.kode_role));
        VmsVendorEntity vv;
        if(ue.role.kode_role.equals("99")){
            vv = VmsVendorEntity.find("isApproval = ?1 AND id_vendor = ?2", 0, id).firstResult();
        }else{
            vv = VmsVendorEntity.find("user = ?1 AND isApproval = ?2 AND id_vendor = ?3", ue, 0, id).firstResult();
        }

        return Response.ok().entity(ResponseHandler.ok("Inquiry Berhasil", vv)).build();

    }

    @POST
    @Path("/update-pengajuan-vendor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePengajuan(@QueryParam("id") String id,@Valid @RequestBody CreateVendorDto create, @Context SecurityContext ctx){
        // List<VmsVendorEntity> vv = VmsVendorEntity.find("isApproval IS NULL").list();
        VmsVendorEntity vm = VmsVendorEntity.findById(id);
        VmsVendorMstKualifikasi ku = VmsVendorMstKualifikasi.findById(create.id_kualifikasi_usaha);
        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
     
        vm.nama_perusahaan = create.nama_perusahaan;
        vm.user = ue;
        // vm.id_pengajuan = pengajuan;
        // vm.tanggal_pengajuan = LocalDateTime.now();
        vm.kualifikasi_usaha = ku;
        vm.klasifikasi_usaha = create.klasifikasi_usaha;
        vm.alamat_perusahaan = create.alamat_perusahaan;
        vm.kategori = create.kategori;
        vm.spesialisasi = create.spesialis;
        vm.nama_pic = create.nama_pic;
        vm.email_pic = create.email_pic;
        vm.no_hp_pic = create.no_hp_pic;
        vm.nama_direktur = create.nama_direktur;
        vm.email_direktur = create.email_direktur;
        vm.no_hp_direktur = create.no_hp_direktur;
        vm.website = create.website;
        // vm.persist();
        return Response.ok().entity(ResponseHandler.ok("Update Vendor Berhasil", vm)).build();

    }

    @PATCH
    @Path("/pengajuan-vendor/approval")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateApproval(
        @QueryParam("id") String id, @RequestBody ApprovalPengajuanDto approval, @Context SecurityContext ctx
    ){
        VmsVendorEntity ve = VmsVendorEntity.find("id_vendor = ?1", id).firstResult();
        
        // System.out.println(ve.email_pic);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        LocalDateTime now = LocalDateTime.now();
        LocalDate nowDate = LocalDate.now();
        ve.approvedBy = ctx.getUserPrincipal().getName();
        ve.approvedAt = LocalDateTime.now();
        ve.isApproval = approval.isApproval;
        if(approval.isApproval == 1){
            
            ve.no_skt = "SKT."+now.format(formatter);
            ve.tanggal_awal_skt = nowDate;
            ve.tanggal_akhir_skt = nowDate.plusYears(2);
        }
        if(approval.isApproval == 0){
            ve.catatan = approval.catatan;
        }
        
        

        return Response.ok().entity(ResponseHandler.ok("approval berhasil", ve)).build();
    }

    @GET
    @Path("/draft-pengajuan-ajukan")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateApprovalDraft(
        @QueryParam("id") String id, @Context SecurityContext ctx
    ){
        VmsVendorEntity ve = VmsVendorEntity.find("id_vendor = ?1", id).firstResult();
        System.out.println(ve.alamat_perusahaan);
        // System.out.println(ve.email_pic);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
        LocalDateTime now = LocalDateTime.now();
        LocalDate nowDate = LocalDate.now();
        // ve.approvedBy = ctx.getUserPrincipal().getName();
        ve.tanggal_pengajuan = LocalDateTime.now();
        ve.isApproval = null;
        
        

        return Response.ok().entity(ResponseHandler.ok("approval berhasil", ve)).build();
    }

    @GET
    @Path("/monitoring-pengajuan")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMonitoring(
        @Context SecurityContext ctx
    ){
        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        String query = "";
        System.out.println((ue.role.kode_role));
        List<VmsVendorEntity> vv;
        if(ue.role.kode_role.equals("99")){
            vv = VmsVendorEntity.listAll();
        }else{
            vv = VmsVendorEntity.find("user = ?1", ue).list();
        }
        

        return Response.ok().entity(ResponseHandler.ok("Inquiry Berhasil", vv)).build();

    }

    @GET
    @Path("/list-vendor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVendor(
        @Context SecurityContext ctx
    ){
        
        List<VmsVendorEntity> vv = VmsVendorEntity.find("isApproval = ?1 GROUP BY nama_perusahaan", 1).list();
        

        return Response.ok().entity(ResponseHandler.ok("Inquiry Berhasil", vv)).build();

    }

    

    @GET
    @Path("/dokumen-file")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/pdf")
    public Response getFile(
        @QueryParam("id") String id
    ){
        VmsVendorDetailEntity vme = VmsVendorDetailEntity.findById(id);
        System.out.println(vme.url_dokumen);
        try {
            // String url = "uploads/"+vme.url_dokumen;
            InputStream imageStream = Files.newInputStream(Paths.get(vme.url_dokumen));
            return Response.ok(imageStream).build();
        } catch (Exception e) {
           throw new InternalError("Cant get file");
        }
        
    }

    @DELETE
    @Path("/delete-pengajuan")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response deleteUploadById(@QueryParam("id") String id){
        
        try {
            // VmsVendorEntity vv = VmsVendorEntity.findById(id);
            // VmsVendorDetailEntity.delete("vendor = ?1", vv);
            VmsVendorEntity.deleteById(id);
            
            return Response.ok().entity(ResponseHandler.ok("Delete Berhasil",null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalError(e.getMessage());
        }
        





        

    }

    
}
