package org.sim.umira.resources.CostControl;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.CostControl.CreatePuDto;
import org.sim.umira.dtos.CostControl.ResponseRapaPendapatanUsahaDto;
import org.sim.umira.dtos.CostControl.CreateProyekDto;
import org.sim.umira.entities.CostControl.BiayaKontruksiEntity;
import org.sim.umira.entities.CostControl.PendapatanUsahaEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.RapaEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/CostControl/PendapatanUsaha")
@Secured
public class PendapatanUsahaRes {

    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/dokumen-pendapatan-usaha");


    @POST
    @Path("/create-pu")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
     public Response createPu(
        @Valid @MultipartForm CreatePuDto create
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);

            PendapatanUsahaEntity pu = new PendapatanUsahaEntity();
            String fileName = java.util.UUID.randomUUID() + "-" + create.dokumen_upload.fileName();
            java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
            Files.copy(
                create.dokumen_upload.uploadedFile(),
                target,
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            pu.proyek = proyek;
            pu.week_pu = create.week_pu;
            pu.tanggal_awal = create.tanggal_awal;
            pu.tanggal_akhir = create.tanggal_akhir;
            pu.nominal_pu = create.nominal_pu;
            pu.dokumen_pu = target.toString();
            pu.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Pu Berhasil", pu)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
    }
    @GET
    @Path("/get-pu-by-proyek")
    @Transactional
     public Response getPuByProyek(
        @QueryParam("id_proyek") String id_proyek
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(id_proyek);
            List<PendapatanUsahaEntity> puList = PendapatanUsahaEntity.find("proyek = ?1", proyek).list(); 
            return Response.ok().entity(ResponseHandler.ok("get Pu Berhasil", puList)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }
    @GET
    @Path("/get-pu-by-id")
    @Transactional
     public Response getPuById(
        @QueryParam("id") String id
    ){
        try {
            PendapatanUsahaEntity pu = PendapatanUsahaEntity.findById(id);
            return Response.ok().entity(ResponseHandler.ok("get Pu by id Berhasil", pu)).build();
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
            List<ResponseRapaPendapatanUsahaDto> rapaNew = new ArrayList<>();
            for (RapaEntity rapaEntity : rapa) {
                List<BiayaKontruksiEntity> bk = BiayaKontruksiEntity.find("rapa =?1 ", rapaEntity).list();
                BigDecimal total_bk_rapa = BigDecimal.ZERO;
                for (BiayaKontruksiEntity bkArr : bk) {
                    total_bk_rapa = total_bk_rapa.add(bkArr.harga_total);
                }

                rapaNew.add(new ResponseRapaPendapatanUsahaDto(rapaEntity.id_rapa, rapaEntity.Kategori, rapaEntity.kode_rap, rapaEntity.group,
            rapaEntity.item_pekerjaan, rapaEntity.spesifikasi, rapaEntity.satuan, rapaEntity.volume, rapaEntity.harga_satuan,
            rapaEntity.harga_total, total_bk_rapa));
            }
            
            return Response.ok().entity(ResponseHandler.ok("get Rapa by proyek Berhasil", rapaNew)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @POST
    @Path("/update-pu")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
     public Response updatePu(
        @Valid @MultipartForm CreatePuDto create
    ){
        try {
            // ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            PendapatanUsahaEntity pu = PendapatanUsahaEntity.findById(create.id_pu);
            pu.week_pu = create.week_pu;
            pu.tanggal_awal = create.tanggal_awal;
            pu.tanggal_akhir = create.tanggal_akhir;
            // pu.proyek = proyek;
            pu.nominal_pu = create.nominal_pu;
            if(create.dokumen_upload != null){
                Files.deleteIfExists(java.nio.file.Path.of(pu.dokumen_pu));
                String fileName = java.util.UUID.randomUUID() + "-" + create.dokumen_upload.fileName();
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(create.dokumen_upload.uploadedFile(),target,java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                pu.dokumen_pu = target.toString();
            }
            
            return Response.ok().entity(ResponseHandler.ok("Update Pu Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
    }

    @DELETE
    @Path("/delete-pu")
    @Transactional
     public Response deletePu(
        @QueryParam("id") String id
    ){ 
        try {
            boolean Delete = PendapatanUsahaEntity.deleteById(id);
            return Response.ok().entity(ResponseHandler.ok("Hapus Pu Berhasil", Delete)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
    }
}
