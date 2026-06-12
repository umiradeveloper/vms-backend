package org.sim.umira.dtos.SimAsset;

import java.time.LocalDateTime;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateMutasiAssetDto {
    public String id_mutasi_asset;

    // @FormParam("tanggal_mutasi")
    // @PartType(MediaType.TEXT_PLAIN)
    // public LocalDateTime tanggal_mutasi;

    // public LocalDateTime tanggal_penerimaan;

    @NotBlank(message = "id_asset be required")
    @FormParam("id_asset")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_asset;

     @NotBlank(message = "alasan_mutasi be required")
    @FormParam("alasan_mutasi")
    @PartType(MediaType.TEXT_PLAIN)
    public String alasan_mutasi;

     @NotBlank(message = "lokasi_asal be required")
    @FormParam("lokasi_asal")
    @PartType(MediaType.TEXT_PLAIN)
    public String lokasi_asal;

     @NotBlank(message = "lokasi_tujuan be required")
    @FormParam("lokasi_tujuan")
    @PartType(MediaType.TEXT_PLAIN)
    public String lokasi_tujuan;

    @NotBlank(message = "pic_tujuan be required")
    @FormParam("pic_tujuan")
    @PartType(MediaType.TEXT_PLAIN)
    public String pic_tujuan;

    
    @FormParam("dokumen_referensi")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public FileUpload dokumen_referensi;
}
