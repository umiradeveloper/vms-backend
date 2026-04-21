package org.sim.umira.dtos.HumanResources;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AttendanceDto {
    public String id_employee;

    public LocalDate tanggal;

    // @JsonFormat(pattern = "HH:mm")
    public LocalTime jam_masuk;

    // @JsonFormat(pattern = "HH:mm")
    public LocalTime jam_keluar;

    public String status;

    public String keterangan;
}
