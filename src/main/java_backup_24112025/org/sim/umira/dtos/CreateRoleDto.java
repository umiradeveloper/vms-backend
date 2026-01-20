package org.sim.umira.dtos;

import jakarta.validation.constraints.NotBlank;

public class CreateRoleDto {
    @NotBlank(message = "nama_role must be required")
    public String nama_role;
    @NotBlank(message = "kode_role must be required")
    public String kode_role;
}
