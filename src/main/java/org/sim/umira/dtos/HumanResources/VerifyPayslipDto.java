package org.sim.umira.dtos.HumanResources;

import jakarta.validation.constraints.NotBlank;

public class VerifyPayslipDto {

    @NotBlank(message = "harus di isi")
    public String tahun;
    @NotBlank(message = "harus di isi")
    public String bulan;
    @NotBlank(message = "harus di isi")
    public String password;

}
