package org.sim.umira.resources.CostControl;



import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.hibernate.Session;
import org.sim.umira.dtos.CostControl.CreateCostCodeDto;
import org.sim.umira.dtos.CostControl.CreateSingleCostCodeDto;
import org.sim.umira.dtos.CostControl.ProjectCostCodeDto;
import org.sim.umira.dtos.CostControl.RapaDto;
import org.sim.umira.dtos.CostControl.ResponseCostCodeDto;
import org.sim.umira.entities.CostControl.CostCodeEntity;
import org.sim.umira.entities.CostControl.KategoriEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.SatuanEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;


@Path("/CostControl/Cost-Code")
@Secured
public class CostCodeRes {
    @Inject
    EntityManager em;
    

    @POST
    @Path("/create-cost-code")
    @Transactional
    public Response createCostCode(
        @Valid @RequestBody CreateCostCodeDto create
    ){
        
        for (int i = 0; i < create.kode_kategori.size(); i++) { 
            final int idy = i;
            KategoriEntity kategori_check = KategoriEntity.find("kode_kategori = ?1", create.kode_kategori.get(idy).trim()).firstResult();
            if(kategori_check == null){
                throw new BadRequestException("Kode kategori dengan "+create.kode_kategori.get(idy).trim()+" tidak terdaftar");
            }
        }
         for (int i = 0; i < create.kode.size(); i++) { 
            final int idy = i;
            CostCodeEntity costCodeCheck = CostCodeEntity.find("cost_code = ?1", create.kode.get(idy).trim()).firstResult();
            if(costCodeCheck != null){
                throw new BadRequestException("Cost Code dengan kode "+create.kode.get(idy).trim()+" sudah terdaftar");
            }
            CostCodeEntity costCodeNameCheck = CostCodeEntity.find("LOWER(REPLACE(TRIM(nama), ' ', '')) = ?1", create.nama.get(idy).trim().replace(" ", "").toLowerCase()).firstResult();
            if(costCodeNameCheck != null){
                throw new BadRequestException("Cost Code dengan nama "+create.nama.get(idy).trim()+" sudah terdaftar");
            }
            
        }
        // for (int i = 0; i < create.satuan.size(); i++) { 
        //     final int idz = i;
        //     SatuanEntity satuan_check = SatuanEntity.find("nama_satuan = ?1", create.satuan.get(idz).trim()).firstResult();
        //     if(satuan_check == null){
        //         throw new BadRequestException("Nama satuan dengan "+create.satuan.get(idz).trim()+" tidak terdaftar");
        //     }
        // }

        try {
            Session session = em.unwrap(Session.class);
            int batch = create.kode.size();
            System.out.println(create.kode_kategori.size());
            for (int i = 0; i < create.kode.size(); i++) {
                final int idx = i;
                // System.out.println(create.kode.get(idx));
                String uuid = java.util.UUID.randomUUID().toString();
                // System.out.println(create.kode_kategori.get(idx));

                KategoriEntity kategori = KategoriEntity.find("kode_kategori = ?1", create.kode_kategori.get(idx).trim()).firstResult();
                
                // System.out.println(kategori);
                
                    session.doWork(connection -> {
                        try (PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO cc_cost_code (id_cost_code, cost_code, nama, klasifikasi, satuan, spesifikasi, kode_jenis, jenis, kode_kategori) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
                        )) {
                            ps.setString(1, uuid);
                            ps.setString(2, create.kode.get(idx));
                            ps.setString(3, create.nama.get(idx));
                            ps.setString(4, create.klasifikasi.get(idx));
                            ps.setString(5, create.satuan.get(idx));
                            ps.setString(6, create.spesifikasi.get(idx));
                            ps.setString(7, create.kode_jenis.get(idx));
                            ps.setString(8, create.jenis.get(idx));
                            ps.setString(9, kategori.id_kategori);
                           
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
            return Response.ok().entity(ResponseHandler.ok("Create Cost Code Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
        
    }
    @GET
    @Path("/get-cost-code")
    @Transactional
    public Response getCostCode(){
        try{
            // List<CostCodeEntity> costCode = CostCodeEntity.findAll().list();
            List<CostCodeEntity> costCode = CostCodeEntity.find(
                "SELECT c FROM CostCodeEntity c JOIN FETCH c.kategori"
            ).list();
            List<ResponseCostCodeDto> response = new ArrayList<>();
            for (CostCodeEntity costCodeE : costCode) {
                List<ProjectCostCodeDto> proyek = ProyekEntity.find("SELECT p.nama_proyek as nama_proyek, SUM(b.volume_bk) as volume, SUM(b.harga_total) as harga_total FROM ProyekEntity p JOIN p.bk b JOIN b.rapa r JOIN r.costCodeRapa c WHERE c.cost_code = ?1 GROUP BY p.nama_proyek",costCodeE.cost_code).project(ProjectCostCodeDto.class).list();
                response.add(new ResponseCostCodeDto(costCodeE.id_cost_code, costCodeE.cost_code, costCodeE.nama, costCodeE.klasifikasi, costCodeE.spesifikasi, costCodeE.satuan, costCodeE.kode_jenis, costCodeE.kategori.nama_kategori, costCodeE.kategori.kode_kategori,costCodeE.jenis, proyek));
            }
            return Response.ok().entity(ResponseHandler.ok("get Cost Code Berhasil", response)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }
    @GET
    @Path("/get-cost-code-by-proyek")
    @Transactional
    public Response getCostCodeByProyek(@QueryParam("cost_code") String costCode){
        try{
  
            List<ProjectCostCodeDto> proyek = ProyekEntity.find("SELECT p.nama_proyek as nama_proyek, SUM(b.volume_bk) as volume, SUM(b.harga_total) as harga_total FROM ProyekEntity p JOIN p.bk b JOIN b.rapa r JOIN r.costCodeRapa c WHERE c.cost_code = ?1 GROUP BY p.nama_proyek",costCode).project(ProjectCostCodeDto.class).list();
            return Response.ok().entity(ResponseHandler.ok("get Cost Code Proyek Berhasil", proyek)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

    @POST
    @Path("/create-single-cost-code")
    @Transactional
    public Response createSingleCostCode(@Valid CreateSingleCostCodeDto create){

        KategoriEntity kategori_check = KategoriEntity.find("kode_kategori = ?1", create.kode_kategori.trim()).firstResult();
        if(kategori_check == null){
            throw new BadRequestException("kode kategori dengan "+create.kode_kategori+" tidak terdaftar");
        } 
        System.out.println(create.kode);
         try{
            CostCodeEntity createCostCode = new CostCodeEntity();
            createCostCode.cost_code = create.kode;
            createCostCode.nama = create.nama;
            createCostCode.jenis = create.jenis;
            createCostCode.klasifikasi = create.klasifikasi;
            createCostCode.kode_jenis = create.kode_jenis;
            createCostCode.satuan = create.satuan;
            createCostCode.spesifikasi = create.spesifikasi;
            createCostCode.kategori = kategori_check;
            createCostCode.persist();
            return Response.ok().entity(ResponseHandler.ok("create Cost Code Berhasil", createCostCode)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

    @POST
    @Path("/update-single-cost-code")
    @Transactional
    public Response updateSingleCostCode(@Valid CreateSingleCostCodeDto create){

        KategoriEntity kategori_check = KategoriEntity.find("kode_kategori = ?1", create.kode_kategori.trim()).firstResult();
        if(kategori_check == null){
            throw new BadRequestException("kode kategori dengan "+create.kode_kategori+" tidak terdaftar");
        } 
        // System.out.println(create.kode);
         try{
            CostCodeEntity createCostCode = CostCodeEntity.findById(create.id_cost_code);
            createCostCode.cost_code = create.kode;
            createCostCode.nama = create.nama;
            createCostCode.jenis = create.jenis;
            createCostCode.klasifikasi = create.klasifikasi;
            createCostCode.kode_jenis = create.kode_jenis;
            createCostCode.satuan = create.satuan;
            createCostCode.spesifikasi = create.spesifikasi;
            createCostCode.kategori = kategori_check;
            return Response.ok().entity(ResponseHandler.ok("update Cost Code Berhasil", createCostCode)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }
    
}
