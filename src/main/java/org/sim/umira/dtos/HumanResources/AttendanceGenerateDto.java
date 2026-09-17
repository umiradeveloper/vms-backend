package org.sim.umira.dtos.HumanResources;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class AttendanceGenerateDto {
    @NotEmpty(message = "id_employee wajib diisi")
    public List<String> id_employee;

    @NotBlank(message = "month wajib diisi")
    public String month;
    @NotEmpty(message = "year wajib diisi")
    public String year;
    @NotEmpty(message = "jam masuk wajib diisi")
    public String jam_masuk;
    @NotEmpty(message = "jam keluar wajib diisi")
    public String jam_keluar;
    @NotEmpty(message = "status wajib diisi")
    public String status;
    @NotEmpty(message = "keterangan wajib diisi")
    public String keterangan;
}
