package org.sim.umira.resources;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.sim.umira.entities.RoleEntity;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.VmsVendorEntity;
import org.sim.umira.entities.ChecklistTransaksi.TransaksiEntity;
import org.sim.umira.entities.CostControl.ActionPlanEntity;
import org.sim.umira.entities.CostControl.PendapatanUsahaEntity;
import org.sim.umira.entities.CostControl.PengajuanBiayaKonstruksiEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.ScurveEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/Dashboard")
@Secured
public class DashboardRes {
    @GET
    @Path("/get-data/card-vendor")
    public Response getDataCardVendor() {
        
        try {

            List<CardVendor> cardVendor = new ArrayList<>();

            BigInteger vendorPengajuanDaftar = BigInteger.ZERO;
            BigInteger vendorBelumDaftar = BigInteger.ZERO;
            BigInteger vendorTelahTerdaftar = BigInteger.ZERO;
            BigInteger vendorTotal = BigInteger.ZERO;

            RoleEntity roleVendor = RoleEntity.find(
                    "kode_role = ?1",
                    "01").firstResult();

            List<UserEntity> vendorUsers = UserEntity.find(
                    "role = ?1",
                    roleVendor).list();

            for (UserEntity user : vendorUsers) {

                List<VmsVendorEntity> vendorList = VmsVendorEntity.find(
                        "user = ?1",
                        user).list();

                boolean hasPengajuan = !vendorList.isEmpty();

                boolean isApproved = vendorList.stream()
                        .filter(v -> v.isApproval != null)
                        .anyMatch(v -> v.isApproval == 1);

                if (isApproved) {

                    vendorTelahTerdaftar = vendorTelahTerdaftar.add(BigInteger.ONE);

                } else if (hasPengajuan) {

                    vendorPengajuanDaftar = vendorPengajuanDaftar.add(BigInteger.ONE);

                } else {

                    vendorBelumDaftar = vendorBelumDaftar.add(BigInteger.ONE);
                }

                vendorTotal = vendorTotal.add(BigInteger.ONE);
            }

            cardVendor.add(
                    new CardVendor(
                            "Belum Rekanan",
                            vendorBelumDaftar));

            cardVendor.add(
                    new CardVendor(
                            "Rekanan",
                            vendorTelahTerdaftar));

            cardVendor.add(
                    new CardVendor(
                            "Pengajuan Rekanan",
                            vendorPengajuanDaftar));

            cardVendor.add(
                    new CardVendor(
                            "Total",
                            vendorTotal));

            return Response.ok()
                    .entity(
                            ResponseHandler.ok(
                                    "Get Data Card Vendor Berhasil",
                                    cardVendor))
                    .build();

        } catch (Exception e) {

            throw new InternalServerErrorException(
                    e.getMessage());
        }
    }

