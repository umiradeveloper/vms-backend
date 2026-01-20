package org.sim.umira.resources;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CreateKualifikasiVendorDto;
import org.sim.umira.entities.VmsVendorMstKualifikasi;
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

@Path("/mst-kualifikasi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Secured
public class MstKualifikasiRes {
    @GET
    @Path("/all")
    public Response getMstKualifikasi(){
        List<VmsVendorMstKualifikasi> kualifikasi = VmsVendorMstKualifikasi.list("ORDER BY no_urut ASC");
        return Response.ok().entity(ResponseHandler.ok("Inquiry data mst kualifikasi", kualifikasi)).build();
    }

    @POST
    @Path("/create")
    @Transactional
    public Response createMstKualifikasi(
        @Valid @RequestBody CreateKualifikasiVendorDto create
    ){
        VmsVendorMstKualifikasi kualifikasi = new VmsVendorMstKualifikasi();
        kualifikasi.kualifikasi = create.nama_kualifikasi;
        kualifikasi.persist();
        return Response.ok().entity(ResponseHandler.ok("Kualifikasi Master Vendor Berhasil di tambahkan", null)).build();
    }
}
