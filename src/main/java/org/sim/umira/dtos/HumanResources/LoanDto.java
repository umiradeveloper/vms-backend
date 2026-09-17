package org.sim.umira.dtos.HumanResources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoanDto {

    @NotBlank(message = "id_employee harus di isi")
    public String id_employee;

    @NotNull(message = "total_pinjaman harus di isi")
    public Integer total_pinjaman;

    @NotNull(message = "jumlah_cicilan harus di isi")
    public Integer jumlah_cicilan;

    @NotBlank(message = "bulan_awal harus di isi")
    public String bulan_awal;

    @NotBlank(message = "tahun_awal harus di isi")
    public String tahun_awal;

    @NotBlank(message = "bulan_akhir harus di isi")
    public String bulan_akhir;

    @NotBlank(message = "tahun_akhir harus di isi")
    public String tahun_akhir;
    
}
