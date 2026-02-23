package org.sim.umira.resources.CostControl;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CostControl.CreateAnnouncementDto;
import org.sim.umira.entities.CostControl.AnnouncementEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/CostControl/Announcement")
@Secured
public class AnnouncementRes {
    

 @POST
@Path("/create-announcement")
@Consumes(MediaType.MULTIPART_FORM_DATA)
@Transactional
public Response create(@MultipartForm CreateAnnouncementDto dto) {

    try {

        String uploadDir = "uploads/announcements/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String filePath = null;

        if (dto.dokumen != null) {
            String fileName = dto.dokumen.fileName();
            Path path = Path.of(uploadDir + fileName);

            Files.copy(dto.dokumen.uploadedFile(),
                    path,
                    StandardCopyOption.REPLACE_EXISTING);

            filePath = path.toString();
        }

        Announcement announcement = new Announcement();
        announcement.judulAnnouncement = dto.judul_announcement;
        announcement.isiAnnouncement = dto.isi_announcement;
        announcement.dokumenPath = filePath;

        announcement.persist();

        return Response.ok().entity(Map.of("message", "Berhasil disimpan")).build();

    } catch (Exception e) {
        return Response.serverError().entity("Upload gagal").build();
    }
}

    @GET
    @Path("/get-announcement")
    public Response getAnnouncement(){
        List<AnnouncementEntity> announcement = AnnouncementEntity.listAll();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Announcement Berhasil", announcement)).build();
    }

    // @PATCH
    // @Path("/update-kategori")
    // @Transactional
    // public Response updateKategori(
    //     @Valid @RequestBody CreateKategoriDto create
    // ){
    //     try {
    //         KategoriEntity kategori = KategoriEntity.findById(create.id_kategori);
    //         kategori.nama_kategori = create.nama_kategori;
    //         kategori.kode_kategori = create.kode_kategori;
    //         return Response.ok().entity(ResponseHandler.ok("Update Kategori Berhasil", null)).build();
    //     } catch (Exception e) {
    //         throw new InternalError(e.getMessage());
    //     }
        
    // }

    @DELETE
    @Path("/delete-announcement")
    @Transactional
    public Response deleteAnnouncement(
        @QueryParam("id") String id
    ){
        try {
            Boolean announcement = AnnouncementEntity.deleteById(id);
       
            return Response.ok().entity(ResponseHandler.ok("Delete Announcement Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }
}
