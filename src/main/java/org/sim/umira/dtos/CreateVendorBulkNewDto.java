package org.sim.umira.dtos;

import java.util.List;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateVendorBulkNewDto {
    
    @FormParam("nama_perusahaan")
    @PartType(MediaType.TEXT_PLAIN)
    public String nama_perusahaan;

    @FormParam("id_kualifikasi_usaha")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_kualifikasi_usaha;

    @FormParam("klasifikasi_usaha")
    @PartType(MediaType.TEXT_PLAIN)
    public String klasifikasi_usaha;

    @FormParam("alamat_perusahaan")
    @PartType(MediaType.TEXT_PLAIN)
    public String alamat_perusahaan;

    @FormParam("kategori")
    @PartType(MediaType.TEXT_PLAIN)
    public String kategori;

    @FormParam("spesialis")
    @PartType(MediaType.TEXT_PLAIN)
    public String spesialis;

    @FormParam("nama_pic")
    @PartType(MediaType.TEXT_PLAIN)
    public String nama_pic;

    @FormParam("email_pic")
    @PartType(MediaType.TEXT_PLAIN)
    public String email_pic;

    @FormParam("no_hp_pic")
    @PartType(MediaType.TEXT_PLAIN)
    public String no_hp_pic;

    @FormParam("nama_direktur")
    @PartType(MediaType.TEXT_PLAIN)
    public String nama_direktur;

    @FormParam("email_direktur")
    @PartType(MediaType.TEXT_PLAIN)
    public String email_direktur;

    @FormParam("no_hp_direktur")
    @PartType(MediaType.TEXT_PLAIN)
    public String no_hp_direktur;

    @FormParam("website")
    @PartType(MediaType.TEXT_PLAIN)
    public String website;


    @FormParam("id_vendor")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_vendor;

    @FormParam("id_dokumen")
    @PartType(MediaType.TEXT_PLAIN)
    public List<String> id_dokumen;

    @FormParam("files")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public List<FileUpload> files; 
}
