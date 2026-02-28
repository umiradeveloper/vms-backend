package org.sim.umira.resources.M;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.M.UpdateUserMDto;
import org.sim.umira.entities.AnnouncementEntity;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/mobile")
public class MRes {

    @POST
    @Path("/updateUser")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    @Secured
    public Response updateUserM(@Valid @MultipartForm UpdateUserMDto update){
        try {
            UserEntity ue = UserEntity.findById(update.id_user);
            ue.nama = update.nama;
            ue.username = update.username;
            ue.email = update.email;
            ue.no_hp = update.no_hp;
            if(update.foto_profile != null){
                String uploadFoto = "uploads/foto_profile/"+ue.id_user+"/";
                 File dirFoto = new File(uploadFoto);
                if (!dirFoto.exists())
                    dirFoto.mkdirs();
                String ext = update.foto_profile.fileName().substring(update.foto_profile.fileName().lastIndexOf("."));
                String randomFileName = UUID.randomUUID().toString() + ext;
                java.nio.file.Path path = java.nio.file.Path.of(uploadFoto + "/" + randomFileName);

                Files.copy(update.foto_profile.uploadedFile(),
                        path,
                        StandardCopyOption.REPLACE_EXISTING);
                ue.fotoPath = path.toString();
            }
            if(update.password != null && !update.password.trim().isEmpty()){
                ue.password = BcryptUtil.bcryptHash(update.password);
            }
            return Response.ok().entity(ResponseHandler.ok("success update user", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok(Response.Status.INTERNAL_SERVER_ERROR).entity(ResponseHandler.error("Ada Kesalahan Server")).build();
            // TODO: handle exception
        }
    }
     @GET
    @Path("/foto-profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({"image/png","image/jpeg"})
    public Response getFotoProfile(
            @QueryParam("id") String id) {
        try { // direktori saat jar dijalankan
            UserEntity user = UserEntity.findById(id);
            InputStream imageStream = Files.newInputStream(Paths.get(user.fotoPath));
            return Response.ok(imageStream).build();
        } catch (Exception e) {
            throw new InternalError("Cant get file");
        }

    }
}
