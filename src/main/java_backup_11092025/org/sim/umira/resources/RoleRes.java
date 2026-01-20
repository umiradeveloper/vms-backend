package org.sim.umira.resources;


import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CreateRoleDto;
import org.sim.umira.entities.RoleEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/role")
@Secured
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoleRes {
    @POST()
    @Path("/create-role")
    @Transactional
    public Response createUser(
        @Valid @RequestBody CreateRoleDto crd
    ){
        RoleEntity re = new RoleEntity();
        re.nama_role = crd.nama_role;
        re.kode_role = crd.kode_role;
        re.persist();
        return Response.ok().entity(ResponseHandler.ok("Insert role success", null)).build();
    }

    @GET
    @Path("/get-role")
    public Response getUser(){
        List<RoleEntity> re = RoleEntity.listAll();
        return Response.ok().entity(ResponseHandler.ok("Get role success", re)).build();
    }
}
