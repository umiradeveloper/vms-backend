package org.sim.umira.resources.M;

import org.sim.umira.dtos.M.UpdateUserMDto;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/mobile")
@Secured
public class MRes {

    @POST
    @Path("/updateUser")
    @Transactional
    public Response updateUserM(@Valid UpdateUserMDto update){
        try {
            UserEntity ue = UserEntity.findById(update.id_user);
            ue.nama = update.nama;
            ue.username = update.username;
            ue.email = update.email;
            ue.no_hp = update.no_hp;
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
}
