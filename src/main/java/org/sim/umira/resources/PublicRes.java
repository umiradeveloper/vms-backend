package org.sim.umira.resources;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.sim.umira.entities.VmsVendorDetailEntity;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/public")
public class PublicRes {
    


    @GET
    @Path("/dokumen-file-public")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/pdf")
    @PermitAll
    public Response getFilePublic(
        @QueryParam("id") String id
    ){
        VmsVendorDetailEntity vme = VmsVendorDetailEntity.findById(id);
        System.out.println(vme.url_dokumen);
        try {
            // String url = "uploads/"+vme.url_dokumen;
            InputStream imageStream = Files.newInputStream(Paths.get(vme.url_dokumen));
            return Response.ok(imageStream).header("Content-Disposition", "inline; filename=\"dokumen.pdf\"").build();
        } catch (Exception e) {
           throw new InternalError("Cant get file");
        }
        
    }
}
