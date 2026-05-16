package org.sim.umira.resources.CostControl;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.hibernate.Session;
import org.sim.umira.dtos.CostControl.CreateCostCodeDto;
import org.sim.umira.dtos.CostControl.CreateSingleCostCodeDto;
import org.sim.umira.dtos.CostControl.ProjectCostCodeDto;
import org.sim.umira.dtos.CostControl.RapaDto;
import org.sim.umira.dtos.CostControl.ResponseCostCodeDto;
import org.sim.umira.entities.CostControl.CostCodeEntity;
import org.sim.umira.entities.CostControl.KategoriEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.CostControl.SatuanEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/CostControl/Cost-Code")
@Secured
public class CostCodeRes {
    @Inject
    EntityManager em;

    @POST
    @Path("/create-cost-code")
    @Transactional
    public Response createCostCode(
            @Valid @RequestBody CreateCostCodeDto create) {


        Set<String> unique = new HashSet<>();
        Set<String> duplicates = new HashSet<>();

        for (String item : create.kode) {
            if (!unique.add(item.trim())) {
                duplicates.add(item.trim());
            }
        }
        if(duplicates.size() > 0){
            String arrDuplicate = String.join(",", duplicates);
            throw new BadRequestException("Duplikat Kode "+arrDuplicate+"");
        }

        try {

            Session session = em.unwrap(Session.class);

            int batchSize = 100;



            /*
             * PRELOAD KATEGORI
             */
            Map<String, KategoriEntity> kategoriMap = KategoriEntity.<KategoriEntity>listAll()
                    .stream()
                    .collect(Collectors.toMap(
                            k -> k.kode_kategori.trim(),
                            k -> k));

            /*
             * PRELOAD COST CODE
             */
            Map<String, CostCodeEntity> costCodeMap = CostCodeEntity.<CostCodeEntity>listAll()
                    .stream()
                    .collect(Collectors.toMap(
                            c -> c.cost_code,
                            c -> c));

            session.doWork(connection -> {

                String sql = """
                            INSERT INTO cc_cost_code (
                                id_cost_code,
                                cost_code,
                                nama,
                                klasifikasi,
                                satuan,
                                spesifikasi,
                                kode_jenis,
                                jenis,
                                kode_kategori
                            )
                            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;

                try (PreparedStatement ps = connection.prepareStatement(sql)) {

                    for (int i = 0; i < create.kode.size(); i++) {

                        String kode = create.kode.get(i);

                        KategoriEntity kategori = kategoriMap.get(
                                create.kode_kategori
                                        .get(i)
                                        .trim());

                        CostCodeEntity costCode = costCodeMap.get(kode);

                        /*
                         * UPDATE
                         */
                        if (costCode != null) {

                            costCode.cost_code = kode;
                            costCode.nama = create.nama.get(i);
                            costCode.jenis = create.jenis.get(i);
                            costCode.kategori = kategori;
                            costCode.kode_jenis = create.kode_jenis.get(i);
                            costCode.klasifikasi = create.klasifikasi.get(i);
                            costCode.satuan = create.satuan.get(i);
                            costCode.spesifikasi = create.spesifikasi.get(i);

                        }

                        /*
                         * INSERT
                         */
                        else {

                            ps.setString(
                                    1,
                                    UUID.randomUUID().toString());

                            ps.setString(2, kode);
                            ps.setString(3, create.nama.get(i));
                            ps.setString(4, create.klasifikasi.get(i));
                            ps.setString(5, create.satuan.get(i));
                            ps.setString(6, create.spesifikasi.get(i));
                            ps.setString(7, create.kode_jenis.get(i));
                            ps.setString(8, create.jenis.get(i));
                            ps.setString(9, kategori.id_kategori);

                            ps.addBatch();
                        }

                        /*
                         * FLUSH BATCH
                         */
                        if (i > 0 && i % batchSize == 0) {

                            ps.executeBatch();

                            session.flush();
                            session.clear();
                        }
                    }

                    /*
                     * EXECUTE SISA
                     */
                    ps.executeBatch();
                }
            });

            return Response.ok()
                    .entity(
                            ResponseHandler.ok(
                                    "Create Cost Code Berhasil",
                                    null))
                    .build();

        } catch (Exception e) {

            throw new InternalError(e.getMessage());
        }

    }
    public static boolean hasDuplicate(List<String> list) {
        return list.size() != new HashSet<>(list).size();
    }

    @GET
    @Path("/get-cost-code")
    @Transactional
    public Response getCostCode() {
        try {
            // List<CostCodeEntity> costCode = CostCodeEntity.findAll().list();
            List<CostCodeEntity> costCode = CostCodeEntity.find(
                    "SELECT c FROM CostCodeEntity c JOIN FETCH c.kategori").list();
            List<ResponseCostCodeDto> response = new ArrayList<>();
            for (CostCodeEntity costCodeE : costCode) {
                List<ProjectCostCodeDto> proyek = ProyekEntity.find(
                        "SELECT p.nama_proyek as nama_proyek, SUM(b.volume_bk) as volume, SUM(b.harga_total) as harga_total FROM ProyekEntity p JOIN p.bk b JOIN b.rapa r JOIN r.costCodeRapa c WHERE c.cost_code = ?1 GROUP BY p.nama_proyek",
                        costCodeE.cost_code).project(ProjectCostCodeDto.class).list();
                response.add(new ResponseCostCodeDto(costCodeE.id_cost_code, costCodeE.cost_code, costCodeE.nama,
                        costCodeE.klasifikasi, costCodeE.spesifikasi, costCodeE.satuan, costCodeE.kode_jenis,
                        costCodeE.kategori.nama_kategori, costCodeE.kategori.kode_kategori, costCodeE.jenis, proyek));
            }
            return Response.ok().entity(ResponseHandler.ok("get Cost Code Berhasil", response)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

    @GET
    @Path("/get-cost-code-by-proyek")
    @Transactional
    public Response getCostCodeByProyek(@QueryParam("cost_code") String costCode) {
        try {

            List<ProjectCostCodeDto> proyek = ProyekEntity.find(
                    "SELECT p.nama_proyek as nama_proyek, SUM(b.volume_bk) as volume, SUM(b.harga_total) as harga_total FROM ProyekEntity p JOIN p.bk b JOIN b.rapa r JOIN r.costCodeRapa c WHERE c.cost_code = ?1 GROUP BY p.nama_proyek",
                    costCode).project(ProjectCostCodeDto.class).list();
            return Response.ok().entity(ResponseHandler.ok("get Cost Code Proyek Berhasil", proyek)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

    @POST
    @Path("/create-single-cost-code")
    @Transactional
    public Response createSingleCostCode(@Valid CreateSingleCostCodeDto create) {

        KategoriEntity kategori_check = KategoriEntity.find("kode_kategori = ?1", create.kode_kategori.trim())
                .firstResult();
        if (kategori_check == null) {
            throw new BadRequestException("kode kategori dengan " + create.kode_kategori + " tidak terdaftar");
        }
        System.out.println(create.kode);
        try {
            CostCodeEntity createCostCode = new CostCodeEntity();
            createCostCode.cost_code = create.kode;
            createCostCode.nama = create.nama;
            createCostCode.jenis = create.jenis;
            createCostCode.klasifikasi = create.klasifikasi;
            createCostCode.kode_jenis = create.kode_jenis;
            createCostCode.satuan = create.satuan;
            createCostCode.spesifikasi = create.spesifikasi;
            createCostCode.kategori = kategori_check;
            createCostCode.persist();
            return Response.ok().entity(ResponseHandler.ok("create Cost Code Berhasil", createCostCode)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

    @POST
    @Path("/update-single-cost-code")
    @Transactional
    public Response updateSingleCostCode(@Valid CreateSingleCostCodeDto create) {

        KategoriEntity kategori_check = KategoriEntity.find("kode_kategori = ?1", create.kode_kategori.trim())
                .firstResult();
        if (kategori_check == null) {
            throw new BadRequestException("kode kategori dengan " + create.kode_kategori + " tidak terdaftar");
        }
        // System.out.println(create.kode);
        try {
            CostCodeEntity createCostCode = CostCodeEntity.findById(create.id_cost_code);
            createCostCode.cost_code = create.kode;
            createCostCode.nama = create.nama;
            createCostCode.jenis = create.jenis;
            createCostCode.klasifikasi = create.klasifikasi;
            createCostCode.kode_jenis = create.kode_jenis;
            createCostCode.satuan = create.satuan;
            createCostCode.spesifikasi = create.spesifikasi;
            createCostCode.kategori = kategori_check;
            return Response.ok().entity(ResponseHandler.ok("update Cost Code Berhasil", createCostCode)).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
        }
    }

}
