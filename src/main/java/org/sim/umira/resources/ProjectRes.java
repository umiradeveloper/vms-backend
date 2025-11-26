package org.sim.umira.resources;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.CreateProjectDto;
import org.sim.umira.dtos.LoginDto;
import org.sim.umira.dtos.ResponseLoginDto;
import org.sim.umira.entities.ProjectDoneEntity;
import org.sim.umira.entities.ProjectOnGoingEntity;
import org.sim.umira.entities.VmsVendorDetailEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.annotation.security.PermitAll;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/project")

public class ProjectRes {

    @POST
    @Path("/project-done")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Secured
     public Response createProjectDone(@MultipartForm CreateProjectDto create) { 
        try {
            ArrayList<String> dokumen = new ArrayList<>();
            create.files.forEach(file -> {
                System.out.println(file.fileName());
                String ext = file.fileName().substring(file.fileName().lastIndexOf("."));
                String randomFileName = UUID.randomUUID().toString() + ext;
                try {
                    java.nio.file.Path target = java.nio.file.Path.of("uploads/projectdone", randomFileName);
                    Files.createDirectories(target.getParent());
                    Files.copy(file.uploadedFile(), target, StandardCopyOption.REPLACE_EXISTING);
                    dokumen.add(target.toString());
                    // VmsVendorDetailEntity ve = new VmsVendorDetailEntity();
                    // ve.vendor = vm;
                    // // ve.dokumen = vd;
                    // ve.nama_dokumen = vd.nama_dokumen;
                    // ve.url_dokumen = target.toString();
                    // ve.persist();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            ProjectDoneEntity proj = new ProjectDoneEntity();
            proj.nama_project = create.nama_project;
            proj.deskripsi_project = create.deskripsi_project;
            proj.url_gambar = String.join(",", dokumen);
            proj.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Project Done", null)).build();
        } catch (Exception e) {
            return Response.ok().entity(ResponseHandler.ok(e.getMessage(), null)).build();
            // TODO: handle exception
        }
        
     }

    @GET
    @Path("/project-done")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
     public Response getProjectDone() { 
        try {
           List<ProjectDoneEntity> proj = ProjectDoneEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Create Project Done", proj)).build();
        } catch (Exception e) {
            return Response.ok().entity(ResponseHandler.ok(e.getMessage(), null)).build();
            // TODO: handle exception
        }
        
     }

    @DELETE
    @Path("/delete-project-done")
    @Secured
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
     public Response deleteProjectDone(@QueryParam("id") String id) { 
        try {
        //    List<ProjectDoneEntity> proj = ProjectDoneEntity.listAll();
            Long deleted = ProjectDoneEntity.delete("id_project_done = ?1", id);
            return Response.ok().entity(ResponseHandler.ok("Delete Project Done", deleted)).build();
        } catch (Exception e) {
            return Response.ok().entity(ResponseHandler.ok(e.getMessage(), null)).build();
            // TODO: handle exception
        }
        
     }

    @GET
    @Path("/dokumentasi")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("image/jpeg")
    public Response getFile(
        @QueryParam("url") String url
    ){
        // VmsVendorDetailEntity vme = VmsVendorDetailEntity.findById(id);
        // System.out.println(vme.url_dokumen);
        try {
            // String url = "uploads/"+vme.url_dokumen;
            InputStream imageStream = Files.newInputStream(Paths.get(url));
            return Response.ok(imageStream).build();
        } catch (Exception e) {
        throw new InternalError("Cant get file");
        }
        
    }
     @GET
    @Path("/foto")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("image/jpeg")
    public Response getDokumen(
        @QueryParam("url") String url
    ){
        // VmsVendorDetailEntity vme = VmsVendorDetailEntity.findById(id);
        // System.out.println(vme.url_dokumen);
        try {
            // String url = "uploads/"+vme.url_dokumen;
            InputStream imageStream = Files.newInputStream(Paths.get(url));
            return Response.ok(imageStream).build();
        } catch (Exception e) {
        throw new InternalError("Cant get file");
        }
        
    }

    @POST
    @Path("/project-on-going")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
     public Response createProjectOnGoing(@MultipartForm CreateProjectDto create) { 
        try {
            ArrayList<String> dokumen = new ArrayList<>();
            create.files.forEach(file -> {
                System.out.println(file.fileName());
                String ext = file.fileName().substring(file.fileName().lastIndexOf("."));
                String randomFileName = UUID.randomUUID().toString() + ext;
                try {
                    java.nio.file.Path target = java.nio.file.Path.of("uploads/projectongoing", randomFileName);
                    Files.createDirectories(target.getParent());
                    Files.copy(file.uploadedFile(), target, StandardCopyOption.REPLACE_EXISTING);
                    dokumen.add(target.toString());
                    // VmsVendorDetailEntity ve = new VmsVendorDetailEntity();
                    // ve.vendor = vm;
                    // // ve.dokumen = vd;
                    // ve.nama_dokumen = vd.nama_dokumen;
                    // ve.url_dokumen = target.toString();
                    // ve.persist();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            ProjectOnGoingEntity proj = new ProjectOnGoingEntity();
            proj.nama_project = create.nama_project;
            proj.deskripsi_project = create.deskripsi_project;
            proj.url_gambar = String.join(",", dokumen);
            proj.persist();
            return Response.ok().entity(ResponseHandler.ok("Create Project On Going", null)).build();
        } catch (Exception e) {
            return Response.ok().entity(ResponseHandler.ok(e.getMessage(), null)).build();
            // TODO: handle exception
        }
        
     }
     @GET
    @Path("/project-on-going")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
     public Response getProjectOnGoing() { 
        try {
           List<ProjectOnGoingEntity> proj = ProjectOnGoingEntity.listAll();
            return Response.ok().entity(ResponseHandler.ok("Get Project On Going", proj)).build();
        } catch (Exception e) {
            return Response.ok().entity(ResponseHandler.ok(e.getMessage(), null)).build();
            // TODO: handle exception
        }
        
     }

     @GET
     @Path("/delete-project-ongoing")
     @Secured
    //  @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
     public Response deleteProjectOnGoing(@QueryParam("id") String id) { 
        try {
        //    List<ProjectDoneEntity> proj = ProjectDoneEntity.listAll();
            Long deleted = ProjectOnGoingEntity.delete("id_project_done = ?1", id);
            return Response.ok().entity(ResponseHandler.ok("Delete Project Done", deleted)).build();
        } catch (Exception e) {
            return Response.ok().entity(ResponseHandler.ok(e.getMessage(), null)).build();
            // TODO: handle exception
        }
        
     }
}
