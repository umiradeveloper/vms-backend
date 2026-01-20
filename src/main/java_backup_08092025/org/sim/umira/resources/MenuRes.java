package org.sim.umira.resources;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CreateMenuDto;
import org.sim.umira.entities.AppsEntity;
import org.sim.umira.entities.MenuEntity;
import org.sim.umira.handlers.ResponseHandler;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/menu")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MenuRes {
    @POST
    @Path("/create-menu")
    @Transactional
    public Response createMenu(@Valid @RequestBody CreateMenuDto cmd){
        AppsEntity ae = AppsEntity.find("code_apps = ?1", cmd.code_apps).firstResult();
        if(ae.id_apps == null){
            // return Response.ok().entity(ResponseHandler.ok("Apps not found", null)).build();
            throw new NotFoundException("Menu Not Found");
        }
        MenuEntity me = new MenuEntity();
        me.code_menu = cmd.code_menu;
        me.nama_menu = cmd.nama_menu;
        me.menu_title = cmd.menu_title;
        me.icon_menu = cmd.icon_menu;
        me.type_menu = cmd.type_menu;
        me.selected_menu = cmd.selected_menu;
        me.dircange_menu = cmd.dircange_menu;
        me.path_menu = cmd.path_menu;
        me.apps = ae;
        me.persist();
        return Response.ok().entity(ResponseHandler.ok("create menu successfull", null)).build();
    }
}
