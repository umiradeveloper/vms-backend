package org.sim.umira.dtos.M;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class UpdateUserMDto {

    @FormParam("id_user")
    public String id_user;
    @FormParam("nama")
    @NotBlank(message = "nama must be Required")
    public String nama;
    @FormParam("username")
    @NotBlank(message = "username must be Required")
    public String username;
    @FormParam("email")
    @NotBlank(message = "email must be Required")
    public String email;
    @FormParam("no_hp")
    @NotBlank(message = "No Handphone must be Required")
    public String no_hp;
    
    @FormParam("password")
    public String password;


    @FormParam("foto_profile")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public FileUpload foto_profile;



}