    @GET
    @Path("/get-data/vendor-pengajuan")
    public Response getDataPengajuanVendor() {
        try {
            List<VmsVendorEntity> vendorPengajuan = VmsVendorEntity.find(
                    "ORDER BY tanggal_pengajuan DESC")
                    .page(0, 5)
                    .list();
            return Response.ok().entity(ResponseHandler.ok("Get Data Pengajuan Vendor Berhasil", vendorPengajuan))
                    .build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-data/get-pengajuan-akun-vendor")
    public Response getDataPengajuanAkunVendor() {
        try {
            RoleEntity re = RoleEntity.find("kode_role = ?1", "01").firstResult();
            List<UserEntity> vendorAkun = UserEntity.find(
                    "isApproval  = ?1 AND role = ?2", 0, re)
                    .list();
            return Response.ok().entity(ResponseHandler.ok("Get Data Akun Vendor Berhasil", vendorAkun))
                    .build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-data/card-checklist-pembayaran")
    public Response getDataCardChecklist() {
        
        try {

            List<CardVendor> cardVendor = new ArrayList<>();
            List<TransaksiEntity> pengajuanCount = TransaksiEntity.find("status_pengajuan = ?1 ", "Pengajuan").list();
            List<TransaksiEntity> verifiedCount = TransaksiEntity.find("status_pengajuan = ?1 ", "Verified").list();
            List<TransaksiEntity> paymentCount = TransaksiEntity.find("status_pengajuan = ?1 ", "Payment").list();
            List<TransaksiEntity> rejectCount = TransaksiEntity.find("status_pengajuan = ?1 ", "Reject").list();
            cardVendor.add(new CardVendor("Pengajuan Transaksi", BigInteger.valueOf(pengajuanCount.size())));
            cardVendor.add(new CardVendor("Verified Transaksi", BigInteger.valueOf(verifiedCount.size())));
            cardVendor.add(new CardVendor("Payment Transaksi", BigInteger.valueOf(paymentCount.size())));
            cardVendor.add(new CardVendor("Reject Transaksi", BigInteger.valueOf(rejectCount.size())));
            return Response.ok()
                    .entity(
                            ResponseHandler.ok(
                                    "Get Data Card Payment Berhasil",
                                    cardVendor))
                    .build();

        } catch (Exception e) {

            throw new InternalServerErrorException(
                    e.getMessage());
        }
    }
    @GET
    @Path("/get-data/get-pengajuan-checklist-pembayaran")
    public Response getDataPengajuanChecklistPembayaran() {
        try {
           List<TransaksiEntity> trx = TransaksiEntity.find(
                    "ORDER BY tanggal_pengajuan DESC")
                    .page(0, 10)
                    .list();
            return Response.ok().entity(ResponseHandler.ok("Get Data Pengajuan Transaksi Pembayaran Berhasil", trx))
                    .build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }


    @GET
    @Path("/get-data/get-chart-proyek")
    public Response getChartProyek(){
        
        List<ProyekEntity> pe = ProyekEntity.listAll();
        for(ProyekEntity proj: pe){
            if(proj.periode_awal_progress == null){
                throw new BadRequestException("Periode Awal Progress Harus Di Isi");
            }
             if(proj.periode_akhir_progress == null){
                throw new BadRequestException("Periode Akhir Progress Harus Di Isi");
            }
        }


        try {
            
            List<ResChart> resChart = new ArrayList<>();
            for(ProyekEntity proE: pe){
                List<SeriesChart> result = new ArrayList<>();
                LocalDate start = proE.periode_awal_progress;
                LocalDate end = proE.periode_akhir_progress;
                LocalDate currentStart = start;
                int weekNumber = 1;

                while (!currentStart.isAfter(end)) {
                    LocalDate currentEnd = currentStart.plusDays(6);
                    if (currentEnd.isAfter(end)) {
                        currentEnd = end;
                    }
                    PendapatanUsahaEntity pu = PendapatanUsahaEntity.find("week_pu = ?1 AND proyek = ?2", weekNumber, proE).firstResult();
                    ScurveEntity scurve = ScurveEntity.find("week = ?1 AND proyek = ?2", weekNumber, proE).firstResult();
                    ActionPlanEntity action = ActionPlanEntity.find("week = ?1 AND proyek = ?2", weekNumber, proE).firstResult();
    
                    BigDecimal persen_pu = null;
                    if(pu != null){
                        persen_pu = (pu.nominal_pu.compareTo(BigInteger.ZERO)>0)?new BigDecimal(pu.nominal_pu).divide(new BigDecimal(proE.biaya_rab), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")):null;
                    }
                    BigDecimal persen_action_plan = null;
                    if(action != null){
                        persen_action_plan = (action.nominal_action_plan.compareTo(BigInteger.ZERO)>0)?new BigDecimal(action.nominal_action_plan).divide(new BigDecimal(proE.biaya_rab), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")):null;
                    }
                    BigDecimal persen_scurve = null;
                    if(scurve != null){
                        persen_scurve = (scurve.nominal_scurve.compareTo(BigInteger.ZERO) > 0)?new BigDecimal(scurve.nominal_scurve).divide(new BigDecimal(proE.biaya_rab), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100")): null;
                    }
                    
                   
                 
                    

                    result.add(new SeriesChart(weekNumber, currentStart, currentEnd, (persen_scurve != null)?persen_scurve.toBigInteger():null, (persen_action_plan != null)?persen_action_plan.toBigInteger():null, (persen_pu != null)?persen_pu.toBigInteger():null));

                    weekNumber++;
                    currentStart = currentStart.plusDays(7);
                }
                resChart.add(new ResChart(proE,  result));
            }
            
            return Response.ok().entity(ResponseHandler.ok("Get Data Chart Proyek Berhasil", resChart))
                    .build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @GET
    @Path("/get-data/get-card-costcontrol")
    public Response getCardCostControl(){
        try {
            List<CardVendor> cardCostControl = new ArrayList<>();
            List<PengajuanBiayaKonstruksiEntity> pengajuan = PengajuanBiayaKonstruksiEntity.listAll();
            List<PengajuanBiayaKonstruksiEntity> approval = PengajuanBiayaKonstruksiEntity.find("""
                SELECT DISTINCT p
                FROM PengajuanBiayaKonstruksiEntity p
                WHERE EXISTS (
                    SELECT 1
                    FROM PengajuanBiayaKonstruksiPersetujuanEntity ps
                    WHERE ps.pengajuan_bk = p
                    AND ps.tanggal_persetujuan IS NULL
                    AND ps.urutan = (
                        SELECT MIN(ps2.urutan)
                        FROM PengajuanBiayaKonstruksiPersetujuanEntity ps2
                        WHERE ps2.pengajuan_bk = p
                            AND ps2.tanggal_persetujuan IS NULL
                    )
                )
            """).list();  
            cardCostControl.add(new CardVendor("Pengajuan Perubahan BK", BigInteger.valueOf(pengajuan.size())));
            cardCostControl.add(new CardVendor("Approval Perubahan BK", BigInteger.valueOf(approval.size())));

            return Response.ok().entity(ResponseHandler.ok("Get Data Card BK", cardCostControl)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public record CardVendor(String nama, BigInteger total) {}
    public record WeekData(Integer week, LocalDate currentStart, LocalDate currentEnd){}

    public record SeriesChart(Integer week, LocalDate currentStart, LocalDate currentEnd, BigInteger kurva_s, BigInteger action_plan, BigInteger realisasi){}

    public record ResChart(ProyekEntity proyek, List<SeriesChart> series){}

}
