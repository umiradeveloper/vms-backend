package org.sim.umira.dtos.M;

import jakarta.validation.constraints.NotBlank;

public class UpdateUserMDto {
    public String id_user;
    @NotBlank(message = "nama must be Required")
    public String nama;
    @NotBlank(message = "username must be Required")
    public String username;
    @NotBlank(message = "email must be Required")
    public String email;
    @NotBlank(message = "No Handphone must be Required")
    public String no_hp;
    
    public String password;

}
