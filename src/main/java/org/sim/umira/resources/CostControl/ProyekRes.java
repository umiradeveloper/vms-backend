package org.sim.umira.resources.CostControl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CostControl.CreateProyekDto;
import org.sim.umira.dtos.CostControl.MosDto;
import org.sim.umira.dtos.CostControl.ResponseProyekDto;
import org.sim.umira.entities.CostControl.AdendumProyekEntity;
import org.sim.umira.entities.CostControl.BiayaKontruksiEntity;
import org.sim.umira.entities.CostControl.MosEntity;
import org.sim.umira.entities.CostControl.MosNewEntity;
import org.sim.umira.entities.CostControl.PendapatanUsahaEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.ScurveEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import io.quarkus.panache.common.Sort;
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
            proyek.kerja_tambah = create.kerja_tambah;
            proyek.kerja_kurang = create.kerja_kurang;
            proyek.bk_pu_awal = create.biaya_rap.multiply(BigInteger.valueOf(100)).divide(create.biaya_rab).toString();
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
    @Path("/get-proyek-dashboard")
    public Response getProyekDashboard(){
        List<ProyekEntity> proyek = ProyekEntity.listAll();
        ArrayList<ResponseProyekDto> responseProyek = new ArrayList<>();
        for(ProyekEntity proE: proyek){
            List<PendapatanUsahaEntity> pu = PendapatanUsahaEntity.find("proyek = ?1", proE).list();
            Integer total_pu = 0;
            for (PendapatanUsahaEntity pendapatanUsahaEntity : pu) {
                total_pu += pendapatanUsahaEntity.nominal_pu;
            }
            BigDecimal total_bk = BigDecimal.ZERO;
            List<BiayaKontruksiEntity> bk = BiayaKontruksiEntity.find("proyek = ?1", proE).list();
            for (BiayaKontruksiEntity biayaKontruksiEntity : bk) {
                total_bk = total_bk.add(biayaKontruksiEntity.harga_total);
            }
            List<MosNewEntity> mos = MosNewEntity.find("proyek = ?1", Sort.by("week").descending(), proE).list();
            BigInteger currMos = BigInteger.ZERO;
            if(mos.size() > 0){
                currMos = mos.get(0).nominal_mos;
            }
            List<AdendumProyekEntity> adendumProyek = AdendumProyekEntity.find("proyek = ?1", proE).list();
            BigInteger kerja_tambah_total = BigInteger.ZERO;
            for (AdendumProyekEntity adendumPro : adendumProyek){
                if(adendumPro.kerja_tambah != null){
                    kerja_tambah_total = kerja_tambah_total.add(adendumPro.kerja_tambah);
                }
                
            }
            BigInteger kerja_kurang_total = BigInteger.ZERO;
            for (AdendumProyekEntity adendumPro : adendumProyek){
                if(adendumPro.kerja_kurang != null){
                    kerja_kurang_total = kerja_kurang_total.add(adendumPro.kerja_kurang);
                }
                
            }
            BigInteger total_scurve = BigInteger.ZERO;
            List<ScurveEntity> scurve = ScurveEntity.find("proyek = ?1", proE).list();
            for(ScurveEntity se: scurve){
                if(se.nominal_scurve != null){
                    total_scurve = total_scurve.add(se.nominal_scurve);
                }
            }
            responseProyek.add(new ResponseProyekDto(total_bk, total_pu, currMos, kerja_tambah_total, kerja_kurang_total, total_scurve, proE));
        }
        return Response.ok().entity(ResponseHandler.ok("Inquiry Proyek Berhasil", responseProyek)).build();
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
        try {
             ProyekEntity proyek = ProyekEntity.findById(id);

            List<PendapatanUsahaEntity> pu = PendapatanUsahaEntity.find("proyek = ?1", proyek).list();
            Integer total_pu = 0;
            for (PendapatanUsahaEntity pendapatanUsahaEntity : pu) {
                total_pu += pendapatanUsahaEntity.nominal_pu;
            }
            BigDecimal total_bk = BigDecimal.ZERO;
            List<BiayaKontruksiEntity> bk = BiayaKontruksiEntity.find("proyek = ?1", proyek).list();
            for (BiayaKontruksiEntity biayaKontruksiEntity : bk) {
                total_bk = total_bk.add(biayaKontruksiEntity.harga_total);
            }
            List<MosNewEntity> mos = MosNewEntity.find("proyek = ?1", Sort.by("week").descending(), proyek).list();
            BigInteger currMos = BigInteger.ZERO;
            if(mos.size() > 0){
                currMos = mos.get(0).nominal_mos;
            }
            List<AdendumProyekEntity> adendumProyek = AdendumProyekEntity.find("proyek = ?1", proyek).list();
            BigInteger kerja_tambah_total = BigInteger.ZERO;
            for (AdendumProyekEntity adendumPro : adendumProyek){
                if(adendumPro.kerja_tambah != null){
                    kerja_tambah_total = kerja_tambah_total.add(adendumPro.kerja_tambah);
                }
                
            }
            
            BigInteger kerja_kurang_total = BigInteger.ZERO;
            for (AdendumProyekEntity adendumPro : adendumProyek){
                if(adendumPro.kerja_kurang != null){
                    kerja_kurang_total = kerja_kurang_total.add(adendumPro.kerja_kurang);
                }
                
            }
            System.out.println(kerja_tambah_total);
            // ResponseProyekDto res = new ResponseProyekDto(total_bk, total_pu, currMos, kerja_tambah_total, kerja_kurang_total, proyek);
            ResponseProyekDto<ProyekEntity> dto = new ResponseProyekDto<>(total_bk, total_pu, currMos, kerja_tambah_total, kerja_kurang_total, proyek);
            // System.out.println(total_bk);
            
            return Response.ok().entity(ResponseHandler.ok("Inquiry Proyek Berhasil", dto)).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
       
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
            proyek.kerja_tambah = create.kerja_tambah;
            proyek.kerja_kurang = create.kerja_kurang;
            proyek.bk_pu_awal = create.biaya_rap.multiply(BigInteger.valueOf(100)).divide(create.biaya_rab).toString();
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
