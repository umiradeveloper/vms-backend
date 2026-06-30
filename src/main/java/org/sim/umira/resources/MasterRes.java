package org.sim.umira.resources;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.configs.GoogleCalendarConfig;
import org.sim.umira.dtos.CostControl.ReportProyekDto;
import org.sim.umira.dtos.HumanResources.KlasifikasiWorksDto;
import org.sim.umira.entities.BranchEntity;
import org.sim.umira.entities.RoleEntity;
import org.sim.umira.entities.VmsVendorDetailMinioEntity;
import org.sim.umira.entities.VmsVendorEntity;
import org.sim.umira.entities.CostControl.KategoriEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.HumanResources.KlasifikasiWorkEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.kafka.KafkaProducers;
import org.sim.umira.kafka.DTO.UploadEventDto;
import org.sim.umira.minio.MinioServices;
import org.sim.umira.services.PdfService;
import org.sim.umira.services.YearCalendarService;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/master")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MasterRes {

    @Inject
    PdfService pdfService;

    @Inject
    RedisDataSource redis;

    @Inject
    MinioServices minio;

    @Inject
    KafkaProducers kafkaProducers;

    @GET
    @Path("/get-branch")
    public Response getBranch() {
        List<BranchEntity> be = BranchEntity.listAll();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Branch Success", be)).build();
    }

    @GET
    @Path("/get-role")
    public Response getRole() {
        List<RoleEntity> be = RoleEntity.find("kode_role != ?1 AND kode_role != ?2", "99", "01").list();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Role Success", be)).build();
    }

    @GET
    @Path("/get-week-by-project")
    public Response getWeekByProject(
            @QueryParam("id_project") String id_project) {
        ProyekEntity pe = ProyekEntity.findById(id_project);
        // for(ProyekEntity proj: pe){
        if (pe.periode_awal_progress == null) {
            throw new BadRequestException("Periode Awal Progress Harus Di Isi");
        }
        if (pe.periode_akhir_progress == null) {
            throw new BadRequestException("Periode Akhir Progress Harus Di Isi");
        }
        // }

        try {

            List<WeekData> result = new ArrayList<>();
            LocalDate start = pe.periode_awal_progress;
            LocalDate end = pe.periode_akhir_progress;
            LocalDate currentStart = start;
            int weekNumber = 1;

            while (!currentStart.isAfter(end)) {
                LocalDate currentEnd = currentStart.plusDays(6);
                if (currentEnd.isAfter(end)) {
                    currentEnd = end;
                }

                result.add(new WeekData(weekNumber, currentStart, currentEnd));

                weekNumber++;
                currentStart = currentStart.plusDays(7);
            }

            return Response.ok().entity(ResponseHandler.ok("Inquiry Role Success",
                    result.stream().sorted(Comparator.comparing(WeekData::week)).toList())).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }

    }

    @GET
    @Path("/kategori")
    public Response getKategori() {
        List<KategoriEntity> kategori = KategoriEntity.listAll();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Kategori Success", kategori)).build();
    }

    @GET
    @Path("/all-vendor")
    public Response getVendor() {
        try {
            List<VmsVendorEntity> vv = VmsVendorEntity.find(
                    "id IN (" +
                            "SELECT MAX(id) FROM VmsVendorEntity " +
                            "WHERE isApproval = ?1 " +
                            "GROUP BY nama_perusahaan" +
                            ") ORDER BY tanggal_pengajuan DESC",
                    1).list();

            return Response.ok().entity(ResponseHandler.ok("Inquiry Berhasil", vv)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }

    }

    @GET
    @Path("/check")
    public String checkRedis() {

        ValueCommands<String, String> value = redis.value(String.class);

        value.set("test", "hello redis");

        return value.get("test");
    }

    @GET
    @Path("/minio-check")
    public Response checkBucket() {
        try {
            return Response.ok().entity(ResponseHandler.ok("Inquiry Berhasil", minio.listObjects())).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException("Internal server error");
        }
    }

    @GET
    @Path("/minio-file")
    // @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/pdf")
    public Response checkFile(
            @QueryParam("url") String url) {
        try {
            InputStream file = minio.getFile(url);
            return Response.ok(file).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-pdf")
    public Response getPdf() {
        try {
            ReportProyekDto project = new ReportProyekDto(
                    "Pembangunan Gedung A",
                    "PT Maju Mundur",
                    LocalDate.now(),
                    LocalDate.now().plusMonths(6),
                    75);

            byte[] pdf = pdfService.generatePdf(project);

            return Response.ok(pdf)
                    .header(
                            "Content-Disposition",
                            "attachment; filename=report.pdf")
                    .build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/Execute-upload-vms-detail")
    public Response executeUploadVms() {
        try {
            List<VmsVendorDetailMinioEntity> listDetail = VmsVendorDetailMinioEntity.listAll();
            for (VmsVendorDetailMinioEntity detail : listDetail) {
                java.nio.file.Path path = java.nio.file.Path.of(detail.url_dokumen);

                if (!Files.exists(path)) {

                    System.out.printf(
                            "[SKIP] File not found: %s%n",
                            detail.url_dokumen);

                    continue;
                }
                String filename = Paths.get(detail.url_dokumen).getFileName().toString();
                kafkaProducers.uploadDoc(new UploadEventDto("uploads", filename, detail.url_dokumen));
            }
            return Response.ok().entity(ResponseHandler.ok("Inquiry Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    public record WeekData(int week, LocalDate startDate, LocalDate endDate) {
    }

    @ConfigProperty(name = "google.calendar.umira-absensi")
    String absensiCalendar;

    @ConfigProperty(name = "google.calendar.umira-cuti")
    String cutiCalendar;

    @GET
    @Path("/check-absensi-calendar")
    public Response createAbsensiCalendarGoogle(@QueryParam("nama") String nama,
            @QueryParam("tanggal") String tanggal_absen) {
        try {
            Calendar service = GoogleCalendarConfig.getService();
            LocalDate tanggal = LocalDate.parse(tanggal_absen);
            Event event = new Event();

            event.setSummary("Absensi - " + nama);
            event.setDescription("Kehadiran Karyawan");

            // Biru
            event.setColorId("9");

            event.setStart(
                    new EventDateTime()
                            .setDate(
                                    new DateTime(tanggal.toString())));

            event.setEnd(
                    new EventDateTime()
                            .setDate(
                                    new DateTime(
                                            tanggal.plusDays(1).toString())));

            Event created = service.events()
                    .insert(absensiCalendar, event)
                    .execute();

            return Response.ok().entity(ResponseHandler.ok("absensi Berhasil", created.getHtmlLink())).build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }

    }

    @GET
    @Path("/calendar")
    public Response getTanggalCalendar() {
        try {
            String holidayCalendarId = "id.indonesian#holiday@group.v.calendar.google.com";
            Calendar service = GoogleCalendarConfig.getService();

            // 1. ambil libur nasional
            Set<LocalDate> holidays = YearCalendarService.getHolidays(service, holidayCalendarId);

            // 2. generate 1 tahun
            List<YearCalendarService.DayInfo> calendar = YearCalendarService.generateYear(2026, holidays);

            // for (Event e : events.getItems()) {
            // System.out.println("LIBUR: " + e.getSummary());
            // System.out.println("DATE : " + e.getStart().getDate());
            // }
            // 3. output
            for (YearCalendarService.DayInfo d : calendar) {
                System.out.println(d.date + " -> " + d.type);
            }
            return Response.ok().entity(ResponseHandler.ok("get satu tahun", calendar)).build();
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }

    }


    @POST
    @Path("/create-klasifikasi-works")
    @Transactional
    public Response createKlasifikasiWorks(@Valid @RequestBody KlasifikasiWorksDto klasifikasiWork){
        try {

            KlasifikasiWorkEntity klasifikasi = new KlasifikasiWorkEntity();
            klasifikasi.klasifikasi_works = klasifikasiWork.klasifikasi_works;
            klasifikasi.nama_klasifikasi_works = klasifikasiWork.nama_klasifikasi;
            klasifikasi.is_shift = klasifikasiWork.is_shift;
            klasifikasi.is_office = klasifikasiWork.is_office;
            klasifikasi.is_jadwal = klasifikasiWork.is_jadwal;
            klasifikasi.jam_masuk = klasifikasiWork.jam_masuk;
            klasifikasi.jam_keluar = klasifikasiWork.jam_keluar;
            klasifikasi.persist();

            return Response.ok().entity(ResponseHandler.ok("create klasifikasi works berhasil", null)).build();
            
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }
    @GET
    @Path("/get-klasifikasi-works")
    @Transactional
    public Response getKlasifikasiWorks(){
        try {

            List<KlasifikasiWorkEntity> list = KlasifikasiWorkEntity.listAll();

            return Response.ok().entity(ResponseHandler.ok("get klasifikasi works berhasil", list)).build();
            
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
            // TODO: handle exception
        }
    }

}
