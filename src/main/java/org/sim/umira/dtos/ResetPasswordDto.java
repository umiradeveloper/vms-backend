package org.sim.umira.dtos;

import jakarta.validation.constraints.NotBlank;

public class ResetPasswordDto {
    public String token;

    @NotBlank(message = "Password is required")
    public String password;
}
