package org.sim.umira.dtos.HumanResources;

import java.time.LocalDate;
import java.util.List;

public class PengajuanOvertimeDto {
    public String id_employee;

    public LocalDate tanggal;

    public String jam_mulai;

    public String jam_selesai;

    public String alasan;

    public String dokumen;

    public List<String> id_employee_approval;

    public List<String> level_approval;

    public List<Integer> urutan;
}
