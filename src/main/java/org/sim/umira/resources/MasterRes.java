package org.sim.umira.resources;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.sim.umira.entities.BranchEntity;
import org.sim.umira.entities.RoleEntity;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.handlers.ResponseHandler;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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
        List<RoleEntity> be = RoleEntity.find("kode_role != ?1 AND kode_role != ?2", "99", "01").list();
        return Response.ok().entity(ResponseHandler.ok("Inquiry Role Success", be)).build();
    }
    
    @GET
    @Path("/get-week-by-project")
    public Response getWeekByProject(
        @QueryParam("id_project") String id_project
    ){
        try {
            ProyekEntity pe = ProyekEntity.findById(id_project);
            List<WeekData> result = new ArrayList<>();
            LocalDate start = pe.tanggal_awal_kontrak;
            LocalDate end = pe.tanggal_akhir_kontrak;
            LocalDate currentStart = start;
            int weekNumber = 1;

        while (!currentStart.isAfter(end)) {
            LocalDate currentEnd = currentStart.plusDays(6);
            if (currentEnd.isAfter(end)) {
                currentEnd = end;
            }

            result.add(new WeekData(weekNumber, currentStart, currentEnd));

            weekNumber++;
            currentStart = currentStart.plusDays(7);
        }

            return Response.ok().entity(ResponseHandler.ok("Inquiry Role Success", result.stream().sorted(Comparator.comparing(WeekData::week)).toList())).build();
        } catch (Exception e) {
            throw new InternalError(e.getMessage());
            // TODO: handle exception
        }
        
    }

    public record WeekData(int week, LocalDate startDate, LocalDate endDate) {}

}
