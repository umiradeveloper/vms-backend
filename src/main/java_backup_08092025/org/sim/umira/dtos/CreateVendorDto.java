package org.sim.umira.dtos;

import jakarta.validation.constraints.NotBlank;

public class CreateVendorDto {

    @NotBlank(message = "nama_perusahaan must be required")
    public String nama_perusahaan;

     @NotBlank(message = "id_kualifikasi_usaha must be required")
    public String id_kualifikasi_usaha;

    @NotBlank(message = "klasifikasi_usaha must be required")
    public String klasifikasi_usaha;

    @NotBlank(message = "alamat_perusahaan must be required")
    public String alamat_perusahaan;

    @NotBlank(message = "kategori must be required")
    public String kategori;

    @NotBlank(message = "spesialis must be required")
    public String spesialis;

    @NotBlank(message = "nama_pic must be required")
    public String nama_pic;

    @NotBlank(message = "email_pic must be required")
    public String email_pic;

    @NotBlank(message = "no_hp_pic must be required")
    public String no_hp_pic;

    @NotBlank(message = "nama_direktur must be required")
    public String nama_direktur;

    @NotBlank(message = "email_direktur must be required")
    public String email_direktur;

    @NotBlank(message = "no_hp_direktur must be required")
    public String no_hp_direktur;

    @NotBlank(message = "website must be required")
    public String website;

}
