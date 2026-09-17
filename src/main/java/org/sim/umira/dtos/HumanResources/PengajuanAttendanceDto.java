package org.sim.umira.dtos.HumanResources;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PengajuanAttendanceDto {

    @NotNull(message = "tanggal harus di isi")
    public LocalDate tanggal;

    // @JsonFormat(pattern = "HH:mm")
    @NotBlank(message = "jam masuk harus di isi")
    public String jam_masuk;

    // @JsonFormat(pattern = "HH:mm")
    @NotBlank(message = "jam keluar harus di isi")
    public String jam_keluar;

    @NotBlank(message = "status harus di isi")
    public String status;

    @NotBlank(message = "tanggal harus di isi")
    public String keterangan;

    @NotEmpty(message = "approval line wajib diisi")
    public List<String> id_employee_approval;

    @NotEmpty(message = "level approval wajib diisi")
    public List<String> level_approval;

    @NotEmpty(message = "urutan wajib diisi")
    public List<Integer> urutan;

}
