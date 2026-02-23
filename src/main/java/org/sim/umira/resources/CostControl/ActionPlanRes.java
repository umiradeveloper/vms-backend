package org.sim.umira.resources.CostControl;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.CostControl.CreateActionPlanDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.CostControl.ActionPlanEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.ScurveEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/CostControl/ActionPlan")
@Secured
public class ActionPlanRes {
    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/dokumen-action-plan");


    @POST
    @Path("/create-action-plan")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createActionPlan(
        @Valid @MultipartForm CreateActionPlanDto create, @Context SecurityContext ctx
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            ActionPlanEntity cekActionPlan = ActionPlanEntity.find("proyek = ?1 and week = ?2", proyek, create.week).firstResult();
            if(cekActionPlan != null){
                throw new BadRequestException("Action Plan Week "+create.week+" exist");
            }
            ActionPlanEntity ap = new ActionPlanEntity();
            ap.nominal_action_plan = create.nominal_action_plan;
            ap.tanggal_akhir = create.tanggal_akhir;
            ap.tanggal_awal = create.tanggal_awal;
            ap.week = create.week;
            ap.proyek = proyek;
            ap.created_by = ue.id_user;
            ap.created_at = LocalDateTime.now();
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
            ap.url_dokumen = target.toString();
            ap.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Scurve Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalError(e.getMessage());
        }
    }

    @GET
    @Path("/get-action-plan-by-proyek")
    @Transactional
     public Response getActionPlanByProyek(
        @QueryParam("id_proyek") String id_proyek
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(id_proyek);
            List <ActionPlanEntity> list = ActionPlanEntity.find("proyek = ?1", proyek).list();
            return Response.ok().entity(ResponseHandler.ok("get action plan Berhasil", list)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

    @DELETE
    @Path("/delete-action-plan-by-id")
    @Transactional
     public Response deleteScurveById(
        @QueryParam("id") String id
    ){
        try {
            Boolean puList = ActionPlanEntity.deleteById(id); 
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
            ActionPlanEntity pu = ActionPlanEntity.findById(id);
            InputStream imageStream = Files.newInputStream(Paths.get(pu.url_dokumen));
            return Response.ok(imageStream).build();
        } catch (Exception e) {
            e.printStackTrace();
           throw new InternalError("Cant get file");
        }
        
    }


}
