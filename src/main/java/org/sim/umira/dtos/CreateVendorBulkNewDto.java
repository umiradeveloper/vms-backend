package org.sim.umira.dtos;

import java.util.List;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateVendorBulkNewDto {
    
    @FormParam("nama_perusahaan")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "nama_perusahaan must be required")
    public String nama_perusahaan;

    @FormParam("id_kualifikasi_usaha")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "id_kualifikasi must be required")
    public String id_kualifikasi_usaha;

    @FormParam("klasifikasi_usaha")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "klasifikasi_usaha must be required")
    public String klasifikasi_usaha;

    @FormParam("alamat_perusahaan")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "alamat_perusahaan must be required")
    public String alamat_perusahaan;

    @FormParam("kategori")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "kategori must be required")
    public String kategori;

    @FormParam("spesialis")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "spesialis must be required")
    public String spesialis;

    @FormParam("nama_pic")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "nama_pic must be required")
    public String nama_pic;

    @FormParam("email_pic")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "email must be required")
    public String email_pic;

    @FormParam("no_hp_pic")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "no_hp_pic must be required")
    public String no_hp_pic;

    @FormParam("nama_direktur")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "nama_direktur must be required")
    public String nama_direktur;

    @FormParam("email_direktur")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "email_direktur must be required")
    public String email_direktur;

    @FormParam("no_hp_direktur")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "no_hp_direktur must be required")
    public String no_hp_direktur;

    @FormParam("website")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "website must be required")
    public String website;


    @FormParam("id_vendor")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_vendor;

    @FormParam("id_dokumen")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "dokumen must be required")
    @NotEmpty(message = "dokumen must be required")
    public List<String> id_dokumen;

    @FormParam("files")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    @NotNull(message = "dokumen files must be required")
    @NotEmpty(message = "dokumen files must be required")
    public List<FileUpload> files; 
}
