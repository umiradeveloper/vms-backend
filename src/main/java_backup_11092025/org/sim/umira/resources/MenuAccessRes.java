package org.sim.umira.resources;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CreateMenuAccessDto;
import org.sim.umira.entities.AppsEntity;
import org.sim.umira.entities.MenuAccessEntity;
import org.sim.umira.entities.MenuAccessMobileEntity;
import org.sim.umira.entities.MenuEntity;
import org.sim.umira.entities.MenuMobileEntity;
import org.sim.umira.entities.RoleEntity;
import org.sim.umira.handlers.ResponseHandler;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/menu-access")
public class MenuAccessRes {
    @POST
    @Path("/create-menu-access")
    @Transactional
    public Response createMenuAccess(@Valid @RequestBody CreateMenuAccessDto cma){
        RoleEntity re = RoleEntity.findById(cma.id_role);
        AppsEntity ae = AppsEntity.find("code_apps = ?1", cma.code_apps).firstResult();
        MenuEntity me = MenuEntity.find("code_menu = ?1", cma.code_menu).firstResult();
        
        MenuAccessEntity mae = new MenuAccessEntity();
        mae.apps = ae;
        mae.role = re;
        mae.menu = me;
        mae.persist();
        return Response.ok().entity(ResponseHandler.ok("Create menu access success", re)).build();
    }


    @POST
    @Path("/create-menu-access-mobile")
    @Transactional
    public Response createMenuAccessMobile(@Valid @RequestBody CreateMenuAccessDto cma){
        RoleEntity re = RoleEntity.findById(cma.id_role);
        AppsEntity ae = AppsEntity.find("code_apps = ?1", cma.code_apps).firstResult();
        MenuMobileEntity me = MenuMobileEntity.find("kode_menu = ?1", cma.code_menu).firstResult();
        
        MenuAccessMobileEntity mae = new MenuAccessMobileEntity();
        mae.apps = ae;
        mae.role = re;
        mae.menu = me;
        mae.persist();
        return Response.ok().entity(ResponseHandler.ok("Create menu access success", re)).build();
    }
}
