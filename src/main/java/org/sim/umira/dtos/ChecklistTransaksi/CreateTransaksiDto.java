package org.sim.umira.dtos.ChecklistTransaksi;

import java.util.List;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateTransaksiDto {


     @FormParam("jenis_transaksi")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "jenis_transaksi must be required")
    public String jenis_transaksi;

     @FormParam("proyek")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "jenis_transaksi must be required")
    public String proyek;


    @FormParam("tempo_pembayaran_after_verified")
    @PartType(MediaType.TEXT_PLAIN)
    // @NotBlank(message = "jenis_transaksi must be required")
    public String tempo_pembayaran_after_verified;

    @FormParam("catatan")
    @PartType(MediaType.TEXT_PLAIN)
    public String catatan;

    @FormParam("kode_transaksi")
    @PartType(MediaType.TEXT_PLAIN)
    public String kode_transaksi;

    @FormParam("nama_transaksi")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "nama_transaksi must be required")
    @NotEmpty(message = "nama_transaksi must be required")
    public List<String> nama_transaksi;
    
    @FormParam("files")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    @NotNull(message = "dokumen files must be required")
    @NotEmpty(message = "dokumen files must be required")
    public List<FileUpload> files; 

    @FormParam("nilai")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "nilai_value must be required")
    @NotEmpty(message = "nilai_value must be required")
    public List<Integer> nilai_value;
}
