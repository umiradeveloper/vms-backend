package org.sim.umira.dtos.CostControl;

import java.time.LocalDate;

public class ReportProyekDto {
    public String namaProject;
    public String client;
    public LocalDate tanggalMulai;
    public LocalDate tanggalSelesai;
    public Integer progress;
    // public List<TaskDto> tasks;

    public ReportProyekDto(
            String namaProject,
            String client,
            LocalDate tanggalMulai,
            LocalDate tanggalSelesai,
            Integer progress
            // List<TaskDto> tasks
    ) {
        this.namaProject = namaProject;
        this.client = client;
        this.tanggalMulai = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.progress = progress;
        // this.tasks = tasks;
    }

    
}


