package org.sim.umira.dtos;




import java.time.LocalDateTime;



public class CreateUserDto {
    // @NotBlank(message = "Email is required")
    // @Email(message = "invalid email format")
    public String email;
    // @NotBlank(message = "Password is required")
    public String password;
    // @NotBlank(message = "nama_perusahaan is required")
    public String nama_perusahaan;

    public String username;
    // @NotBlank(message = "kode_branch is required")
    public String kode_branch;

    public String no_handphone;

    public String kode_role;

    public String approvedBy;

    public Integer isApproval;

    public LocalDateTime approvedAt;

    public String catatan;
}
