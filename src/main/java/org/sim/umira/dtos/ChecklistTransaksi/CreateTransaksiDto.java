package org.sim.umira.dtos.ChecklistTransaksi;

import java.util.List;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateTransaksiDto {


     @FormParam("jenis_transaksi")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "jenis_transaksi must be required")
    public String jenis_transaksi;

     @FormParam("proyek")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "proyek must be required")
    public String proyek;


    @FormParam("tempo_pembayaran_after_verified")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "Tempo must be required")
    public String tempo_pembayaran_after_verified;

    @FormParam("catatan")
    @PartType(MediaType.TEXT_PLAIN)
    public String catatan;

    @FormParam("kode_transaksi")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "kode_transaksi must be required")
    public String kode_transaksi;

    @FormParam("nama_transaksi")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "nama_transaksi wajib ada")
    @Size(min = 1, message = "nama_transaksi minimal 1 item")
    public List<
        @NotBlank(message = "nama_transaksi tidak boleh kosong")
        String
    > nama_transaksi;


    @FormParam("files")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    @NotNull(message = "files wajib ada")
    @Size(min = 1, message = "files minimal 1 item")
    public List<
        @NotNull(message = "file tidak boleh null")
        FileUpload
    > files;


    @FormParam("nilai")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "nilai wajib ada")
    @Size(min = 1, message = "nilai minimal 1 item")
    public List<
        @NotNull(message = "nilai tidak boleh null")
        @Positive(message = "nilai harus lebih dari 0")
        Integer
    > nilai_value;
}
