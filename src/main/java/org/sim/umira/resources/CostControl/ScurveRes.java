package org.sim.umira.resources.CostControl;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.CostControl.CreateScurveDto;
import org.sim.umira.entities.CostControl.PendapatanUsahaEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.ScurveEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

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

@Path("/CostControl/Scurve")
@Secured
public class ScurveRes {
    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/dokumen-scurve");

    @POST
    @Path("/create-scurve")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createScurve(
        @Valid @MultipartForm CreateScurveDto create
    ){
        try {
            ScurveEntity scurve = new ScurveEntity();
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            scurve.week = create.week;
            scurve.nominal_scurve = create.nominal_scurve;
            scurve.tanggal_awal = create.tanggal_awal;
            scurve.tanggal_akhir = create.tanggal_akhir;
            scurve.proyek = proyek;
            String ext = create.file_dokumen.fileName().substring(create.file_dokumen.fileName().lastIndexOf("."));
            String fileName = java.util.UUID.randomUUID() + ext;
            if (!Files.exists(UPLOAD_DIR)) {
                Files.createDirectories(UPLOAD_DIR);
            }
            java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
            Files.copy(
                create.file_dokumen.uploadedFile(),
                target,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            scurve.url_dokumen = target.toString();
            scurve.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Scurve Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalError(e.getMessage());
        }
    }

    @PATCH
    @Path("/update-scurve")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateScurve(
        @Valid @MultipartForm CreateScurveDto create
    ){
        try {
            ScurveEntity scurve = ScurveEntity.findById(create.id_scurve);
            // ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            scurve.week = create.week;
            scurve.nominal_scurve = create.nominal_scurve;
            scurve.tanggal_awal = create.tanggal_awal;
            scurve.tanggal_akhir = create.tanggal_akhir;
            // scurve.proyek = proyek;
            if(create.file_dokumen != null){
                String ext = create.file_dokumen.fileName().substring(create.file_dokumen.fileName().lastIndexOf("."));
                String fileName = java.util.UUID.randomUUID() + ext;
                if (!Files.exists(UPLOAD_DIR)) {
                    Files.createDirectories(UPLOAD_DIR);
                }
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                    create.file_dokumen.uploadedFile(),
                    target,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                scurve.url_dokumen = target.toString();
            }
            
            // scurve.persist();
            return Response.ok().entity(ResponseHandler.ok("update Scurve Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalError(e.getMessage());
        }
    }

    @GET
    @Path("/get-scurve-by-proyek")
    @Transactional
     public Response getScurveByProyek(
        @QueryParam("id_proyek") String id_proyek
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(id_proyek);
            List<ScurveEntity> puList = ScurveEntity.find("proyek = ?1", proyek).list(); 
            return Response.ok().entity(ResponseHandler.ok("get Scurve Berhasil", puList)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

    @GET
    @Path("/get-scurve-by-id")
    @Transactional
     public Response getScurveById(
        @QueryParam("id") String id
    ){
        try {
            ScurveEntity puList = ScurveEntity.findById(id); 
            return Response.ok().entity(ResponseHandler.ok("get Scurve Berhasil", puList)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

    @DELETE
    @Path("/delete-scurve-by-id")
    @Transactional
     public Response deleteScurveById(
        @QueryParam("id") String id
    ){
        try {
            Boolean puList = ScurveEntity.deleteById(id); 
            return Response.ok().entity(ResponseHandler.ok("delete Berhasil", puList)).build();
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
            ScurveEntity pu = ScurveEntity.findById(id);
            InputStream imageStream = Files.newInputStream(Paths.get(pu.url_dokumen));
            return Response.ok(imageStream).build();
        } catch (Exception e) {
            e.printStackTrace();
           throw new InternalError("Cant get file");
        }
        
    }

}
