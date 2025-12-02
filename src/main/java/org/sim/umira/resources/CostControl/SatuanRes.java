package org.sim.umira.resources.CostControl;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CostControl.CreateSatuanDto;
import org.sim.umira.entities.CostControl.SatuanEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/CostControl/Satuan")
@Secured
public class SatuanRes {
    

    @POST
    @Path("/create-satuan")
    @Transactional
    public Response createSatuan(
        @Valid @RequestBody CreateSatuanDto create
    ){
        try {
            SatuanEntity satuan = new SatuanEntity();
            satuan.nama_satuan = create.nama_satuan;
            satuan.kode_satuan = create.kode_satuan;
            satuan.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Satuan Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @GET
    @Path("/get-satuan")
    public Response getSatuan(){
        List<SatuanEntity> satuan = SatuanEntity.listAll();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Satuan Berhasil", satuan)).build();
    }

    @PATCH
    @Path("/update-satuan")
    @Transactional
    public Response updateSatuan(
        @Valid @RequestBody CreateSatuanDto create
    ){
        try {
            SatuanEntity satuan = SatuanEntity.findById(create.id_satuan);
            satuan.nama_satuan = create.nama_satuan;
            satuan.kode_satuan = create.kode_satuan;
            return Response.ok().entity(ResponseHandler.ok("Update Satuan Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @DELETE
    @Path("/delete-satuan")
    @Transactional
    public Response deleteSatuan(
        @QueryParam("id") String id
    ){
        try {
            Boolean satuan = SatuanEntity.deleteById(id);
       
            return Response.ok().entity(ResponseHandler.ok("Delete Satuan Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }
}
