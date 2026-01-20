package org.sim.umira.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginDto {

    @NotBlank(message = "email is required")
    // @Email(message = "Invalid email format")
    public String email;

    @NotBlank(message = "Password is required")
    public String password;
}
