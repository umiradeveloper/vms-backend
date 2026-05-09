package org.sim.umira.resources.CostControl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.hibernate.Session;
import org.sim.umira.dtos.CostControl.CreateBiayaBkDto;
import org.sim.umira.dtos.CostControl.CreateBkBulkDto;
import org.sim.umira.dtos.CostControl.CreateProyekDto;
import org.sim.umira.dtos.CostControl.GetCostCodeRapaDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.CostControl.BiayaKontruksiEntity;
import org.sim.umira.entities.CostControl.CostCodeEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.RapaEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import io.quarkus.security.ForbiddenException;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/CostControl/BiayaKonstruksi")
@Secured
public class BiayaKonstruksiRes {

     @Inject
    EntityManager em;

    @POST
    @Path("/create-bk")
    @Transactional
     public Response createBk(
        @Valid @RequestBody CreateBiayaBkDto create, @Context SecurityContext ctx
    ){
        
            RapaEntity rapa = RapaEntity.findById(create.id_rapa);
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            List <BiayaKontruksiEntity> listBk = BiayaKontruksiEntity.find("rapa = ?1", rapa).list();
            BigDecimal totalBk = create.harga_total;
            BigDecimal volumeBk = create.volume_bk;
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();

            for (BiayaKontruksiEntity biayaKontruksiEntity : listBk) {
                totalBk = totalBk.add(biayaKontruksiEntity.harga_total);
                volumeBk = volumeBk.add(biayaKontruksiEntity.volume_bk);
            }
            if(totalBk.compareTo(new BigDecimal(proyek.biaya_rap)) > 0){
                throw new BadRequestException("total BK melebihi biaya RAP silahkan pengajuan approval ke Project Manager");
            }
            if(volumeBk.compareTo(rapa.volume) > 0){
                throw new BadRequestException("Volume melebihi perkiraan RAPA silahkan pengajuan approval ke Project Manager");
            }
            
     try {
            BiayaKontruksiEntity bk = new BiayaKontruksiEntity();
            bk.rapa = rapa;
            bk.proyek = proyek;
            bk.nama_vendor = create.nama_vendor;
            bk.volume_bk = create.volume_bk;
            bk.nama_penerima = create.nama_penerima;
            bk.harga_total = create.harga_total;
            bk.tanggal_penerima = create.tanggal_penerima;
            bk.created_by = ue.id_user;
            bk.created_at = LocalDateTime.now();
            bk.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Bk Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
    }

    @POST
    @Path("/create-bk-bulk")
    @Transactional
     public Response createBkBulk(
        @Valid @RequestBody CreateBkBulkDto create, @Context SecurityContext ctx
    ){

        ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
        BigDecimal totalBk = BigDecimal.ZERO;
        
        for (int i = 0; i < create.cost_code.size(); i++) {
            final int idx = i;
            CostCodeEntity ce = CostCodeEntity.find("cost_code = ?1", create.cost_code.get(idx)).firstResult();
            RapaEntity rapa = RapaEntity.find("costCodeRapa = ?1 AND proyek = ?2", ce, proyek).firstResult();
            if(rapa == null){
                throw new BadRequestException("Cost Code Tidak Terdaftar Di Rapa");
            }
            List <BiayaKontruksiEntity> listBk = BiayaKontruksiEntity.find("rapa = ?1 AND proyek = ?2", rapa, proyek).list();
            BigDecimal volumeBk = create.volume_bk.get(idx);
            for (BiayaKontruksiEntity biayaKontruksiEntity : listBk) {
                totalBk = totalBk.add(biayaKontruksiEntity.harga_total);
                volumeBk = volumeBk.add(biayaKontruksiEntity.volume_bk);
            }
            totalBk = totalBk.add(create.harga_total.get(idx));
            if(volumeBk.compareTo(rapa.volume) > 0){
                throw new BadRequestException("Volume Melebihi dari RAPA yang telah di tentukan");
            }
        }
        if(totalBk.compareTo(new BigDecimal(proyek.biaya_rap)) > 0){
            throw new BadRequestException("total BK melebihi biaya RAP silahkan pengajuan approval");
        }

        
        try {
            
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            Session session = em.unwrap(Session.class);
            int batch = create.cost_code.size();
            // System.out.println(create);
            for (int i = 0; i < create.cost_code.size(); i++) {
                String uuid = java.util.UUID.randomUUID().toString();
                
                final int idx = i;
                CostCodeEntity costCodeEntity = CostCodeEntity.find("cost_code = ?1", create.cost_code.get(idx)).firstResult();
                RapaEntity rapaEntity = RapaEntity.find("costCodeRapa = ?1 AND proyek = ?2", costCodeEntity, proyek).firstResult();
                // System.out.println(create.kategori.get(idx));
                
                    session.doWork(connection -> {
                        try (PreparedStatement ps = connection.prepareStatement(
                            // "INSERT INTO cc_rapa (id_rapa, id_proyek, kategori, kode_rap, `group`, item_pekerjaan, spesifikasi, satuan, volume, harga_satuan, harga_total, created_at, created_by, id_cost_code) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                            "INSERT INTO cc_biaya_kontruksi (id_biaya_kontruksi, id_proyek, id_rapa, nama_vendor, volume_bk, harga_total, nama_penerima, tanggal_penerima, reference_id_pengajuan, created_at, created_by, no_po, invoice_nota) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        )) {
                            ps.setString(1, uuid);
                            ps.setString(2, proyek.id_proyek);
                            ps.setString(3, rapaEntity.id_rapa);
                            ps.setString(4, null);
                            ps.setBigDecimal(5, create.volume_bk.get(idx));
                            ps.setBigDecimal(6, create.harga_total.get(idx));
                            ps.setString(7, null);
                            ps.setObject(8, create.tanggal.get(idx));
                            ps.setString(9, null);
                            ps.setObject(10, LocalDateTime.now());
                            ps.setString(11, ue.id_user);
                            ps.setString(12, create.no_po.get(idx));
                            ps.setString(13, create.invoice_nota.get(idx));
                            ps.addBatch();
                            ps.executeBatch();
                        }

                    });
                    if (i % batch == 0) {
                        session.flush();
                        session.clear();
                    }
                
                
            }

             return Response.ok().entity(ResponseHandler.ok("Upload Biaya Konstruksi Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
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
        // try {
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
        // } catch (Exception e) {
        //     throw new InternalError(e.getMessage());
        //     // TODO: handle exception
        // }
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

    @POST
    @Path("/get-cost-code-rapa")
    public Response getCostCodeRapa(@Valid @RequestBody GetCostCodeRapaDto get, @QueryParam("id_proyek") String id_proyek){
        for (int i = 0; i < get.CostCode.size(); i++) {
                final int index = i;
                CostCodeEntity costCodeEntity = CostCodeEntity.find("cost_code = ?1", get.CostCode.get(index).trim()).firstResult();
                // SatuanEntity satuan = SatuanEntity.find("kode_satuan = ?1", create.satuan.get(index)).firstResult();
                // KategoriEntity kategori = KategoriEntity.find("kode_kategori = ?1", create.kategori.get(index)).firstResult();
                
                if(costCodeEntity == null){
                    throw new BadRequestException("Cost Code index ke "+index+ " cost code "+get.CostCode.get(index).trim()+" tidak terdaftar");
                }
        }
        for (int i = 0; i < get.CostCode.size(); i++) {
                final int index = i;
                CostCodeEntity costCodeEntity = CostCodeEntity.find("cost_code = ?1", get.CostCode.get(index).trim()).firstResult();
                ProyekEntity proyek = ProyekEntity.findById(id_proyek);
                RapaEntity rapaCheck = RapaEntity.find("costCodeRapa = ?1 AND proyek = ?2", costCodeEntity, proyek).firstResult();
                if(rapaCheck == null){
                    throw new BadRequestException("Cost Code index ke "+index+ " cost code "+get.CostCode.get(index).trim()+" tidak terdaftar di RAPA silahkan update rapa");
                }
                // SatuanEntity satuan = SatuanEntity.find("kode_satuan = ?1", create.satuan.get(index)).firstResult();
                // KategoriEntity kategori = KategoriEntity.find("kode_kategori = ?1", create.kategori.get(index)).firstResult();
                
                // if(costCodeEntity == null){
                //     throw new BadRequestException("Cost Code index ke "+index+ " cost code "+get.CostCode.get(index).trim()+" tidak terdaftar");
                // }
        }
        try {
            
            List<CostCodeEntity> costCode = CostCodeEntity.find("cost_code in ?1", get.CostCode).list();
            // rapa.persist();
            return Response.ok().entity(ResponseHandler.ok("get cost code Berhasil", costCode)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }


    

}
