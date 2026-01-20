package org.sim.umira.dtos;

import jakarta.validation.constraints.NotBlank;

public class CreateKualifikasiVendorDto {
    @NotBlank(message = "nama_kualifikasi must be required")
    public String nama_kualifikasi;

}
