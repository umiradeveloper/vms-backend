package org.sim.umira.resources.CostControl;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CostControl.CreateMosDto;
import org.sim.umira.dtos.CostControl.MosDto;
import org.sim.umira.entities.CostControl.MosEntity;
import org.sim.umira.entities.CostControl.MosNewEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.RapaEntity;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/CostControl/MaterialOnSite")
@Secured
public class MosRes {
    @POST
    @Path("/create-mos")
    @Transactional
    public Response createMos(
        @Valid @RequestBody MosDto create
    ){
        try {
            MosNewEntity mos = new MosNewEntity();
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            mos.week = create.week;
            mos.nominal_mos = create.nominal_mos;
            mos.proyek = proyek;
            mos.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Mos Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @PATCH
    @Path("/update-mos")
    @Transactional
    public Response updateMos(
        @Valid @RequestBody MosDto create
    ){
        try {
            MosNewEntity mos = MosNewEntity.findById(create.id_mos);
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            mos.week = create.week;
            mos.nominal_mos = create.nominal_mos;
            mos.proyek = proyek;
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
}
