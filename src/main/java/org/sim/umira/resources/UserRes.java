package org.sim.umira.resources;






import java.time.LocalDateTime;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.configs.ConfigHttpService;
import org.sim.umira.dtos.CreateUserDto;
import org.sim.umira.entities.BranchEntity;
import org.sim.umira.entities.RoleEntity;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/users")
@Secured
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserRes {
    
    @Inject
    ConfigHttpService httpService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all")
    public Response getUsers(@Context SecurityContext ctx) {
        List<UserEntity> ue = UserEntity.listAll();
        return Response.ok().entity(ResponseHandler.ok("Success", ue)).build();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all/vendor")
    public Response getUsersVendor(@Context SecurityContext ctx) {
        RoleEntity re = RoleEntity.find("kode_role = ?1", "01").firstResult();
        List<UserEntity> ue = UserEntity.find("role = ?1", re).list();
        return Response.ok().entity(ResponseHandler.ok("Success", ue)).build();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/all/staff")
    public Response getUsersStaf(@Context SecurityContext ctx) {
        RoleEntity re = RoleEntity.find("kode_role != ?1", "01").firstResult();
        List<UserEntity> ue = UserEntity.find("role = ?1", re).list();
        return Response.ok().entity(ResponseHandler.ok("Success", ue)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/insert")
    @Transactional
    public Response insertUsers(@Valid @RequestBody CreateUserDto user){
        RoleEntity re = RoleEntity.find("kode_role = ?1", user.kode_role).firstResult();
        BranchEntity be = BranchEntity.find("kode_branch = ?1", user.kode_branch).firstResult();
        UserEntity ue = new UserEntity();
        // System.out.println(ue.nama);
        ue.nama = user.nama_perusahaan;
        ue.role = re;
        ue.branch = be;
        ue.email = user.email;
        ue.username = user.username;
        ue.password = BcryptUtil.bcryptHash(user.password);
        ue.approvedBy = user.approvedBy;
        ue.isApproval = user.isApproval;
        ue.approvedAt = LocalDateTime.now();
        ue.persist();
        return Response.ok().entity(ResponseHandler.ok("User Berhasil Di update", null)).build();
    }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    @Transactional
    public Response updateUsers(@QueryParam("id") String id, @Valid @RequestBody CreateUserDto user){
        RoleEntity re = RoleEntity.find("kode_role = ?1", user.kode_role).firstResult();
        BranchEntity be = BranchEntity.find("kode_branch = ?1", user.kode_branch).firstResult();
        UserEntity ue = UserEntity.findById(id);
        System.out.println(user.password);
        ue.nama = user.nama_perusahaan;
        ue.role = re;
        ue.branch = be;
        ue.email = user.email;
        if(user.password != null){
            ue.password = BcryptUtil.bcryptHash(user.password);
        }
        ue.approvedBy = user.approvedBy;
        ue.isApproval = user.isApproval;
        ue.approvedAt = LocalDateTime.now();
        ue.persist();
        return Response.ok().entity(ResponseHandler.ok("User Berhasil Di update", null)).build();
    }
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/deleted")
    @Transactional
    public Response deleteUsers(@QueryParam("id") String id){
        try {
            Boolean ue = UserEntity.deleteById(id);
            if(ue){
                return Response.ok().entity(ResponseHandler.ok("Deleted user data berhasil", null)).build();
            }else{
                // return Response.ok().entity(ResponseHandler.ok("Deleted user data berhasil", null)).build();
                throw new NotFoundException("user not found");
            }
            
        } catch (Exception e) {
            // TODO: handle exception
            throw new InternalError(e.getMessage());
        }
        
    }
    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update-approval")
    @Transactional
    public Response ApprovalUsers(@QueryParam("id") String id, @Valid @RequestBody CreateUserDto user, @Context SecurityContext ctx){
        try {

            UserEntity ue = UserEntity.findById(id);
            UserEntity checkUser = UserEntity.find("email = ?1", ctx.getUserPrincipal().getName()).firstResult();
            ue.approvedBy = checkUser.username;
            ue.isApproval = user.isApproval;
            if(user.isApproval == 2){
                ue.catatan = user.catatan;
                String response  = httpService.SendWhatsapp(ue.no_hp, "Registrasi akun Anda Di Tolak \n \ndengan alasan sebagai berikut: \n \n"+user.catatan+" \n \nNo Reply");
                String responseEmail = httpService.sendEmail(ue.email, "Registrasi Di Tolak", "Registrasi");
                System.out.println(response);
                System.out.println(responseEmail);
            }
            if(user.isApproval == 1){
                httpService.SendWhatsapp(ue.no_hp, "Registrasi akun Anda Di setujui \n \nSilahkan login untuk pendaftaran vendor rekanan \n \nNo Reply");
                // String responseEmail = httpService.sendEmail(ue.email, "Registrasi akun di setujui", "Registrasi");
                // System.out.println(response);
            }
            ue.approvedAt = LocalDateTime.now();
            
            return Response.ok().entity(ResponseHandler.ok("User Berhasil Di update", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            // return Response.ok().entity(ResponseHandler.error("User B Di update")).build();
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
        
    }

}
