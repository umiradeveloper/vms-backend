package org.sim.umira.resources.CostControl;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CostControl.CreateKategoriDto;
import org.sim.umira.entities.CostControl.KategoriEntity;
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

@Path("/CostControl/Kategori")
@Secured
public class KategoriRes {
    

    @POST
    @Path("/create-kategori")
    @Transactional
    public Response createKategori(
        @Valid @RequestBody CreateKategoriDto create
    ){
        try {
            KategoriEntity kategori = new KategoriEntity();
            kategori.nama_kategori = create.nama_kategori;
            kategori.kode_kategori = create.kode_kategori;
            kategori.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Kategori Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @GET
    @Path("/get-kategori")
    public Response getKategori(){
        List<KategoriEntity> kategori = KategoriEntity.listAll();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Kategori Berhasil", kategori)).build();
    }

    @PATCH
    @Path("/update-kategori")
    @Transactional
    public Response updateKategori(
        @Valid @RequestBody CreateKategoriDto create
    ){
        try {
            KategoriEntity kategori = KategoriEntity.findById(create.id_kategori);
            kategori.nama_kategori = create.nama_kategori;
            kategori.kode_kategori = create.kode_kategori;
            return Response.ok().entity(ResponseHandler.ok("Update Kategori Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @DELETE
    @Path("/delete-kategori")
    @Transactional
    public Response deleteKategori(
        @QueryParam("id") String id
    ){
        try {
            Boolean kategori = KategoriEntity.deleteById(id);
       
            return Response.ok().entity(ResponseHandler.ok("Delete Kategori Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }
}
