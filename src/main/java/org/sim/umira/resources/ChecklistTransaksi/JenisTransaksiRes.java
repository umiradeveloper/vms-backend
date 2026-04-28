package org.sim.umira.resources.ChecklistTransaksi;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.ChecklistTransaksi.JenisTransaksiDto;
import org.sim.umira.entities.ChecklistTransaksi.JenisTransaksiEntity;
import org.sim.umira.handlers.ResponseHandler;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/ChecklistTransaksi/jenis-transaksi")
public class JenisTransaksiRes {
    
    @POST
    @Path("/create-jenis-transaksi")
    @Transactional
    public Response createTransaksi(@RequestBody JenisTransaksiDto jenis){
        try {
            JenisTransaksiEntity jenisTrans = new  JenisTransaksiEntity();
            jenisTrans.nama_transaksi = jenis.nama_transaksi;
            jenisTrans.jenis_transaksi = jenis.jenis_transaksi;
            jenisTrans.no_urut = jenis.no_urut;
            jenisTrans.tipe = jenis.tipe;
            jenisTrans.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Jenis Transaksi", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-jenis-transaksi")
    public Response getJenisTransaksi(){
        try {
            // List<JenisTransaksiEntity> jenisTrans = JenisTransaksiEntity.listAll();
            List<JenisTransaksiEntity> jenisTrans = JenisTransaksiEntity.find("select distinct j.jenis_transaksi from JenisTransaksiEntity j").list();
            
            return Response.ok().entity(ResponseHandler.ok("Inquiry Jenis Transaksi", jenisTrans)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-nama-transaksi-by-jenis")
    public Response getNamaTransaksi(@QueryParam("jenis_transaksi") String jenis_transaksi){
        try {
            // List<JenisTransaksiEntity> jenisTrans = JenisTransaksiEntity.listAll();
            List<JenisTransaksiEntity> jenisTrans = JenisTransaksiEntity.find("jenis_transaksi = ?1 ORDER BY no_urut ASC", jenis_transaksi).list();
            
            return Response.ok().entity(ResponseHandler.ok("Inquiry nama Transaksi", jenisTrans)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
}
