package org.sim.umira.resources.CostControl;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CostControl.CreatePuDto;
import org.sim.umira.dtos.CostControl.CreateProyekDto;
import org.sim.umira.entities.CostControl.PendapatanUsahaEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/CostControl/PendapatanUsaha")
@Secured
public class PendapatanUsahaRes {
    @POST
    @Path("/create-pu")
    @Transactional
     public Response createPu(
        @Valid @RequestBody CreatePuDto create
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            PendapatanUsahaEntity pu = new PendapatanUsahaEntity();
            pu.proyek = proyek;
            pu.week_pu = create.week_pu;
            pu.tanggal_awal = create.tanggal_awal;
            pu.tanggal_akhir = create.tanggal_akhir;
            pu.nominal_pu = create.nominal_pu;
            pu.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Pu Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
    }
    @GET
    @Path("/get-pu-by-proyek")
    @Transactional
     public Response getPuByProyek(
        @QueryParam("id_proyek") String id_proyek
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(id_proyek);
            List<PendapatanUsahaEntity> puList = PendapatanUsahaEntity.find("proyek = ?1", proyek).list(); 
            return Response.ok().entity(ResponseHandler.ok("get Pu Berhasil", puList)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

    @POST
    @Path("/update-pu")
    @Transactional
     public Response updatePu(
        @Valid @RequestBody CreatePuDto create
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            PendapatanUsahaEntity pu = PendapatanUsahaEntity.findById(create.id_pu);
            pu.week_pu = create.week_pu;
            pu.tanggal_awal = create.tanggal_awal;
            pu.tanggal_akhir = create.tanggal_akhir;
            pu.proyek = proyek;
            pu.nominal_pu = create.nominal_pu;
            return Response.ok().entity(ResponseHandler.ok("Update Pu Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
    }

    @DELETE
    @Path("/delete-pu")
    @Transactional
     public Response deletePu(
        @QueryParam("id") String id
    ){ 
        try {
            boolean Delete = PendapatanUsahaEntity.deleteById(id);
            return Response.ok().entity(ResponseHandler.ok("Hapus Pu Berhasil", Delete)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
    }
}
