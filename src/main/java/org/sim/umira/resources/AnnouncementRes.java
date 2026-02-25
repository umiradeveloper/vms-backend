package org.sim.umira.resources;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.CreateAnnouncementDto;
import org.sim.umira.entities.AccessAnnouncementEntity;
import org.sim.umira.entities.AnnouncementEntity;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.entities.CostControl.PendapatanUsahaEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/Announcement")
@Secured
public class AnnouncementRes {

    @POST
    @Path("/create-announcement")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    public Response create(@MultipartForm CreateAnnouncementDto dto, @Context SecurityContext ctx) {

        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        try {

            String uploadDir = "uploads/announcements/";
            File dir = new File(uploadDir);
            if (!dir.exists())
                dir.mkdirs();

            String filePath = null;

            if (dto.dokumen != null) {
                // String fileName = dto.dokumen.fileName();
                String ext = dto.dokumen.fileName().substring(dto.dokumen.fileName().lastIndexOf("."));
                String randomFileName = UUID.randomUUID().toString() + ext;
                java.nio.file.Path path = java.nio.file.Path.of(uploadDir + "/" + randomFileName);

                Files.copy(dto.dokumen.uploadedFile(),
                        path,
                        StandardCopyOption.REPLACE_EXISTING);

                filePath = path.toString();
            }
            String filePathFoto = null;
            if (dto.foto_pengumuman != null) {
                String uploadFoto = "uploads/announcements/foto-pengumuman";
                String ext = dto.foto_pengumuman.fileName().substring(dto.foto_pengumuman.fileName().lastIndexOf("."));
                String randomFileName = UUID.randomUUID().toString() + ext;
                java.nio.file.Path path = java.nio.file.Path.of(uploadFoto + "/" + randomFileName);

                Files.copy(dto.foto_pengumuman.uploadedFile(),
                        path,
                        StandardCopyOption.REPLACE_EXISTING);

                filePathFoto = path.toString();
            }

            AnnouncementEntity announcement = new AnnouncementEntity();
            announcement.judulAnnouncement = dto.judul_announcement;
            announcement.isiAnnouncement = dto.isi_announcement;
            announcement.dokumenPath = filePath;
            announcement.created_at = LocalDateTime.now();
            announcement.userBy = ue;
            announcement.fotoPath = filePathFoto;
            // announcement.roleId = dto.role_id;
            announcement.persist();
            String textRoleId = dto.role_id;
            String[] roleId = textRoleId.split(",");
            for (String role : roleId) {
                AccessAnnouncementEntity accessAnnouncement = new AccessAnnouncementEntity();
                accessAnnouncement.announcement = announcement;
                accessAnnouncement.kode_role = role;
                accessAnnouncement.persist();
            }

            return Response.ok().entity(ResponseHandler.ok("Data Announcement Berhasil Di Simpan", null)).build();

        } catch (Exception e) {
            return Response.serverError().entity("Upload gagal").build();
        }
    }

    @GET
    @Path("/get-announcement")
    public Response getAnnouncement(@Context SecurityContext ctx) {

        UserEntity ue = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
        List<AnnouncementEntity> announcement;
        if (ue.role.kode_role.equals("99")) {
            announcement = AnnouncementEntity.listAll();
        } else {
            announcement = AnnouncementEntity.find("""
                        SELECT DISTINCT p
                        FROM AnnouncementEntity p JOIN p.created_by u
                        WHERE EXISTS (
                            SELECT 1
                            FROM AccessAnnouncementEntity ps
                            WHERE ps.announcement = p
                            AND ps.kode_role = ?1
                        )
                    """, ue.role.kode_role).list();
        }

        return Response.ok().entity(ResponseHandler.ok("Inquiry Announcement Berhasil", announcement)).build();
    }

   

    @DELETE
    @Path("/delete-announcement")
    @Transactional
    public Response deleteAnnouncement(
            @QueryParam("id") String id) {
        try {
            Boolean announcement = AnnouncementEntity.deleteById(id);

            return Response.ok().entity(ResponseHandler.ok("Delete Announcement Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }

    }

    @GET
    @Path("/dokumen-file")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/pdf")
    public Response getFile(
            @QueryParam("id") String id) {
        try { // direktori saat jar dijalankan
            AnnouncementEntity announcement = AnnouncementEntity.findById(id);
            InputStream imageStream = Files.newInputStream(Paths.get(announcement.dokumenPath));
            return Response.ok(imageStream).build();
        } catch (Exception e) {
            throw new InternalError("Cant get file");
        }

    }

    @GET
    @Path("/foto-pengumuman")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({
            "application/pdf",
            "image/png",
            "image/jpeg"
    })
    public Response getFotoPengumuman(
            @QueryParam("id") String id) {
        try { // direktori saat jar dijalankan
            AnnouncementEntity announcement = AnnouncementEntity.findById(id);
            InputStream imageStream = Files.newInputStream(Paths.get(announcement.fotoPath));
            return Response.ok(imageStream).build();
        } catch (Exception e) {
            throw new InternalError("Cant get file");
        }

    }
}
