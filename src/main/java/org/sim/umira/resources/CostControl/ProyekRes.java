package org.sim.umira.resources.CostControl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CostControl.BkKategoriDto;
import org.sim.umira.dtos.CostControl.CreateProyekDto;
import org.sim.umira.dtos.CostControl.MosDto;
import org.sim.umira.dtos.CostControl.RapaDto;
import org.sim.umira.dtos.CostControl.ResponseProyekDto;
import org.sim.umira.dtos.CostControl.ResponseRapaPendapatanUsahaDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.CostControl.AdendumProyekEntity;
import org.sim.umira.entities.CostControl.BiayaKontruksiEntity;
import org.sim.umira.entities.CostControl.KategoriEntity;
import org.sim.umira.entities.CostControl.MosEntity;
import org.sim.umira.entities.CostControl.MosNewEntity;
import org.sim.umira.entities.CostControl.PendapatanUsahaEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.RapaEntity;
import org.sim.umira.entities.CostControl.ScurveEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/CostControl/Proyek")
@Secured
public class ProyekRes {

    @Inject
    EntityManager em;
    
    @POST
    @Path("/create-proyek")
    @Transactional
    public Response createProyek(
        @Valid @RequestBody CreateProyekDto create, @Context SecurityContext ctx
    ){
        try {
            // System.out.println(create.tanggal_awal_kontrak);
            UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
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
            proyek.periode_awal_progress = create.periode_awal_progress;
            proyek.periode_akhir_progress = create.periode_akhir_progress;
            proyek.created_at = LocalDateTime.now();
            proyek.created_by = ue.id_user;
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
            
            BigInteger total_pu = BigInteger.ZERO;
            for (PendapatanUsahaEntity pendapatanUsahaEntity : pu) {
                total_pu = total_pu.add(pendapatanUsahaEntity.nominal_pu);
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
            BigInteger total_pu = BigInteger.ZERO;
            for (PendapatanUsahaEntity pendapatanUsahaEntity : pu) {
                total_pu = total_pu.add(pendapatanUsahaEntity.nominal_pu);
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
             BigInteger total_scurve = BigInteger.ZERO;
            List<ScurveEntity> scurve = ScurveEntity.find("proyek = ?1", proyek).list();
            for(ScurveEntity se: scurve){
                if(se.nominal_scurve != null){
                    total_scurve = total_scurve.add(se.nominal_scurve);
                }
            }
            System.out.println(kerja_tambah_total);
            // ResponseProyekDto res = new ResponseProyekDto(total_bk, total_pu, currMos, kerja_tambah_total, kerja_kurang_total, proyek);
            ResponseProyekDto<ProyekEntity> dto = new ResponseProyekDto<>(total_bk, total_pu, currMos, kerja_tambah_total, kerja_kurang_total,  total_scurve,proyek);
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
            proyek.periode_awal_progress = create.periode_awal_progress;
            proyek.periode_akhir_progress = create.periode_akhir_progress;
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
    // Mobile endpoint

    @GET
    @Path("/M/get-proyek-dashboard")
    public Response getMProyekDashboard(
        @QueryParam("search") String search
    ){
        List<ProyekEntity> proyek;

        if(search != null && !search.isEmpty()){
            proyek = ProyekEntity.find("nama_proyek like ?1 OR kode_proyek like ?1", "%"+search+"%").list();
        }else{
            proyek = ProyekEntity.listAll();
        }

        ArrayList<ResponseProyekDto> responseProyek = new ArrayList<>();
        for(ProyekEntity proE: proyek){
            List<PendapatanUsahaEntity> pu = PendapatanUsahaEntity.find("proyek = ?1", proE).list();
            
            BigInteger total_pu = BigInteger.ZERO;
            for (PendapatanUsahaEntity pendapatanUsahaEntity : pu) {
                total_pu = total_pu.add(pendapatanUsahaEntity.nominal_pu);
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
    @Path("/get-bk-pu-by-week")
    public Response getBkPuByWeek(
        @QueryParam("id_project") String id_project
    ){
        try {
            ProyekEntity pe = ProyekEntity.findById(id_project);
            List<WeekData> result = new ArrayList<>();
            LocalDate start = pe.periode_awal_progress;
            LocalDate end = pe.periode_akhir_progress;
            LocalDate currentStart = start;
            int weekNumber = 1;

            BigInteger cumulativeBk = BigInteger.ZERO;
            BigInteger cumulativePu = BigInteger.ZERO;
            // BigInteger cumulativeMos = BigInteger.ZERO;

        while (!currentStart.isAfter(end)) {
            LocalDate currentEnd = currentStart.plusDays(6);
            if (currentEnd.isAfter(end)) {
                currentEnd = end;
            }
            LocalDateTime startCurrent = currentStart.atStartOfDay();
            LocalDateTime endCurrent = currentEnd.atTime(LocalTime.MAX);
            // List<BiayaKontruksiEntity> bk = BiayaKontruksiEntity.find("tanggal_penerimaan = ?1 BETWEEN tanggal_penerimaan = ?2",currentStart, currentEnd).list();
            List<BiayaKontruksiEntity> bk = BiayaKontruksiEntity.find(
                    "proyek = ?1 AND tanggal_penerima BETWEEN ?2 AND ?3",
                    pe,
                    startCurrent,
                    endCurrent
                ).list();
            BigInteger total_bk = BigInteger.ZERO;
            for(BiayaKontruksiEntity biayaK: bk){
                if (biayaK.harga_total != null) {
                    total_bk = total_bk.add(biayaK.harga_total.toBigInteger());
                }
            }
            PendapatanUsahaEntity pu = PendapatanUsahaEntity.find("week_pu = ?1 AND proyek = ?2", weekNumber, pe).firstResult();
            MosNewEntity mos = MosNewEntity.find("week = ?1 AND proyek = ?2", weekNumber, pe).firstResult();
            cumulativeBk = cumulativeBk.add(total_bk);
            if(pu != null){
                cumulativePu = cumulativePu.add(pu.nominal_pu);
            }
            
            result.add(new WeekData(weekNumber, currentStart, currentEnd, total_bk, (pu != null)?pu.nominal_pu:BigInteger.ZERO, (mos != null)?mos.nominal_mos:BigInteger.ZERO, cumulativeBk, cumulativePu));
            // result.add(new WeekData(weekNumber, currentStart, currentEnd));

            weekNumber++;
            currentStart = currentStart.plusDays(7);
        }

            return Response.ok().entity(ResponseHandler.ok("Inquiry BkPu", result.stream().sorted(Comparator.comparing(WeekData::week)).toList())).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
        
    }
    @GET
    @Path("/get-bk-kategori")
    public Response getBkKategori(@QueryParam("id_proyek") String id_proyek){
        // List<ProyekEntity> proyek = ProyekEntity.listAll();
        ProyekEntity proyek = ProyekEntity.findById(id_proyek);
        List<KategoriEntity> kategori = KategoriEntity.listAll();
        List<BkKategoriDto> bk = new ArrayList<>();
        for(KategoriEntity ktgr: kategori){
            List<BiayaKontruksiEntity> biayaK = BiayaKontruksiEntity.find("SELECT b FROM BiayaKontruksiEntity b JOIN b.rapa r JOIN r.costCodeRapa c JOIN c.kategori k WHERE b.proyek = ?1 AND c.kategori = ?2", proyek, ktgr).list();
            BigDecimal total_biaya_k = BigDecimal.ZERO;
            if(biayaK.size() > 0){
                for(BiayaKontruksiEntity bkE: biayaK){
                    total_biaya_k = total_biaya_k.add(bkE.harga_total);
                }
            }
            bk.add(new BkKategoriDto(ktgr.id_kategori, ktgr.kode_kategori, ktgr.nama_kategori, total_biaya_k));
        }
        // List<BkKategoriDto> bk = BiayaKontruksiEntity.find("SELECT k.kode_kategori as kode_kategori, k.nama_kategori as nama_kategori, SUM(b.harga_total) as biaya FROM BiayaKontruksiEntity b JOIN b.rapa r JOIN r.costCodeRapa c JOIN c.kategori k WHERE b.proyek = ?1 GROUP BY k.nama_kategori, k.kode_kategori", proyek).project(BkKategoriDto.class).list();
        return Response.ok().entity(ResponseHandler.ok("Get BK Kategori", bk)).build();
    }


    @GET
    @Path("/get-rapa-proyek-dashboard")
    @Transactional
    public Response getRapaByProyek(
            @QueryParam("id_proyek") String id_proyek, @QueryParam("id_kategori") String id_kategori) {
        try {

            ProyekEntity proyek = ProyekEntity.findById(id_proyek);
            KategoriEntity kategoriGet = KategoriEntity.findById(id_kategori);
            // List<RapaEntity> rapa = RapaEntity.find("proyek = ?1", proyek).list();
            List<RapaEntity> rapaOri = em.createQuery("""
                SELECT r FROM RapaEntity r
                JOIN FETCH r.costCodeRapa c
                LEFT JOIN FETCH c.kategori
                WHERE r.proyek = ?1
                AND c.kategori = ?2
            """, RapaEntity.class)
            .setParameter(1, proyek)
            .setParameter(2, kategoriGet)
            .getResultList();

            List<RapaDto> rapa = rapaOri.stream()
            .map(RapaDto::new)
            .toList();
            List<ResponseRapaPendapatanUsahaDto> rapaNew = new ArrayList<>();
            for (RapaDto rapaEntity : rapa) {
                RapaEntity getRapa = RapaEntity.findById(rapaEntity.id_rapa);
                List<BiayaKontruksiEntity> bk = BiayaKontruksiEntity.find("rapa =?1 ", getRapa).list();
                BigDecimal total_bk_rapa = BigDecimal.ZERO;
                for (BiayaKontruksiEntity bkArr : bk) {
                    total_bk_rapa = total_bk_rapa.add(bkArr.harga_total);
                }

                rapaNew.add(new ResponseRapaPendapatanUsahaDto(rapaEntity.id_rapa, rapaEntity.kategori,
                        rapaEntity.kode_rap, null,
                        rapaEntity.item_pekerjaan, rapaEntity.spesifikasi, rapaEntity.satuan, rapaEntity.volume,
                        rapaEntity.harga_satuan,
                        rapaEntity.harga_total, total_bk_rapa));
            }

            return Response.ok().entity(ResponseHandler.ok("get Rapa by proyek Berhasil", rapaNew)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }

    }


    public record WeekData(int week, LocalDate startDate, LocalDate endDate, BigInteger bk, BigInteger pu, BigInteger mos, BigInteger kumulativeBk, BigInteger kumulativePu) {}

}
