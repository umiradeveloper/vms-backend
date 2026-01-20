package org.sim.umira.resources;


import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.CreateAppsDto;
import org.sim.umira.entities.AppsEntity;
import org.sim.umira.handlers.ResponseHandler;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/apps")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppsRes {
    @POST
    @Path("/create-apps")
    @Transactional
    public Response createApps(@Valid @RequestBody CreateAppsDto cat){
        AppsEntity ae = new AppsEntity();
        ae.apps_name = cat.name_apps;
        ae.code_apps = cat.code_apps;
        ae.persist();
        return Response.ok().entity(ResponseHandler.ok("Create apps successfull", null)).build();

    }
}
