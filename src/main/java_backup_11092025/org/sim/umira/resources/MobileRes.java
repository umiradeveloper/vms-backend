package org.sim.umira.resources;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.LoginDto;
import org.sim.umira.dtos.ResponseLoginDto;
import org.sim.umira.entities.MenuAccessEntity;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.JwtService;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth/mobile")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MobileRes {

    @Inject
    JwtService js;

    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid @RequestBody LoginDto loginDto) {
        // System.out.println(loginDto.email);
        UserEntity user = UserEntity.find("(email = ?1 OR no_hp = ?1)", loginDto.email).firstResult();
        
        if(user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ResponseHandler.error("Invalid email or password")).build();
        }
        if(user.isApproval == 0 ){
            return Response.status(Response.Status.UNAUTHORIZED).entity(ResponseHandler.error("User Not Active")).build();
        }
        // System.out.println(BcryptUtil.matches(loginDto.password, user.password));
        if(!BcryptUtil.matches(loginDto.password, user.password)){
            return Response.status(Response.Status.UNAUTHORIZED).entity(ResponseHandler.error("Password not match")).build();
        }
        
        String token = js.generateToken(user.email, List.of(user.role.nama_role));
        // String id_role = user.role.id_role;
        List<MenuAccessEntity> mae = MenuAccessEntity.find("role = ?1 order by menu.code_menu asc", user.role).list();

        // System.out.println(mae);
        try {
           
            return Response.ok().entity(ResponseHandler.ok("Success", new ResponseLoginDto(token, user, mae))).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok().entity(ResponseHandler.error(e.getMessage())).build();
        }
    }
}
