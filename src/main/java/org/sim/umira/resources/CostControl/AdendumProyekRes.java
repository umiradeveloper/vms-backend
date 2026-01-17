package org.sim.umira.resources.CostControl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.hibernate.Session;
import org.jboss.resteasy.reactive.MultipartForm;
import org.sim.umira.dtos.CostControl.CreateAdendumProyekDto;
import org.sim.umira.dtos.CostControl.CreateAdendumProyekNewDto;
import org.sim.umira.dtos.CostControl.CreateKategoriDto;
import org.sim.umira.entities.CostControl.AdendumProyekEntity;
import org.sim.umira.entities.CostControl.KategoriEntity;
import org.sim.umira.entities.CostControl.PendapatanUsahaEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/CostControl/AdendumProyek")
@Secured
public class AdendumProyekRes {

    @Inject
    EntityManager em;

    private static final java.nio.file.Path UPLOAD_DIR = java.nio.file.Path.of("uploads/dokumen-adendum");

    @POST
    @Path("/create-adendum-bulk")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createAdendumBulk(
        @Valid @MultipartForm CreateAdendumProyekDto create
    ){
        try {
            ProyekEntity proyek = ProyekEntity.find("id_proyek = ?1", create.id_proyek).firstResult();
            Session session = em.unwrap(Session.class);
            int batch = create.dokumen_adendum.size();
            Files.createDirectories(UPLOAD_DIR);    
            for (int i = 0; i < create.nomor_adendum.size(); i++) {
                String uuid = java.util.UUID.randomUUID().toString();
                final int idx = i;
                String fileName = java.util.UUID.randomUUID() + "-" + create.dokumen_adendum.get(idx).fileName();
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                    create.dokumen_adendum.get(idx).uploadedFile(),
                    target,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                session.doWork(connection -> {
                        try (PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO cc_adendum_proyek (id_adendum, id_proyek, nomor_adendum, dokumen_adendum, kerja_tambah, kerja_kurang) VALUES (?, ?, ?, ?, ?, ?)"
                        )) {
                            ps.setString(1, uuid);
                            ps.setString(2, proyek.id_proyek);
                            ps.setString(3, create.nomor_adendum.get(idx));
                            ps.setString(4, target.toString());
                            ps.setBigDecimal(5, new BigDecimal(create.kerja_tambah.get(idx)));
                            ps.setBigDecimal(6, new BigDecimal(create.kerja_kurang.get(idx)));
                            ps.addBatch();
                            ps.executeBatch();
                        }

                    });
                    if (i % batch == 0) {
                        session.flush();
                        session.clear();
                    }
            }
            return Response.ok().entity(ResponseHandler.ok("Create Kategori Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }



    @POST
    @Path("/update-adendum-bulk")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateAdendumBulk(
        @Valid @MultipartForm CreateAdendumProyekDto create
    ){
        try {
            ProyekEntity proyek = ProyekEntity.find("id_proyek = ?1", create.id_proyek).firstResult();
            List<AdendumProyekEntity> adendumGet = AdendumProyekEntity.find("proyek = ?1", proyek).list();
            for (AdendumProyekEntity adendumProyekEntity : adendumGet) {
                java.nio.file.Path target = UPLOAD_DIR.resolve(adendumProyekEntity.dokumen_adendum);
                
                boolean deleteAdendum = AdendumProyekEntity.deleteById(adendumProyekEntity.id_adendum);
                if(deleteAdendum){
                    Files.deleteIfExists(target);
                }   
            }

            
            Session session = em.unwrap(Session.class);
            int batch = create.dokumen_adendum.size();
            Files.createDirectories(UPLOAD_DIR);
            for (int i = 0; i < create.nomor_adendum.size(); i++) {
                String uuid = java.util.UUID.randomUUID().toString();
                final int idx = i;
                String fileName = java.util.UUID.randomUUID() + "-" + create.dokumen_adendum.get(idx).fileName();
                java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
                Files.copy(
                    create.dokumen_adendum.get(idx).uploadedFile(),
                    target,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                session.doWork(connection -> {
                        try (PreparedStatement ps = connection.prepareStatement(
                            "INSERT INTO cc_adendum_proyek (id_adendum, id_proyek, nomor_adendum, dokumen_adendum, kerja_tambah, kerja_kurang) VALUES (?, ?, ?, ?, ?, ?)"
                        )) {
                            ps.setString(1, uuid);
                            ps.setString(2, proyek.id_proyek);
                            ps.setString(3, create.nomor_adendum.get(idx));
                            ps.setString(4, target.toString());
                            ps.setBigDecimal(5, new BigDecimal(create.kerja_tambah.get(idx)));
                            ps.setBigDecimal(6, new BigDecimal(create.kerja_kurang.get(idx)));
                            ps.addBatch();
                            ps.executeBatch();
                        }

                    });
                    if (i % batch == 0) {
                        session.flush();
                        session.clear();
                    }
            }
            return Response.ok().entity(ResponseHandler.ok("Create Kategori Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
        
    }

    @POST
    @Path("/create-adendum")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createAdendum(
        @Valid @MultipartForm CreateAdendumProyekNewDto create
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(create.id_proyek);
            AdendumProyekEntity cekAdendum = AdendumProyekEntity.find("proyek = ?1 and nomor_adendum = ?2", proyek, create.nomor_adendum).firstResult();
            if(cekAdendum != null){
                throw new BadRequestException("nomor adendum exist");
            }
            AdendumProyekEntity adendum = new AdendumProyekEntity();
            adendum.proyek = proyek;
            adendum.kerja_tambah = create.kerja_tambah;
            adendum.kerja_kurang = create.kerja_kurang;
            adendum.nomor_adendum = create.nomor_adendum;
            if (!Files.exists(UPLOAD_DIR)) {
                Files.createDirectories(UPLOAD_DIR);
            }
           
            String ext = create.dokumen_adendum.fileName().substring(create.dokumen_adendum.fileName().lastIndexOf("."));
            String fileName = java.util.UUID.randomUUID() + ext;
            java.nio.file.Path target = UPLOAD_DIR.resolve(fileName);
            Files.copy(create.dokumen_adendum.uploadedFile(),target,java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            adendum.dokumen_adendum = target.toString();
            adendum.persist();
            return Response.ok().entity(ResponseHandler.ok("Update Pu Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
    }

    @GET
    @Path("/get-adendum")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAdendum(
        @Valid @QueryParam("id_proyek") String id_proyek
    ){
        try {
            ProyekEntity proyek = ProyekEntity.findById(id_proyek);
            List<AdendumProyekEntity> adendum = AdendumProyekEntity.find("proyek = ?1 ", proyek).list();
            return Response.ok().entity(ResponseHandler.ok("Inquiry adendum by proyek Berhasil", adendum)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
        
    }
    
    @DELETE
    @Path("/delete-adendum")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response deleteAdendum(
        @Valid @QueryParam("id_adendum") String id_adendum
    ){
        try {
            Boolean adendum = AdendumProyekEntity.deleteById(id_adendum);
            return Response.ok().entity(ResponseHandler.ok("delete adendum Berhasil", null)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
        
    }

    @GET
    @Path("/dokumen-file")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("application/pdf")
    public Response getFile(
        @QueryParam("id") String id
    ){  
        try {  // direktori saat jar dijalankan
            AdendumProyekEntity adendum = AdendumProyekEntity.findById(id);
            InputStream imageStream = Files.newInputStream(Paths.get(adendum.dokumen_adendum));
            return Response.ok(imageStream).build();
        } catch (Exception e) {
           throw new InternalError("Cant get file");
        }
        
    }


}
