package org.sim.umira.resources.CostControl;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CostControl.CreateProyekDto;
import org.sim.umira.dtos.CostControl.ResponseProyekDto;
import org.sim.umira.entities.CostControl.BiayaKontruksiEntity;
import org.sim.umira.entities.CostControl.PendapatanUsahaEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
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

@Path("/CostControl/Proyek")
@Secured
public class ProyekRes {
    
    @POST
    @Path("/create-proyek")
    @Transactional
    public Response createProyek(
        @Valid @RequestBody CreateProyekDto create
    ){
        try {
            System.out.println(create.tanggal_awal_kontrak);
            ProyekEntity proyek = new ProyekEntity();
            proyek.nama_proyek = create.nama_proyek;
            proyek.kode_proyek = create.kode_proyek;
            proyek.deskripsi_proyek = create.deskripsi_proyek;
            proyek.biaya_rap = create.biaya_rap;
            proyek.biaya_rab = create.biaya_rab;
            proyek.tanggal_awal_kontrak = create.tanggal_awal_kontrak;
            proyek.tanggal_akhir_kontrak = create.tanggal_akhir_kontrak;
            proyek.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Proyek Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @GET
    @Path("/get-proyek")
    public Response getProyek(){
        List<ProyekEntity> proyek = ProyekEntity.listAll();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Proyek Berhasil", proyek)).build();
    }
    @GET
    @Path("/get-proyek-id")
    public Response getProyekById(
        @QueryParam("id") String id
    ){
        ProyekEntity proyek = ProyekEntity.findById(id);
        return Response.ok().entity(ResponseHandler.ok("Inquiry Proyek Berhasil", proyek)).build();
    }
    @GET
    @Path("/get-proyek-id-bk-pu")
    public Response getProyekByIdBkPu(
        @QueryParam("id") String id
    ){
        ProyekEntity proyek = ProyekEntity.findById(id);

        List<PendapatanUsahaEntity> pu = PendapatanUsahaEntity.find("proyek = ?1", proyek).list();
        Integer total_pu = 0;
        for (PendapatanUsahaEntity pendapatanUsahaEntity : pu) {
            total_pu += pendapatanUsahaEntity.nominal_pu;
        }
        BigDecimal total_bk = BigDecimal.ZERO;
        List<BiayaKontruksiEntity> bk = BiayaKontruksiEntity.find("proyek = ?1", proyek).list();
        for (BiayaKontruksiEntity biayaKontruksiEntity : bk) {
                total_bk.add(biayaKontruksiEntity.harga_total);
        }

        
        return Response.ok().entity(ResponseHandler.ok("Inquiry Proyek Berhasil", new ResponseProyekDto(total_bk, total_pu, proyek))).build();
    }

    @PATCH
    @Path("/update-proyek")
    @Transactional
    public Response updateProyek(
        @Valid @RequestBody CreateProyekDto create, @Valid @QueryParam("id") String id
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(id);
            proyek.nama_proyek = create.nama_proyek;
            proyek.kode_proyek = create.kode_proyek;
            proyek.deskripsi_proyek = create.deskripsi_proyek;
            proyek.biaya_rap = create.biaya_rap;
            proyek.biaya_rab = create.biaya_rab;
            proyek.tanggal_awal_kontrak = create.tanggal_awal_kontrak;
            proyek.tanggal_akhir_kontrak = create.tanggal_akhir_kontrak;
            return Response.ok().entity(ResponseHandler.ok("Update Proyek Berhasil", null)).build();
        } catch (Exception e) {
            System.out.println(e);
            throw new InternalError(e.getMessage());
        }
        
    }

    @DELETE
    @Path("/delete-proyek")
    @Transactional
    public Response deleteProyek(
        @QueryParam("id") String id
    ){
        try {
            Boolean proyek = ProyekEntity.deleteById(id);
       
            return Response.ok().entity(ResponseHandler.ok("Delete Proyek Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }
}
