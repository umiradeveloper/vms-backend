package org.sim.umira.resources.CostControl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.sim.umira.dtos.CostControl.CreatePengajuanBkDto;
import org.sim.umira.dtos.CostControl.ResponseApprovePengajuanBkDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.CostControl.PengajuanBiayaKonstruksiEntity;
import org.sim.umira.entities.CostControl.PengajuanBiayaKonstruksiPersetujuanEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.RapaEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/CostControl/pengajuan")
@Secured
public class PengajuanBiayaKonstruksiRes {
    @Inject
    EntityManager em;


    @POST
    @Path("/create-pengajuan-bk")
    @Transactional
    public Response createPengajuanBk(
        @Valid CreatePengajuanBkDto create, @Context SecurityContext ctx
    ){
        ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
        RapaEntity rapa = RapaEntity.findById(create.id_rapa);
        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        if(proyek == null){
            throw new BadRequestException("Id Proyek tidak di temukan");
        }
        if(rapa == null){
            throw new BadRequestException("Id Rapa tidak di temukan");
        }

        if(ue == null){
            throw new BadRequestException("User tidak di temukan");
        }
        for (int i = 0; i < create.id_user.size(); i++) {

            if(create.id_user.get(i) == null){
                throw new BadRequestException("id_user index "+ i +" null");
            }
            UserEntity userId = UserEntity.findById(create.id_user.get(i));
            if(userId == null){
                throw new BadRequestException("user index "+ i +" tidak di temukan");
            }
        }
        try {
            

            PengajuanBiayaKonstruksiEntity pengajuanBk = new PengajuanBiayaKonstruksiEntity();
            pengajuanBk.nama_penerima = create.nama_penerima;
            pengajuanBk.nama_vendor = create.nama_vendor;
            pengajuanBk.volume_bk = create.volume_bk;
            pengajuanBk.proyek = proyek;
            pengajuanBk.rapa = rapa;
            pengajuanBk.harga_total = create.harga_total;
            pengajuanBk.persist();
              
            // System.out.println(pengajuanBk);
            if(pengajuanBk != null){
               PengajuanBiayaKonstruksiPersetujuanEntity persetujuan = new PengajuanBiayaKonstruksiPersetujuanEntity();
                persetujuan.pengajuan_bk = pengajuanBk;
                persetujuan.nama_persetujuan = ue.username;
                persetujuan.id_user = ue.id_user;
                persetujuan.urutan = 0;
                persetujuan.tanggal_persetujuan = LocalDateTime.now();
                persetujuan.persist();

                for (int i = 0; i < create.id_user.size(); i++) {
                    // System.out.println(create.id_user.get(i));
                    // final int idx = i;
                    UserEntity userId = UserEntity.findById(create.id_user.get(i));
                    PengajuanBiayaKonstruksiPersetujuanEntity persetujuanBk = new PengajuanBiayaKonstruksiPersetujuanEntity();
                    persetujuanBk.id_user = create.id_user.get(i);
                    persetujuanBk.pengajuan_bk = pengajuanBk;
                    persetujuanBk.nama_persetujuan = userId.nama;
                    persetujuanBk.jabatan_persetujuan = userId.role.nama_role;
                    persetujuanBk.persist();
                } 
            }
            

            return Response.ok().entity(ResponseHandler.ok("Create Pengajuan Biaya Konstruksi Berhasil Di Buat", pengajuanBk)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
        
    }

    @GET
    @Path("/get-approve-pengajuan_bk")
    public Response getApprovePengajuanBk(
        @Context SecurityContext ctx
    ){
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            // PengajuanBiayaKonstruksiPersetujuanEntity pengajuan = PengajuanBiayaKonstruksiPersetujuanEntity.find("id_user = ?1 AND tanggal_persetujuan IS NULL ORDER BY urutan ASC ", ue.id_user).firstResult();
            List<PengajuanBiayaKonstruksiEntity> listPengajuan = PengajuanBiayaKonstruksiEntity.listAll();   
            
            return Response.ok().entity(ResponseHandler.ok("Data Tersedia", listPengajuan)).build();

            
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
    @GET
    @Path("/get-monitoring-pengajuan_bk")
    public Response getMonitoringPengajuanBk(
        @Context SecurityContext ctx
    ){
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            // PengajuanBiayaKonstruksiPersetujuanEntity pengajuan = PengajuanBiayaKonstruksiPersetujuanEntity.find("id_user = ?1 AND tanggal_persetujuan IS NULL ORDER BY urutan ASC ", ue.id_user).firstResult();
            List<PengajuanBiayaKonstruksiEntity> listPengajuan = PengajuanBiayaKonstruksiEntity.listAll();   
            
            return Response.ok().entity(ResponseHandler.ok("Data Tersedia", listPengajuan)).build();

            
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @POST
    @Path("/approve-pengajuan-bk")
    @Transactional
    public Response approvePengajuanBk(
        @QueryParam("id_pengajuan_bk") String id_pengajuan_bk, @Context SecurityContext ctx, @QueryParam("catatan") String catatan
    ){
        try {
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            PengajuanBiayaKonstruksiEntity pengajuanBk = PengajuanBiayaKonstruksiEntity.findById(id_pengajuan_bk);
            PengajuanBiayaKonstruksiPersetujuanEntity getPersetujuan = PengajuanBiayaKonstruksiPersetujuanEntity.find("pengajuan_bk = ?1 and id_user = ?2", pengajuanBk, ue.id_user).firstResult();
            getPersetujuan.tanggal_persetujuan = LocalDateTime.now();
            getPersetujuan.catatan_persetujuan = (catatan != "" || catatan != null)?catatan:"";
            return Response.ok().entity(ResponseHandler.ok("Approved Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

}
