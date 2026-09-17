package org.sim.umira.dtos.HumanResources;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OvertimeDto {
    // @NotBlank(message = "id_lembur harus di isi")
    public String id_lembur;
    // @NotBlank(message = "id_employee harus di isi")
    public String id_employee;
    @NotNull(message = "tanggal harus di isi")
    public LocalDate tanggal;
    @NotBlank(message = "jam_mulai harus di isi")
    public String jam_mulai;
    @NotBlank(message = "jam_selesai harus di isi")
    public String jam_selesai;
    @NotBlank(message = "alasan harus di isi")
    public String alasan;
    @NotBlank(message = "dokumen harus di isi")
    public String dokumen;

}
