package org.sim.umira.resources.CostControl;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CostControl.CreateBiayaBkDto;
import org.sim.umira.dtos.CostControl.CreateProyekDto;
import org.sim.umira.entities.CostControl.BiayaKontruksiEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.RapaEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import io.quarkus.security.ForbiddenException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/CostControl/BiayaKonstruksi")
@Secured
public class BiayaKonstruksiRes {
    @POST
    @Path("/create-bk")
    @Transactional
     public Response createBk(
        @Valid @RequestBody CreateBiayaBkDto create
    ){
        try {
            RapaEntity rapa = RapaEntity.findById(create.id_rapa);
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            List <BiayaKontruksiEntity> listBk = BiayaKontruksiEntity.find("rapa = ?1", rapa).list();
            BigDecimal totalBk = create.harga_total;
            for (BiayaKontruksiEntity biayaKontruksiEntity : listBk) {
                totalBk = totalBk.add(biayaKontruksiEntity.harga_total);
            }
            if(totalBk.compareTo(new BigDecimal(proyek.biaya_rap)) > 0){
                throw new ForbiddenException("total BK melebihi biaya RAP");
            }
            BiayaKontruksiEntity bk = new BiayaKontruksiEntity();
            bk.rapa = rapa;
            bk.proyek = proyek;
            bk.nama_vendor = create.nama_vendor;
            bk.volume_bk = create.volume_bk;
            bk.nama_penerima = create.nama_penerima;
            bk.harga_total = create.harga_total;
            bk.tanggal_penerima = create.tanggal_penerima;
            bk.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Bk Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
    }
    @GET
    @Path("/get-bk-by-proyek")
    @Transactional
     public Response getBkByProyek(
        @QueryParam("id_proyek") String id_proyek
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(id_proyek);
            List<BiayaKontruksiEntity> bkList = BiayaKontruksiEntity.find("proyek = ?1", proyek).list(); 
            return Response.ok().entity(ResponseHandler.ok("get Bk Berhasil", bkList)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

    @GET
    @Path("/get-bk-by-rapa")
    @Transactional
     public Response getBkByRapa(
        @QueryParam("id_rapa") String id_rapa
    ){
        try {
            RapaEntity rapa = RapaEntity.findById(id_rapa);
            List<BiayaKontruksiEntity> bkList = BiayaKontruksiEntity.find("rapa = ?1", rapa).list(); 
            return Response.ok().entity(ResponseHandler.ok("get Bk Berhasil", bkList)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }
    @POST
    @Path("/update-bk")
    @Transactional
     public Response updateBk(
        @Valid @RequestBody CreateBiayaBkDto create
    ){
        try {
            RapaEntity rapa = RapaEntity.findById(create.id_rapa);
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            List <BiayaKontruksiEntity> listBk = BiayaKontruksiEntity.find("rapa = ?1", rapa).list();
            BigDecimal totalBk = create.harga_total;
            for (BiayaKontruksiEntity biayaKontruksiEntity : listBk) {
                totalBk = totalBk.add(biayaKontruksiEntity.harga_total);
            }
            if(totalBk.compareTo(new BigDecimal(proyek.biaya_rap)) > 0){
                throw new ForbiddenException("total BK melebihi biaya RAP");
            }
            BiayaKontruksiEntity bk = BiayaKontruksiEntity.findById(create.id_biaya_konstruksi);
            bk.rapa = rapa;
            bk.proyek = proyek;
            bk.nama_vendor = create.nama_vendor;
            bk.volume_bk = create.volume_bk;
            bk.nama_penerima = create.nama_penerima;
            bk.harga_total = create.harga_total;
            bk.tanggal_penerima = create.tanggal_penerima;
            return Response.ok().entity(ResponseHandler.ok("Create Bk Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
    }

    @DELETE
    @Path("/delete-bk")
    @Transactional
     public Response deleteBk(
        @QueryParam("id") String id
    ){ 
        try {
            boolean Delete = BiayaKontruksiEntity.deleteById(id);
            return Response.ok().entity(ResponseHandler.ok("Create Bk Berhasil", Delete)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
    }

}
