package org.sim.umira.resources;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CreateBranch;
import org.sim.umira.entities.BranchEntity;
import org.sim.umira.handlers.ResponseHandler;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/branch")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BranchRes {
    @POST
    @Path("/create-branch")
    @Transactional
    public Response createBranch(
        @Valid @RequestBody CreateBranch create
    ){
        BranchEntity be = new BranchEntity();
        be.kode_branch = create.kode_branch;
        be.nama_branch = create.nama_branch;
        be.persist();
        return Response.ok().entity(ResponseHandler.ok("Insert Branch Success", null)).build();
    }
}
