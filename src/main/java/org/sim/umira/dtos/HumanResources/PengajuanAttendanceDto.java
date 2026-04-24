package org.sim.umira.dtos.HumanResources;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PengajuanAttendanceDto {

    public LocalDate tanggal;

    // @JsonFormat(pattern = "HH:mm")
    public String jam_masuk;

    // @JsonFormat(pattern = "HH:mm")
    public String jam_keluar;

    public String status;

    public String keterangan;

    public List<String> id_employee_approval;

    public List<String> level_approval;

    public List<Integer> urutan;

}
