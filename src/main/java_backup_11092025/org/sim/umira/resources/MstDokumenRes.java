package org.sim.umira.resources;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CreateDokumenDto;
import org.sim.umira.entities.VmsVendorMstDokumenEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/dokumen")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Secured
public class MstDokumenRes {
    @GET
    @Path("/all")
    public Response getDokumen(){
        List<VmsVendorMstDokumenEntity> list = VmsVendorMstDokumenEntity.list("ORDER BY kode_dokumen ASC");
        return Response.ok().entity(ResponseHandler.ok("Inquiry Data Dokumen succes", list)).build();
    }

    @POST
    @Path("/create")
    @Transactional
    public Response createDokumen(@Valid @RequestBody CreateDokumenDto create){
        VmsVendorMstDokumenEntity createDok = new VmsVendorMstDokumenEntity();
        createDok.kode_dokumen = create.kode_dokumen;
        createDok.nama_dokumen = create.nama_dokumen;
        createDok.persist();
        return Response.ok().entity(ResponseHandler.ok("Create dokumen successfull", null)).build();
    }
}
