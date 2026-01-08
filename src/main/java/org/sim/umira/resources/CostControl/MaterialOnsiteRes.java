package org.sim.umira.resources.CostControl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.hibernate.Session;
import org.sim.umira.dtos.CostControl.CreateKategoriDto;
import org.sim.umira.dtos.CostControl.CreateMosBulkDto;
import org.sim.umira.dtos.CostControl.CreateMosDto;
import org.sim.umira.dtos.CostControl.ResponseMaterialDto;
import org.sim.umira.entities.CostControl.KategoriEntity;
import org.sim.umira.entities.CostControl.MosEntity;
import org.sim.umira.entities.CostControl.PendapatanUsahaEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.RapaEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/CostControl/Mos")
@Secured
public class MaterialOnsiteRes {
    @Inject
    EntityManager em;


    @POST
    @Path("/create-mos")
    @Transactional
    public Response createMos(
        @Valid @RequestBody CreateMosDto create
    ){
        try {
            MosEntity mos = new MosEntity();
            RapaEntity rapa = RapaEntity.findById(create.id_rapa);
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            mos.rapa = rapa;
            mos.proyek = proyek;
            mos.volume = create.volume;
            mos.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Mos Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }
    @POST
    @Path("/create-mos-bulk")
    @Transactional
    public Response createMosBulk(
        @Valid @RequestBody CreateMosBulkDto create
    ){
        try {
            // MosEntity mos = new MosEntity();
            Session session = em.unwrap(Session.class);
            int batch = create.id_rapa.size();
            for (int i = 0; i < create.id_rapa.size(); i++) {
                String uuid = java.util.UUID.randomUUID().toString();
                final int idx = i;
                
                 session.doWork(connection -> {
                        try (PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO cc_materialpu (id_mos, id_rapa, volume, id_proyek, id_pu) VALUES (?, ?, ?, ?, ?)"
                        )) {
                            ps.setString(1, uuid);
                            ps.setString(2, create.id_rapa.get(idx));
                            ps.setBigDecimal(3, create.volume.get(idx));
                            ps.setString(4, create.id_proyek);
                            ps.setString(5, create.id_pu);
                            ps.addBatch();
                            ps.executeBatch();
                        }

                    });
                    if (i % batch == 0) {
                        session.flush();
                        session.clear();
                    }

            }
            // RapaEntity rapa = RapaEntity.findById(create.id_rapa);
            // ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            // mos.rapa = rapa;
            // mos.proyek = proyek;
            // mos.volume = create.volume;
            // mos.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Mos Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }
    @POST
    @Path("/update-mos")
    @Transactional
    public Response updateMos(
        @Valid @RequestBody CreateMosDto create
    ){
        try {
            MosEntity mos = MosEntity.findById(create.id_mos);
            RapaEntity rapa = RapaEntity.findById(create.id_rapa);
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            mos.proyek = proyek;
            mos.rapa = rapa;
            mos.volume = create.volume;
            // mos.persist();
            return Response.ok().entity(ResponseHandler.ok("update Mos Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }
    @GET
    @Path("/get-mos-by-rapa")
    @Transactional
    public Response getMosByRapa(
        @QueryParam("id") String id_rapa
    ){
        try {
            RapaEntity rapa = RapaEntity.findById(id_rapa);
            List<MosEntity> mos = MosEntity.find("rapa = ?1", rapa).list();
           
            return Response.ok().entity(ResponseHandler.ok("get Mos by id_rapa Berhasil", mos)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @GET
    @Path("/get-mos-by-proyek")
    @Transactional
    public Response getMosByProyek(
        @QueryParam("id") String id_proyek
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(id_proyek);
            List<MosEntity> mos = MosEntity.find("proyek = ?1", proyek).list();
           
            return Response.ok().entity(ResponseHandler.ok("get Mos by id_proyek Berhasil", mos)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @GET
    @Path("/get-mos-by-pu")
    @Transactional
    public Response getMosByPu(
        @QueryParam("id") String id_pu
    ){
        try {
            // PendapatanUsahaEntity pu = PendapatanUsahaEntity.findById(id_pu);
            // List<MosEntity> mos = MosEntity.find("pu = ?1", pu).list();
            List<ResponseMaterialDto> mos = em.createNativeQuery("SELECT r.id_rapa, r.id_proyek, r.kategori, r.kode_rap, r.group, r.item_pekerjaan, r.spesifikasi, r.satuan, r.volume as volume_rapa, r.harga_satuan, r.harga_total, mpu.id_pu, mpu.volume as volume_material FROM cc_rapa as r INNER JOIN cc_materialpu as mpu ON mpu.id_rapa = r.id_rapa WHERE mpu.id_pu = :id0", ResponseMaterialDto.class).setParameter("id0", id_pu).getResultList();
           
            return Response.ok().entity(ResponseHandler.ok("get Mos by pu Berhasil", mos)).build();
        } catch (Exception e) {
            // System.out.println(e.printStackTrace());
            e.printStackTrace();
            throw new InternalError(e.getMessage());
        }
        
    }
    @GET
    @Path("/delete-mos")
    @Transactional
    public Response deleteMosById(
        @QueryParam("id") String id_rapa
    ){
        try {
          
            Boolean deleteData = MosEntity.deleteById(id_rapa);
           
            return Response.ok().entity(ResponseHandler.ok("get Mos by id_rapa Berhasil", deleteData)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }
}
