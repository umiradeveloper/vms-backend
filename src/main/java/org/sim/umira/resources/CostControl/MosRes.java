package org.sim.umira.resources.CostControl;

import java.nio.file.Files;
import java.io.InputStream;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.CostControl.CreateMosDto;
import org.sim.umira.dtos.CostControl.MosDto;
import org.sim.umira.entities.CostControl.MosEntity;
import org.sim.umira.entities.CostControl.MosNewEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.RapaEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;
import java.nio.file.Files;
import java.nio.file.Paths;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/CostControl/MaterialOnSite")
@Secured
public class MosRes {

    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/dokumen-mos");

    @POST
    @Path("/create-mos")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createMos(
        @Valid @MultipartForm MosDto create
    ){
        try {
            MosNewEntity mos = new MosNewEntity();
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            mos.week = create.week;
            mos.nominal_mos = create.nominal_mos;
            mos.proyek = proyek;
            mos.tanggal_awal = create.tanggal_awal;
            mos.tanggal_akhir = create.tanggal_akhir;
            String ext = create.dokumen_upload.fileName().substring(create.dokumen_upload.fileName().lastIndexOf("."));
            String fileName = java.util.UUID.randomUUID() + ext;
            if (!Files.exists(UPLOAD_DIR)) {
                Files.createDirectories(UPLOAD_DIR);
            }
            java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
            Files.copy(
                create.dokumen_upload.uploadedFile(),
                target,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            
            mos.dokumen_upload = target.toString();
            mos.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Mos Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @PATCH
    @Path("/update-mos")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateMos(
        @Valid @MultipartForm MosDto create
    ){
        try {
            MosNewEntity mos = MosNewEntity.findById(create.id_mos);
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            mos.week = create.week;
            mos.nominal_mos = create.nominal_mos;
            mos.proyek = proyek;
            mos.tanggal_awal = create.tanggal_awal;
            mos.tanggal_akhir = create.tanggal_akhir;
            if(create.dokumen_upload != null){
                Files.deleteIfExists(java.nio.file.Path.of(mos.dokumen_upload));
                String ext = create.dokumen_upload.fileName().substring(create.dokumen_upload.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                    create.dokumen_upload.uploadedFile(),
                    target,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                
                mos.dokumen_upload = target.toString();
            }
            // mos.persist();
            return Response.ok().entity(ResponseHandler.ok("Update Mos Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @GET
    @Path("/get-mos")
    @Transactional
    public Response getMosByProyek(
        @Valid @QueryParam("id") String id_proyek
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(id_proyek);
            List<MosNewEntity> mos = MosNewEntity.find("proyek = ?1", proyek).list();
            
            return Response.ok().entity(ResponseHandler.ok("Create Mos Berhasil", mos)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @GET
    @Path("/get-mos-by-id")
    @Transactional
    public Response getMosById(
        @Valid @QueryParam("id") String id_proyek
    ){
        try {
            List<MosNewEntity> mos = MosNewEntity.findById(id_proyek);
            
            return Response.ok().entity(ResponseHandler.ok("get Mos Berhasil", mos)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @DELETE
    @Path("/delete-mos")
    @Transactional
    public Response deleteMos(
        @Valid @QueryParam("id") String id_proyek
    ){
        try {
            Boolean delete = MosNewEntity.deleteById(id_proyek);
            
            return Response.ok().entity(ResponseHandler.ok("Delete Mos Berhasil", delete)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @GET
    @Path("/dokumen-file")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/pdf")
    public Response getFile(
        @QueryParam("id") String id
    ){  
        try {  // direktori saat jar dijalankan
            MosNewEntity mos = MosNewEntity.findById(id);
            InputStream imageStream = Files.newInputStream(Paths.get(mos.dokumen_upload));
            return Response.ok(imageStream).build();
        } catch (Exception e) {
           throw new InternalError("Cant get file");
        }
    }
}

