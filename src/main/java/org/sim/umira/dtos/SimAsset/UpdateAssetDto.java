package org.sim.umira.dtos.SimAsset;

import java.math.BigInteger;
import java.time.LocalDate;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class UpdateAssetDto {
    @FormParam("id_asset")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_asset;


    @NotBlank(message = "id_user be required")
    @FormParam("id_user")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_user;


    @NotBlank(message = "kode_asset be required")
    @FormParam("kode_asset")
    @PartType(MediaType.TEXT_PLAIN)
    public String kode_asset;

    @NotBlank(message = "nama_asset be required")
    @FormParam("nama_asset")
    @PartType(MediaType.TEXT_PLAIN)
    public String nama_asset;


    @NotBlank(message = "kategori be required")
    @FormParam("kategori")
    @PartType(MediaType.TEXT_PLAIN)
    public String kategori;

    @NotBlank(message = "lokasi be required")
    @FormParam("lokasi")
    @PartType(MediaType.TEXT_PLAIN)
    public String lokasi;


    @NotNull(message = "nama_asset be required")
    @FormParam("nilai_perolehan")
    @PartType(MediaType.TEXT_PLAIN)
    public BigInteger nilai_perolehan;

    @NotNull(message = "tanggal_perolehan be required")
    @FormParam("tanggal_perolehan")
    @PartType(MediaType.TEXT_PLAIN)
    public LocalDate tanggal_perolehan;
    

    @NotBlank(message = "kondisi be required")
    @FormParam("kondisi")
    @PartType(MediaType.TEXT_PLAIN)
    public String kondisi;

    @NotBlank(message = "status_asset be required")
    @FormParam("status_asset")
    @PartType(MediaType.TEXT_PLAIN)
    public String status_asset;


    @NotBlank(message = "deskripsi be required")
    @FormParam("deskripsi_asset")
    @PartType(MediaType.TEXT_PLAIN)
    public String deskripsi_asset;

    // @NotNull(message = "foto must be required")
    @FormParam("foto")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public FileUpload foto;


    @NotNull(message = "nilai_saat_ini be required")
    @FormParam("nilai_saat_ini")
    @PartType(MediaType.TEXT_PLAIN)
    public BigInteger nilai_saat_ini;

    @NotNull(message = "umur_ekonomis be required")
    @FormParam("umur_ekonomis")
    @PartType(MediaType.TEXT_PLAIN)
    public Integer umur_ekonomis;
}
