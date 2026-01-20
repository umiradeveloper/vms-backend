package org.sim.umira.resources;

import java.util.List;

import org.sim.umira.entities.BranchEntity;
import org.sim.umira.entities.RoleEntity;
import org.sim.umira.handlers.ResponseHandler;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/master")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MasterRes {
    @GET
    @Path("/get-branch")
    public Response getBranch(){
        List<BranchEntity> be = BranchEntity.listAll();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Branch Success", be)).build();
    }

    @GET
    @Path("/get-role")
    public Response getRole(){
        List<RoleEntity> be = RoleEntity.listAll();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Role Success", be)).build();
    }
    
}
