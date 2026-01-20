package org.sim.umira.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterDto {
    @NotBlank(message = "Email is required")
    @Email(message = "invalid email format")
    public String email;
    @NotBlank(message = "Password is required")
    public String password;
    @NotBlank(message = "nama_perusahaan is required")
    public String nama_perusahaan;
    @NotBlank(message = "no_hp is required")
    public String no_hp;
    @NotBlank(message = "kode_branch is required")
    public String kode_branch;

    public String kode_role;
}
