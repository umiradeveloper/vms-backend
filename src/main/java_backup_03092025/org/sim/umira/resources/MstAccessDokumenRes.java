package org.sim.umira.resources;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CreateAccessDokDto;
import org.sim.umira.entities.VmsVendorAccessDokumen;
import org.sim.umira.entities.VmsVendorMstDokumenEntity;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/access-document")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Secured
public class MstAccessDokumenRes {
    @GET
    @Path("/all")
    public Response getAccess(){
        List<VmsVendorAccessDokumen> list = VmsVendorAccessDokumen.listAll();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Access Dokumen Success", list)).build();
    }
    @GET
    @Path("/all-id-kualifikasi")
    public Response getAccessByIdKualifikasi(
        @QueryParam("id") String id
    ){
        VmsVendorMstKualifikasi kualifikasi = VmsVendorMstKualifikasi.findById(id);
        List<VmsVendorAccessDokumen> list = VmsVendorAccessDokumen.find("kualifikasi = ?1 Order By dokumen.kode_dokumen ASC", kualifikasi).list();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Access Dokumen By Id Kualifikasi Success", list)).build();
    }

    @POST
    @Path("/create")
    @Transactional
    public Response createAccess(
        @Valid @RequestBody CreateAccessDokDto create
    ){
        VmsVendorMstDokumenEntity dokumen = VmsVendorMstDokumenEntity.find("kode_dokumen", create.kode_dokumen).firstResult();
        VmsVendorMstKualifikasi kualifikasi = VmsVendorMstKualifikasi.findById(create.id_mst_kualifikasi);

        VmsVendorAccessDokumen vmsCreate = new VmsVendorAccessDokumen();
        vmsCreate.dokumen = dokumen;
        vmsCreate.kualifikasi = kualifikasi;
        vmsCreate.persist();
        return Response.ok().entity(ResponseHandler.ok("Create Access Dokumen Success", null)).build();
    }
}
