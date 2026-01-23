package org.sim.umira.resources.CostControl;

import java.sql.PreparedStatement;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.hibernate.Session;
import org.sim.umira.dtos.CostControl.CreateProyekDto;
import org.sim.umira.dtos.CostControl.CreateRapaBulkDto;
import org.sim.umira.dtos.CostControl.CreateRapaDto;
import org.sim.umira.entities.CostControl.KategoriEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.RapaEntity;
import org.sim.umira.entities.CostControl.SatuanEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;


import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/CostControl/Rapa")
@Secured
public class RapaRes {

    @Inject
    EntityManager em;



    @POST
    @Path("/create-rapa")
    @Transactional
    public Response createRapa(
        @Valid @RequestBody CreateRapaDto create
    ){
        try {
            ProyekEntity proyek = ProyekEntity.find("kode_proyek = ?1", create.kode_proyek).firstResult();
            RapaEntity rapa = new RapaEntity();
            rapa.proyek = proyek;
            rapa.kode_rap = create.kode_rap;
            rapa.Kategori = create.kategori;
            rapa.spesifikasi = create.spesifikasi;
            rapa.item_pekerjaan = create.item_pekerjaan;
            rapa.satuan = create.satuan;
            rapa.volume = create.volume;
            rapa.harga_satuan = create.harga_satuan;
            rapa.harga_total = create.harga_total;
            rapa.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Rapa Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @POST
    @Path("/create-rapa-bulk")
    @Transactional
    public Response createRapaBulk(
        @Valid @RequestBody CreateRapaBulkDto create
    ){
        for (int i = 0; i < create.kategori.size(); i++) {
                final int index = i;
                SatuanEntity satuan = SatuanEntity.find("kode_satuan = ?1", create.satuan.get(index)).firstResult();
                KategoriEntity kategori = KategoriEntity.find("kode_kategori = ?1", create.kategori.get(index)).firstResult();
                
                if(satuan == null && kategori == null){
                    throw new BadRequestException("Satuan dan kategori di baris "+index+ " kode kategori "+create.kategori.get(index)+" atau kode satuan "+create.satuan.get(index)+" tidak terdefinisi");
                }
        }

        try {
            ProyekEntity proyek = ProyekEntity.find("kode_proyek = ?1", create.kode_proyek).firstResult();
            Session session = em.unwrap(Session.class);
            int batch = create.kategori.size();
            // System.out.println(create);
            for (int i = 0; i < create.kategori.size(); i++) {
                String uuid = java.util.UUID.randomUUID().toString();
                final int idx = i;
                // System.out.println(create.kategori.get(idx));
                
                    session.doWork(connection -> {
                        try (PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO cc_rapa (id_rapa, id_proyek, kategori, kode_rap, `group`, item_pekerjaan, spesifikasi, satuan, volume, harga_satuan, harga_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        )) {
                            ps.setString(1, uuid);
                            ps.setString(2, proyek.id_proyek);
                            ps.setString(3, create.kategori.get(idx));
                            ps.setString(4, create.kode_rap.get(idx));
                            ps.setString(5, create.group.get(idx));
                            ps.setString(6, create.item_pekerjaan.get(idx));
                            ps.setString(7, create.spesifikasi.get(idx));
                            ps.setString(8, create.satuan.get(idx));
                            ps.setBigDecimal(9, create.volume.get(idx));
                            ps.setInt(10, create.harga_satuan.get(idx));
                            ps.setBigDecimal(11, create.harga_total.get(idx));
                            // ps.setBigDecimal(2, p.nilai);
                            // ps.setObject(3, p.tanggal);
                            ps.addBatch();
                            ps.executeBatch();
                        }

                    });
                    if (i % batch == 0) {
                        session.flush();
                        session.clear();
                    }
                
                
            }
            
            return Response.ok().entity(ResponseHandler.ok("Create Rapa Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }
    @GET
    @Path("/get-rapa-proyek")
    @Transactional
    public Response getRapaByProyek(
        @QueryParam("id_proyek") String id_proyek
    ){
        try {
           
            ProyekEntity proyek = ProyekEntity.findById(id_proyek);
            List<RapaEntity> rapa = RapaEntity.find("proyek = ?1", proyek).list();
   
            return Response.ok().entity(ResponseHandler.ok("get Rapa by proyek Berhasil", rapa)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @GET
    @Path("/get-rapa-id")
    @Transactional
    public Response getRapaById(
        @QueryParam("id_rapa") String id_rapa
    ){
        try {
           
            RapaEntity rapa = RapaEntity.findById(id_rapa);
   
            return Response.ok().entity(ResponseHandler.ok("get Rapa by Id Berhasil", rapa)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @GET
    @Path("/get-rapa-all")
    @Transactional
    public Response getRapaByAll(
        @QueryParam("id_rapa") String id_rapa
    ){
        try {
           
            List<RapaEntity> rapa = RapaEntity.listAll();
   
            return Response.ok().entity(ResponseHandler.ok("get Rapa All Berhasil", rapa)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }
    @PATCH
    @Path("/update-rapa")
    @Transactional
    public Response updateRapa(
        @Valid @RequestBody CreateRapaDto create
    ){
        try {
            // ProyekEntity proyek = ProyekEntity.find("kode_proyek = ?1", create.kode_proyek).firstResult();
            RapaEntity rapa = RapaEntity.findById(create.id_rapa);
            rapa.kode_rap = create.kode_rap;
            rapa.Kategori = create.kategori;
            rapa.spesifikasi = create.spesifikasi;
            rapa.item_pekerjaan = create.item_pekerjaan;
            rapa.satuan = create.satuan;
            rapa.volume = create.volume;
            rapa.harga_satuan = create.harga_satuan;
            rapa.harga_total = create.harga_total;
            // rapa.persist();
            return Response.ok().entity(ResponseHandler.ok("Update Rapa Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @DELETE
    @Path("/delete-rapa")
    @Transactional
    public Response deleteRapa(
        @QueryParam("id") String id
    ){
        try {
            boolean rapa = RapaEntity.deleteById(id);
            // rapa.persist();
            return Response.ok().entity(ResponseHandler.ok("Delete Rapa Berhasil", rapa)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }
}
