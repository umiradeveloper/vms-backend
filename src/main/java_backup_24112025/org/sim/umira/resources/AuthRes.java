package org.sim.umira.resources;






import java.util.List;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.configs.ConfigHttpService;
import org.sim.umira.configs.ConfigService;
import org.sim.umira.dtos.LoginDto;
import org.sim.umira.dtos.RegisterDto;
import org.sim.umira.dtos.ResponseLoginDto;
import org.sim.umira.entities.BranchEntity;
import org.sim.umira.entities.MenuAccessEntity;
import org.sim.umira.entities.MenuAccessMobileEntity;
import org.sim.umira.entities.RoleEntity;
import org.sim.umira.entities.UserEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.JwtService;

import io.quarkus.elytron.security.common.BcryptUtil;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthRes {
    @Inject
    JwtService js;

    @Inject
    ConfigHttpService httpService;

    @Inject
    ConfigService configService;
   

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

     @POST
    @Path("/register-staff")
    @PermitAll
    @Transactional
    public Response registerStaff(@Valid @RequestBody RegisterDto registerDto){
        List<UserEntity> userCheck = UserEntity.find("(email = ?1 OR no_hp =?1)", registerDto.email).list();
        if(userCheck.size() > 0){
            // return Response.ok().entity(ResponseHandler.ok("User exist", null)).build();
            // throw new ForbiddenException("User exist");
            throw new BadRequestException("User Exist");
        }
        RoleEntity re = RoleEntity.find("kode_role = ?1", registerDto.kode_role).firstResult();
        BranchEntity be = BranchEntity.find("kode_branch = ?1", registerDto.kode_branch).firstResult();
        UserEntity ue = new UserEntity();
        ue.email = registerDto.email;
        ue.password = BcryptUtil.bcryptHash(registerDto.password);
        ue.nama = registerDto.nama_perusahaan;
        ue.no_hp = registerDto.no_hp;
        ue.isApproval = 0;
        ue.role = re;
        ue.branch = be;
        ue.persist();

        httpService.SendWhatsapp(registerDto.no_hp, "Terima Kasih telah register \n \n tunggu approval dari administrator");
       httpService.sendEmail(ue.email, "Terima kasih telah register", "Registrasi");
       
        return Response.ok().entity(ResponseHandler.ok("success register", null)).build();
    }

    @POST
    @Path("/login-mobile")
    @PermitAll
    public Response loginMobile(@Valid @RequestBody LoginDto loginDto) {
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
        List<MenuAccessMobileEntity> mae = MenuAccessMobileEntity.find("role = ?1 order by menu.kode_menu asc", user.role).list();

        // System.out.println(mae);
        try {
           
            return Response.ok().entity(ResponseHandler.ok("Success", new ResponseLoginDto(token, user, mae))).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok().entity(ResponseHandler.error(e.getMessage())).build();
        }
    }


    @POST
    @Path("/register-vendor")
    @PermitAll
    @Transactional
    public Response register(@Valid @RequestBody RegisterDto registerDto){
        List<UserEntity> userCheck = UserEntity.find("(email = ?1 OR no_hp =?1)", registerDto.email).list();
        if(userCheck.size() > 0){
            // return Response.ok().entity(ResponseHandler.ok("User exist", null)).build();
            // throw new ForbiddenException("User exist");
            throw new BadRequestException("User Exist");
        }
        RoleEntity re = RoleEntity.find("kode_role = ?1", "01").firstResult();
        BranchEntity be = BranchEntity.find("kode_branch = ?1", registerDto.kode_branch).firstResult();
        UserEntity ue = new UserEntity();
        ue.email = registerDto.email;
        ue.password = BcryptUtil.bcryptHash(registerDto.password);
        ue.nama = registerDto.nama_perusahaan;
        ue.no_hp = registerDto.no_hp;
        ue.isApproval = 0;
        ue.role = re;
        ue.branch = be;
        ue.persist();
	try{
	httpService.SendWhatsapp(registerDto.no_hp, "Terima Kasih telah register \n tunggu approval dari administrator");
	}catch(Exception e){
	 e.printStackTrace();
	}
        //httpService.SendWhatsapp(registerDto.no_hp, "Terima Kasih telah register \n tunggu approval dari administrator");
        httpService.sendEmail(ue.email, "Terima kasih telah register \n tunggu approval dari administrator", "Registrasi");
       
        return Response.ok().entity(ResponseHandler.ok("success register", null)).build();
    }
   
}
